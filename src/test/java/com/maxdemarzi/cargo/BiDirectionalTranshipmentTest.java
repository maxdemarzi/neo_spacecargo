package com.maxdemarzi.cargo;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.harness.junit.Neo4jRule;
import org.neo4j.test.server.HTTP;

import java.util.ArrayList;
import java.util.HashMap;

import static com.maxdemarzi.cargo.TheVerse.THE_VERSE;
import static org.junit.Assert.assertArrayEquals;

public class BiDirectionalTranshipmentTest {
    @Rule
    public Neo4jRule neo4j = new Neo4jRule()
            .withFixture(THE_VERSE)
            .withFixture(TEST_DATA)
            .withExtension("/v1", Service.class);

    @Test
    public void shouldFind2HopRoute() {
        HTTP.Response response = HTTP.POST(neo4j.httpURI().resolve("/v1/service/query").toString(),
                QUERY_MAP);
        ArrayList actual = response.content();
        assertArrayEquals(EXPECTED.toArray(), actual.toArray());
    }

    public static final String TEST_DATA =
            new StringBuilder()
                    // Londinium to Ariopolis via Sihnon
                    // VALID: 12-71-9, 12-71-67, 12-101-94
                    //        12-71-t-101-94
                    //        12-101-t-71-9
                    //        12-101-t-71-67
                    // INVALID: 12-71-t-46-67
                    .append("")
                    .append("CREATE (d1:Docking {code:'d1', departure_date:'11/06/2215 12:00 PM', departure:7758158400, arrival_date:'11/05/2215 12:00 PM', arrival:7758072000})")
                    .append("CREATE (d2:Docking {code:'d2', departure_date:'11/08/2215 12:00 PM', departure:7758331200, arrival_date:'11/07/2215 12:00 PM', arrival:7758244800})")
                    .append("CREATE (d3:Docking {code:'d3', departure_date:'11/10/2215 12:00 PM', departure:7758505200, arrival_date:'11/09/2215 12:00 PM', arrival:7758418800})")
                    .append("CREATE (d4:Docking {code:'d4', departure_date:'11/10/2215 12:00 PM', departure:7758505200, arrival_date:'11/09/2215 3:20 PM', arrival:7758429600})")

                    .append("CREATE (d5:Docking {code:'d5', departure_date:'11/06/2215 12:00 PM', departure:7758158400, arrival_date:'11/05/2215 12:00 PM', arrival:7758072000})")
                    .append("CREATE (d6:Docking {code:'d6', departure_date:'11/08/2215 12:00 PM', departure:7758331200, arrival_date:'11/07/2215 12:00 PM', arrival:7758244800})")
                    .append("CREATE (d7:Docking {code:'d7', departure_date:'11/10/2215 12:00 PM', departure:7758504000, arrival_date:'11/09/2215 3:00 PM', arrival:7758428400})")

                    .append("CREATE (d8:Docking {code:'d8', departure_date:'11/08/2215 11:59 AM', departure:7758331140, arrival_date:'11/07/2215 12:00 PM', arrival:7758244800})")
                    .append("CREATE (d9:Docking {code:'d9', departure_date:'11/10/2215 12:00 PM', departure:7758505200, arrival_date:'11/09/2215 3:20 PM', arrival:7758429600})")

                    .append("MERGE (st9:Station {name:'Aeddin'})")
                    .append("MERGE (st12:Station {name:'Aldik'})")
                    .append("MERGE (st46:Station {name:'Hahyil'})")
                    .append("MERGE (st67:Station {name:'Ladistier'})")
                    .append("MERGE (st71:Station {name:'Lirerim'})")
                    .append("MERGE (st94:Station {name:'Romi'})")
                    .append("MERGE (st101:Station {name:'Torvi'})")

                    .append("MERGE (st71)-[:TRANSHIPMENT {transit_time:14400}]->(st101)")
                    .append("MERGE (st71)-[:TRANSHIPMENT {transit_time:86400}]->(st46)")

                    .append("MERGE (st12)-[:HAS_DOCKING]->(d1)")
                    .append("MERGE (st71)-[:HAS_DOCKING]->(d2)")
                    .append("MERGE (st9)-[:HAS_DOCKING]->(d3)")
                    .append("MERGE (st67)-[:HAS_DOCKING]->(d4)")

                    .append("MERGE (st12)-[:HAS_DOCKING]->(d5)")
                    .append("MERGE (st101)-[:HAS_DOCKING]->(d6)")
                    .append("MERGE (st94)-[:HAS_DOCKING]->(d7)")

