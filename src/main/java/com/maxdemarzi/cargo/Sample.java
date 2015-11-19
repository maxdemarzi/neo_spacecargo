package com.maxdemarzi.cargo;

import org.codehaus.jackson.map.ObjectMapper;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Path("/sample")
public class Sample {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @GET
    @Path("/createtestdata")
    public Response createTestData(@Context GraphDatabaseService db) throws IOException {
        try (Transaction tx = db.beginTx()) {
            db.execute(TheVerse.THE_VERSE);
            db.execute(TheVerse.TEST_DATA);
            tx.success();
        }
        Map<String, String> results = new HashMap<String, String>() {{
            put("testdata", "created");
        }};
        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();

    }
}