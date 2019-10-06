package io.demo.trainstations.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * A route between two stations
 */
public class GraphRoute {

    private Double totalCost;
    private List<String> stations = new ArrayList<>();

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public List<String> getStations() {
        return stations;
    }
}