                    .append("MERGE (st46)-[:HAS_DOCKING]->(d8)")
                    .append("MERGE (st67)-[:HAS_DOCKING]->(d9)")

                    .append("CREATE (d1)-[:CONNECTED_TO {transit_time:86400}]->(d2)")
                    .append("CREATE (d2)-[:CONNECTED_TO {transit_time:87600}]->(d3)")
                    .append("CREATE (d2)-[:CONNECTED_TO {transit_time:98400}]->(d4)")

                    .append("CREATE (d5)-[:CONNECTED_TO {transit_time:86400}]->(d6)")
                    .append("CREATE (d6)-[:CONNECTED_TO {transit_time:87600}]->(d7)")

                    .append("CREATE (d8)-[:CONNECTED_TO {transit_time:86400}]->(d9)")

                    .toString();

    public static HashMap<String, Object> QUERY_MAP = new HashMap<String, Object>(){{
        put("from", "Londinium");
        put("to", "Ariopolis");
        put("departure_date", "11/06/2215");
        put("arrival_date", "11/09/2215");
    }};

    static HashMap<String, Object> LONDINIUM_MAP = new HashMap<String, Object>(){{
        put("name","Londinium");
        put("label", "Planet");
    }};

    static HashMap<String, Object> ALDIK_MAP = new HashMap<String, Object>(){{
        put("name", "Aldik");
        put("label", "Station");
    }};

    static HashMap<String, Object> D1_MAP = new HashMap<String, Object>(){{
        put("code","d1");
        put("departure_date", "11/06/2215 12:00 PM");
        put("departure", 7758158400L);
        put("arrival_date", "11/05/2215 12:00 PM");
        put("arrival", 7758072000L);
        put("label", "Docking");
    }};

    static HashMap<String, Object> D2_MAP = new HashMap<String, Object>(){{
        put("code","d2");
        put("departure_date", "11/08/2215 12:00 PM");
        put("departure", 7758331200L);
        put("arrival_date", "11/07/2215 12:00 PM");
        put("arrival", 7758244800L);
        put("label", "Docking");
    }};

    static HashMap<String, Object> D3_MAP = new HashMap<String, Object>(){{
        put("code","d3");
        put("departure_date", "11/10/2215 12:00 PM");
        put("departure", 7758505200L);
        put("arrival_date", "11/09/2215 12:00 PM");
        put("arrival", 7758418800L);
        put("label", "Docking");
    }};

    static HashMap<String, Object> D4_MAP = new HashMap<String, Object>(){{
        put("code","d4");
        put("departure_date", "11/10/2215 12:00 PM");
        put("departure", 7758505200L);
        put("arrival_date", "11/09/2215 3:20 PM");
        put("arrival", 7758429600L);
        put("label", "Docking");
    }};

    static HashMap<String, Object> D5_MAP = new HashMap<String, Object>(){{
        put("code","d5");
        put("departure_date", "11/06/2215 12:00 PM");
        put("departure", 7758158400L);
        put("arrival_date", "11/05/2215 12:00 PM");
        put("arrival", 7758072000L);
        put("label", "Docking");
    }};

    static HashMap<String, Object> D6_MAP = new HashMap<String, Object>(){{
        put("code","d6");
        put("departure_date", "11/08/2215 12:00 PM");
        put("departure", 7758331200L);
        put("arrival_date", "11/07/2215 12:00 PM");
        put("arrival", 7758244800L);
        put("label", "Docking");
    }};

    static HashMap<String, Object> D7_MAP = new HashMap<String, Object>(){{
        put("code","d7");
        put("departure_date", "11/10/2215 12:00 PM");
        put("departure", 7758504000L);
        put("arrival_date", "11/09/2215 3:00 PM");
        put("arrival", 7758428400L);
        put("label", "Docking");
    }};

    static HashMap<String, Object> AEDDIN_MAP = new HashMap<String, Object>(){{
        put("name","Aeddin");
        put("label", "Station");
    }};

    static HashMap<String, Object> ROMI_MAP = new HashMap<String, Object>(){{
        put("name","Romi");
        put("label", "Station");
    }};

    static HashMap<String, Object> TORVI_MAP = new HashMap<String, Object>(){{
        put("name","Torvi");
        put("label", "Station");
    }};

    static HashMap<String, Object> LIRERIM_MAP = new HashMap<String, Object>(){{
        put("name","Lirerim");
        put("label", "Station");
    }};

    static HashMap<String, Object> LADISTIER_MAP = new HashMap<String, Object>(){{
        put("name","Ladistier");
        put("label", "Station");
    }};

    static HashMap<String, Object> ARIOPOLIS_MAP = new HashMap<String, Object>(){{
        put("name","Ariopolis");
        put("label", "Moon");
    }};

