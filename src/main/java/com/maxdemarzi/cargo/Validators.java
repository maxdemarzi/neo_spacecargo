package com.maxdemarzi.cargo;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;

import static com.maxdemarzi.cargo.Service.DEFAULT_RECORD_LIMIT;
import static com.maxdemarzi.cargo.Service.DEFAULT_TIME_LIMIT;

public class Validators {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static HashMap getValidQueryInput(String body) throws IOException {
        HashMap input;

        // Parse the input
        try {
            input = objectMapper.readValue(body, HashMap.class);
        } catch (Exceptions e) {
            throw Exceptions.invalidInput;
        }

        if (!input.containsKey("from")) {
            throw Exceptions.missingFromParameter;
        }
        if (!input.containsKey("to")) {
            throw Exceptions.missingToParameter;
        }
        if (!input.containsKey("departure_date")) {
            throw Exceptions.missingDepartureDateParameter;
        }
        if (!input.containsKey("arrival_date")) {
            throw Exceptions.missingArrivalDateParameter;
        }
        if (!input.containsKey("record_limit")) {
            input.put("record_limit", DEFAULT_RECORD_LIMIT);
        }
        if (!input.containsKey("time_limit")) {
            input.put("time_limit", DEFAULT_TIME_LIMIT);
        } else {
            // Avoid Integer vs Long nonsense
            input.put("time_limit", ((Number)input.get("time_limit")).longValue());
        }
        if (input.get("from") == "") {
            throw Exceptions.invalidFromParameter;
        }
        if (input.get("to") == "") {
            throw Exceptions.invalidToParameter;
        }
        if (input.get("departure_date") == "") {
            throw Exceptions.invalidDepartureDateParameter;
        }
        if (input.get("arrival_date") == "") {
            throw Exceptions.invalidArrivalDateParameter;
        }

        try {
            TemporalAccessor departure_date = DateTimeFormatter.ofPattern("MM/dd/yyyy").parse((String) input.get("departure_date"));
            LocalDate departure_ldt = LocalDate.from(departure_date);
            input.put("departure_long", departure_ldt.atStartOfDay().toEpochSecond(ZoneOffset.UTC));
        } catch (Exception e) {
            throw Exceptions.invalidDepartureDateParameter;
        }
        try {
            TemporalAccessor arrival_date = DateTimeFormatter.ofPattern("MM/dd/yyyy").parse((String) input.get("arrival_date"));
            LocalDate arrival_ldt = LocalDate.from(arrival_date);
            input.put("arrival_long", arrival_ldt.atTime(23,59,59).toEpochSecond(ZoneOffset.UTC));
        } catch ( Exception e) {
            throw Exceptions.invalidArrivalDateParameter;
        }
        return input;
    }
}
