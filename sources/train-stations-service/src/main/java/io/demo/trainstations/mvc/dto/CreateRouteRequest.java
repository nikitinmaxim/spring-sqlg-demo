package io.demo.trainstations.mvc.dto;

/**
 * Data required for route creation
 */
public class CreateRouteRequest {

    private String station1;
    private String station2;
    private double cost;

    public String getStation1() {
        return station1;
    }

    public void setStation1(String station1) {
        this.station1 = station1;
    }

    public String getStation2() {
        return station2;
    }

    public void setStation2(String station2) {
        this.station2 = station2;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
