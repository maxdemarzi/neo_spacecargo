package com.maxdemarzi.cargo;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.BranchState;

import java.util.Collections;
import java.util.Iterator;

public class RouteExpander implements PathExpander<Long> {
    private long arrival;
    private long stopTime;

    public RouteExpander(long arrival, long stopTime) {
        this.arrival = arrival;
        this.stopTime = System.currentTimeMillis() + stopTime;
    }

    @Override
    public Iterable<Relationship> expand(Path path, BranchState<Long> branchState) {
        // Stop if we are over our time limit
        if (System.currentTimeMillis() < stopTime) {

            // We will start at a Moon or Planet, but need to get to a station
            if ( path.endNode().hasLabel(Labels.Moon) || path.endNode().hasLabel(Labels.Planet)) {
                return path.endNode().getRelationships(
                        Direction.INCOMING, RelationshipTypes.LOCATED_AT);
            }

            // When we reach a station, we need to continue to its dockings or other stations via transhipment.
            if ( path.endNode().hasLabel(Labels.Station)) {

                // If we transfered here, our new times should be our arrival + transit time
                if (path.lastRelationship().isType(RelationshipTypes.TRANSHIPMENT)) {
                    Node lastDocking = null;
                    Iterator<Node> iterator = path.reverseNodes().iterator();
                    while (iterator.hasNext()) {
                        lastDocking = iterator.next();
                        if (lastDocking.hasLabel(Labels.Docking)) { break;}
                    }

                    Long previousArrival = (long)lastDocking.getProperty("arrival");
                    previousArrival += (long)path.lastRelationship().getProperty("transit_time");
                    branchState.setState(previousArrival);
                }

                return path.endNode().getRelationships(
                        RelationshipTypes.HAS_DOCKING,
                        RelationshipTypes.TRANSHIPMENT);
            }

            // Ignore any Dockings that arrive too late.
            Long lastArrival = (long)path.endNode().getProperty("arrival", arrival);
            if ( lastArrival <= arrival ) {



                // Ignore any Transits that we cannot catch due to time.
                Long lastDeparture = branchState.getState();
                Long departure = (Long) path.endNode().getProperty("departure", lastDeparture);

                if (departure >= lastDeparture) {
                    branchState.setState(departure);
                    return path.endNode().getRelationships(
                            RelationshipTypes.CONNECTED_TO,
                            RelationshipTypes.HAS_DOCKING);
                }
            }
        }
        return Collections.emptyList();
    }

    @Override
    public PathExpander reverse() {
        return new PathExpander<Long>() {
            @Override
            public Iterable<Relationship> expand(Path path, BranchState<Long> branchState) {
                // Stop if we are over our time limit
                if (System.currentTimeMillis() < stopTime) {

                    // We will end at a Moon or Planet, but need to get to a station
                    if ( path.endNode().hasLabel(Labels.Moon) || path.endNode().hasLabel(Labels.Planet)) {
                        return path.endNode().getRelationships(
                                Direction.INCOMING, RelationshipTypes.LOCATED_AT);
                    }

                    // When we reach a station, we need to continue to its dockings or other stations via transhipment.
                    if ( path.endNode().hasLabel(Labels.Station)) {
                        return path.endNode().getRelationships(
                                RelationshipTypes.HAS_DOCKING,
                                RelationshipTypes.TRANSHIPMENT);
                    }

                    // Ignore any Dockings that arrive too late.
                    Long lastArrival = (long)path.endNode().getProperty("arrival", arrival);
                    if ( lastArrival <= arrival ) {

                        // Ignore any Transits that we cannot catch due to time.
                        Long lastDeparture = branchState.getState();
                        Long departure = (Long) path.endNode().getProperty("departure", lastDeparture);

                        if (departure <= lastDeparture) {
                            branchState.setState(departure);
                            return path.endNode().getRelationships(
                                    RelationshipTypes.CONNECTED_TO,
                                    RelationshipTypes.TRANSHIPMENT);
                        }
                    }
                }
                return Collections.emptyList();
            }

            @Override
            public PathExpander reverse() {
                return null;
            }
        };
    }
}