    static ArrayList<HashMap> STEP_LIST1 = new ArrayList<HashMap>(){{
        add(LONDINIUM_MAP);
        add(ALDIK_MAP);
        add(D1_MAP);
        add(D2_MAP);
        add(D3_MAP);
        add(AEDDIN_MAP);
        add(ARIOPOLIS_MAP);
    }};

    static ArrayList<HashMap> STEP_LIST2 = new ArrayList<HashMap>(){{
        add(LONDINIUM_MAP);
        add(ALDIK_MAP);
        add(D5_MAP);
        add(D6_MAP);
        add(D7_MAP);
        add(ROMI_MAP);
        add(ARIOPOLIS_MAP);
    }};

    static ArrayList<HashMap> STEP_LIST3 = new ArrayList<HashMap>(){{
        add(LONDINIUM_MAP);
        add(ALDIK_MAP);
        add(D1_MAP);
        add(D2_MAP);
        add(D4_MAP);
        add(LADISTIER_MAP);
        add(ARIOPOLIS_MAP);
    }};

    static ArrayList<HashMap> STEP_LIST4 = new ArrayList<HashMap>(){{
        add(LONDINIUM_MAP);
        add(ALDIK_MAP);
        add(D5_MAP);
        add(D6_MAP);
        add(TORVI_MAP);
        add(LIRERIM_MAP);
        add(D2_MAP);
        add(D3_MAP);
        add(AEDDIN_MAP);
        add(ARIOPOLIS_MAP);
    }};

    static ArrayList<HashMap> STEP_LIST5 = new ArrayList<HashMap>(){{
        add(LONDINIUM_MAP);
        add(ALDIK_MAP);
        add(D1_MAP);
        add(D2_MAP);
        add(LIRERIM_MAP);
        add(TORVI_MAP);
        add(D6_MAP);
        add(D7_MAP);
        add(ROMI_MAP);
        add(ARIOPOLIS_MAP);
    }};

    static ArrayList<HashMap> STEP_LIST6 = new ArrayList<HashMap>(){{
        add(LONDINIUM_MAP);
        add(ALDIK_MAP);
        add(D5_MAP);
        add(D6_MAP);
        add(TORVI_MAP);
        add(LIRERIM_MAP);
        add(D2_MAP);
        add(D4_MAP);
        add(LADISTIER_MAP);
        add(ARIOPOLIS_MAP);
    }};

    static HashMap<String, Object> ANSWER_MAP1 = new HashMap<String, Object>(){{
        put("steps", STEP_LIST1);
        put("departure_date", "11/06/2215 12:00 PM");
        put("arrival_date", "11/09/2215 12:00 PM");
        put("travel_time", 260400);
    }};

    static HashMap<String, Object> ANSWER_MAP2 = new HashMap<String, Object>(){{
        put("steps", STEP_LIST2);
        put("departure_date", "11/06/2215 12:00 PM");
        put("arrival_date", "11/09/2215 3:00 PM");
        put("travel_time", 270000);
    }};

    static HashMap<String, Object> ANSWER_MAP3 = new HashMap<String, Object>(){{
        put("steps", STEP_LIST3);
        put("departure_date", "11/06/2215 12:00 PM");
        put("arrival_date", "11/09/2215 3:20 PM");
        put("travel_time", 271200);
    }};

    static HashMap<String, Object> ANSWER_MAP4 = new HashMap<String, Object>(){{
        put("steps", STEP_LIST4);
        put("departure_date", "11/06/2215 12:00 PM");
        put("arrival_date", "11/09/2215 12:00 PM");
        put("travel_time", 260400);
    }};

    static HashMap<String, Object> ANSWER_MAP5 = new HashMap<String, Object>(){{
        put("steps", STEP_LIST5);
        put("departure_date", "11/06/2215 12:00 PM");
        put("arrival_date", "11/09/2215 3:00 PM");
        put("travel_time", 270000);
    }};

    static HashMap<String, Object> ANSWER_MAP6 = new HashMap<String, Object>(){{
        put("steps", STEP_LIST6);
        put("departure_date", "11/06/2215 12:00 PM");
        put("arrival_date", "11/09/2215 3:20 PM");
        put("travel_time", 271200);
    }};

    public static ArrayList<HashMap> EXPECTED = new ArrayList<HashMap>(){{
        add(ANSWER_MAP1);
        add(ANSWER_MAP2);
        add(ANSWER_MAP3);
        add(ANSWER_MAP4);
        add(ANSWER_MAP5);
        add(ANSWER_MAP6);
    }};
}
