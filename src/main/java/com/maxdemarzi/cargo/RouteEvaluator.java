package com.maxdemarzi.cargo;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;

public class RouteEvaluator implements Evaluator {
    private long departure;
    private long arrival;

    public RouteEvaluator(Long departure, Long arrival) {
        this.departure = departure;
        this.arrival = arrival;
    }

    @Override
    public Evaluation evaluate(Path path) {
        for (Node node: path.nodes()) {
            Long nodeDeparture = (Long)node.getProperty("departure", departure);
            Long nodeArrival = (Long)node.getProperty("arrival", arrival);

            if (nodeDeparture < departure || nodeArrival > arrival) {
                return Evaluation.EXCLUDE_AND_PRUNE;
            }
        }
        return Evaluation.INCLUDE_AND_CONTINUE;
    }
}
