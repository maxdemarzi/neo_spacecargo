package com.maxdemarzi.cargo;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class Exceptions  extends WebApplicationException {

    public Exceptions(int code, String error)  {
        super(new Throwable(error), Response.status(code)
                .entity("{\"error\":\"" + error + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build());

    }

    public static Exceptions invalidInput = new Exceptions(400, "Invalid Input");

    public static Exceptions missingFromParameter = new Exceptions(400, "Missing from Parameter.");
    public static Exceptions invalidFromParameter = new Exceptions(400, "Invalid from Parameter.");

    public static Exceptions missingToParameter = new Exceptions(400, "Missing to Parameter.");
    public static Exceptions invalidToParameter = new Exceptions(400, "Invalid to Parameter.");

    public static Exceptions missingDepartureDateParameter = new Exceptions(400, "Missing departure_date Parameter.");
    public static Exceptions invalidDepartureDateParameter = new Exceptions(400, "Invalid departure_date Parameter.");

    public static Exceptions missingArrivalDateParameter = new Exceptions(400, "Missing arrival_date Parameter.");
    public static Exceptions invalidArrivalDateParameter = new Exceptions(400, "Invalid arrival_date Parameter.");

}
