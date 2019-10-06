package io.demo.trainstations.service;

import io.demo.trainstations.domain.GraphRoute;

import java.util.List;

/**
 * A {@link TrainStationService} is a core service for train stations management
 */
public interface TrainStationService {

    /**
     * Create new station
     * @param name unique name of the station
     */
    void createStation(String name);

    /**
     * Liust all stations
     * @return list of stations names
     */
    List<String> listAllStations();

    /**
     * Delete station
     * @param name name of station
     */
    void deleteStation(String name);

    /**
     * Create/update route between two stations, with specified cost
     * @param station1 first station
     * @param station2 second station
     * @param cost cost of the route
     */
    void createRoute(String station1, String station2, double cost);

    /**
     * Check if route exists, return it's cost
     * @param station1 first station
     * @param station2 second station
     * @return cost of the route
     */
    Double routeExists(String station1, String station2);

    /**
     * Delete route between two stations, if exists
     * @param station1 first station
     * @param station2 second station
     */
    void deleteRoute(String station1, String station2);

    /**
     * Find routes between two stations, including total costs
     * @param station1 first station
     * @param station2 second station
     * @return list of found routes
     */
    List<GraphRoute> findRoutes(String station1, String station2);
}
