package com.maxdemarzi.cargo;

import org.codehaus.jackson.map.ObjectMapper;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.graphdb.traversal.BidirectionalTraversalDescription;
import org.neo4j.graphdb.traversal.InitialBranchState;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Path("/service")
public class Service {
    public static final Integer DEFAULT_RECORD_LIMIT = 25;
    public static final Long DEFAULT_TIME_LIMIT = 200000L; // 2000 ms
    private static final RouteComparator ROUTE_COMPARATOR = new RouteComparator();
    private static final ObjectMapper objectMapper = new ObjectMapper();


    @GET
    @Path("/helloworld")
    public Response helloWorld() throws IOException {
        Map<String, String> results = new HashMap<String,String>(){{
            put("hello","world");
        }};
        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();
    }

    @GET
    @Path("/migrate")
    public Response migrate(@Context GraphDatabaseService db) throws IOException {
        ArrayList<String> results = new ArrayList<>();
        boolean migrated;
        try (Transaction tx = db.beginTx()) {
            migrated = db.schema().getConstraints().iterator().hasNext();
        }

        if (migrated) {
            results.add("Already Migrated!");
        } else {
            // Perform Migration

            try (Transaction tx = db.beginTx()) {
                Schema schema = db.schema();
                schema.constraintFor(Labels.Star)
                        .assertPropertyIsUnique("name")
                        .create();
                schema.constraintFor(Labels.Planet)
                        .assertPropertyIsUnique("name")
                        .create();
                schema.constraintFor(Labels.Station)
                        .assertPropertyIsUnique("name")
                        .create();
                schema.constraintFor(Labels.Docking)
                        .assertPropertyIsUnique("code")
                        .create();
                tx.success();
            }
            // Wait for indexes to come online
            try (Transaction tx = db.beginTx()) {
                Schema schema = db.schema();
                schema.awaitIndexesOnline(1, TimeUnit.DAYS);
            }
            results.add("Migrated");
        }

        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();
    }

    /**
     * JSON formatted body requires:
     *  from: A Departure Point (ex. "Londinium")
     *  to: An Arrival Point (ex. "Sihnon")
     *  departure_date: A date in mm/dd/yyyy format (ex. 11/06/2015)
     *  arrival_date: A date in mm/dd/yyyy format (ex. 12/04/2015)
     *  record_limit: A number representing the maximum results to gather
     *  time_limit: A number representing the maximum time to gather results
     *
     *  This query will find as many path as possible in the given {time_limit},
     *  and then return the best {record_limit} paths found.
     *
     */
    @POST
    @Path("/query")
    public Response query(String body, @Context GraphDatabaseService db) throws IOException {
        ArrayList<HashMap> results = new ArrayList<>();

        // Validate our input or exit right away
        HashMap input = Validators.getValidQueryInput(body);

        int recordLimit = (Integer) input.get("record_limit");

        // Create our Expander which controls the traversal
        RouteExpander routeExpander = new RouteExpander(
                (long)input.get("arrival_long"),
                (long)input.get("time_limit"));

        try (Transaction tx = db.beginTx()) {
            Node startingPoint = db.findNode(Labels.Planet, "name", input.get("from"));
            if (startingPoint == null) {
                startingPoint = db.findNode(Labels.Moon, "name", input.get("from"));
            }

            Node endingPoint = db.findNode(Labels.Planet, "name", input.get("to"));
            if (endingPoint == null) {
                endingPoint = db.findNode(Labels.Moon, "name", input.get("to"));
            }

            // Bi-Directional
            InitialBranchState.State<Long> ibs = new InitialBranchState.State<>((Long)input.get("departure_long"), (Long)input.get("arrival_long"));

            TraversalDescription td = db.traversalDescription()
                    .breadthFirst()
                    .expand(routeExpander, ibs)
                    .uniqueness(Uniqueness.RELATIONSHIP_PATH);

            BidirectionalTraversalDescription bidirtd = db.bidirectionalTraversalDescription()
                    .mirroredSides(td);

            for (org.neo4j.graphdb.Path position : bidirtd.traverse(startingPoint, endingPoint)) {
                HashMap<String, Object> result = new HashMap<>();
                ArrayList<Map> steps = new ArrayList<>();
                for (Node node : position.nodes()) {
                    Map<String, Object> vsidInfo = node.getAllProperties();
                    vsidInfo.put("label", node.getLabels().iterator().next().name());
                    steps.add(vsidInfo);
                }

                result.put("steps", steps);
                result.put("departure_date", steps.get(2).get("departure_date"));
                result.put("arrival_date", steps.get(steps.size() - 3).get("arrival_date"));
                result.put("travel_time", (long)steps.get(steps.size() - 3).get("arrival")
                        - (long)steps.get(2).get("departure"));
                results.add(result);
            }
        }

        Collections.sort(results, ROUTE_COMPARATOR);

        return Response.ok().entity(
                objectMapper.writeValueAsString(
                        results.subList(0,
                                Math.min(results.size(), recordLimit)
                        ))).build();
    }

}
