package com.maxdemarzi.cargo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class RouteComparator  <T extends Comparable<T>> implements Comparator<HashMap> {

    @Override
    public int compare(HashMap route1, HashMap route2) {
        int c;
        // Shortest path first
        c = ((Integer)((ArrayList)route1.get("steps")).size()).compareTo(((ArrayList)route2.get("steps")).size());


        // Shortest travel time next
        if (c == 0) {
            c = ((Long) route1.get("travel_time")).compareTo((Long) route2.get("travel_time"));
        }

        // Earliest arrival time
        if (c == 0) {
            ArrayList<Map> steps1 = (ArrayList) route1.get("steps");
            ArrayList<Map> steps2 = (ArrayList) route2.get("steps");
            c = ((Long) steps1.get(steps1.size() - 1).get("arrival")).compareTo((Long) (steps2.get(steps2.size() - 1).get("arrival")));

            // By Departure Docking Code
            if (c == 0) {
                c = ((String) steps1.get(2).get("code")).compareTo((String) steps2.get(2).get("code"));
                // By Arrival Docking code
                if (c == 0) {
                    c = ((String) steps1.get(steps1.size() - 3).get("code")).compareTo((String) steps2.get(steps2.size() - 3).get("code"));
                }
            }
        }

        return c;
    }

}
