# neo_spacecargo
Sample Bi-Directional Traversal Extension


# Instructions

1. Build it:

        mvn clean package

2. Copy target/cargo-1.0.jar to the plugins/ directory of your Neo4j server.

3. Configure Neo4j by adding a line to conf/neo4j-server.properties:

        org.neo4j.server.thirdparty_jaxrs_classes=com.maxdemarzi.cargo=/v1
       
4. Start Neo4j server.

5. Check that it is installed correctly over HTTP:

        :GET /v1/service/helloworld
        
6. Run the migration:
        
        :GET /v1/service/migrate

7. Create test data:
        
        :GET /v1/sample/createtestdata        
        
8. Query the database:
        
        :POST /v1/service/query {"from":"Londinium", "to":"Sihnon", "departure_date":"11/06/2215", "arrival_date": "12/04/2215" }
