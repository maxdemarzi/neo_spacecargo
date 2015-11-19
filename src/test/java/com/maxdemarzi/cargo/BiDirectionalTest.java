package com.maxdemarzi.cargo;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.harness.junit.Neo4jRule;
import org.neo4j.test.server.HTTP;

import java.util.ArrayList;
import java.util.HashMap;

import static com.maxdemarzi.cargo.TheVerse.THE_VERSE;
import static org.junit.Assert.assertArrayEquals;

public class BiDirectionalTest {
    @Rule
    public Neo4jRule neo4j = new Neo4jRule()
            .withFixture(THE_VERSE)
            .withFixture(TEST_DATA)
            .withExtension("/v1", Service.class);

    @Test
    public void shouldFindDirectRoute() {
        HTTP.Response response = HTTP.POST(neo4j.httpURI().resolve("/v1/service/query").toString(),
                QUERY_MAP);
        ArrayList actual = response.content();
        assertArrayEquals(EXPECTED.toArray(), actual.toArray());
    }

    public static final String TEST_DATA =
            new StringBuilder()
                    // Londinium to Sihnon
                    .append("")
                    .append("CREATE (d1:Docking {code:'d1', departure_date:'11/06/2215 12:00 PM', departure:7758158400, arrival_date:'11/05/2215 12:00 PM', arrival:7758072000})")
                    .append("CREATE (d2:Docking {code:'d2', departure_date:'11/08/2215 12:00 PM', departure:7758331200, arrival_date:'11/07/2215 12:00 PM', arrival:7758244800})")
                    .append("MERGE (st12:Station {name:'Aldik'})")
                    .append("MERGE (st12)-[:HAS_DOCKING]->(d1)")
                    .append("MERGE (st71:Station {name:'Lirerim'})")
                    .append("MERGE (st71)-[:HAS_DOCKING]->(d2)")
                    .append("CREATE (d1)-[:CONNECTED_TO {transit_time:86400}]->(d2)")
                    .toString();

    public static HashMap<String, Object> QUERY_MAP = new HashMap<String, Object>(){{
        put("from", "Londinium");
        put("to", "Sihnon");
        put("departure_date", "11/06/2215");
        put("arrival_date", "11/07/2215");
    }};

    static HashMap<String, Object> STEP1_MAP = new HashMap<String, Object>(){{
        put("name","Londinium");
        put("label", "Planet");
    }};

    static HashMap<String, Object> STEP2_MAP = new HashMap<String, Object>(){{
        put("name", "Aldik");
        put("label", "Station");
    }};

    static HashMap<String, Object> STEP3_MAP = new HashMap<String, Object>(){{
        put("code","d1");
        put("departure_date", "11/06/2215 12:00 PM");
        put("departure", 7758158400L);
        put("arrival_date", "11/05/2215 12:00 PM");
        put("arrival", 7758072000L);
        put("label", "Docking");
    }};

    static HashMap<String, Object> STEP4_MAP = new HashMap<String, Object>(){{
        put("code","d2");
        put("departure_date", "11/08/2215 12:00 PM");
        put("departure", 7758331200L);
        put("arrival_date", "11/07/2215 12:00 PM");
        put("arrival", 7758244800L);
        put("label", "Docking");
    }};

    static HashMap<String, Object> STEP5_MAP = new HashMap<String, Object>(){{
        put("name","Lirerim");
        put("label", "Station");
    }};

    static HashMap<String, Object> STEP6_MAP = new HashMap<String, Object>(){{
        put("name","Sihnon");
        put("label", "Planet");
    }};

    static ArrayList<HashMap> STEP_LIST1 = new ArrayList<HashMap>(){{
        add(STEP1_MAP);
        add(STEP2_MAP);
        add(STEP3_MAP);
        add(STEP4_MAP);
        add(STEP5_MAP);
        add(STEP6_MAP);
    }};

    static HashMap<String, Object> ANSWER_MAP1 = new HashMap<String, Object>(){{
        put("steps", STEP_LIST1);
        put("departure_date", "11/06/2215 12:00 PM");
        put("arrival_date", "11/07/2215 12:00 PM");
        put("travel_time", 86400);
    }};

    public static ArrayList<HashMap> EXPECTED = new ArrayList<HashMap>(){{
        add(ANSWER_MAP1);
    }};
}
