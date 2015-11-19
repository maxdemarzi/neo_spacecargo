package com.maxdemarzi.cargo;

import org.neo4j.graphdb.RelationshipType;

public enum RelationshipTypes implements RelationshipType {
    ORBITS,
    LOCATED_AT,
    CONNECTED_TO,
    HAS_DOCKING,
    TRANSHIPMENT
}
