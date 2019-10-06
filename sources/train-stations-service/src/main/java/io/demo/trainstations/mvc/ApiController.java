package io.demo.trainstations.mvc;

import io.demo.trainstations.domain.GraphRoute;
import io.demo.trainstations.mvc.dto.CreateRouteRequest;
import io.demo.trainstations.service.TrainStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RESTful API implementation
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private TrainStationService trainStationService;

    /**
     * Create station
     * @param name station name
     * @return OK if no exception
     */
    @PostMapping(value = "/stations")
    public ResponseEntity<String> createStation(@RequestBody String name) {
        trainStationService.createStation(name);
        return ResponseEntity.ok("OK");
    }

    /**
     * List all stations
     * @return list of stations names
     */
    @GetMapping(value = "/stations")
    public ResponseEntity<List<String>> listAllStations() {
        List<String> stations = trainStationService.listAllStations();
        return ResponseEntity.ok(stations);
    }

    /**
     * Delete station
     * @param name station name
     * @return OK if no exception
     */
    @DeleteMapping(value = "/stations/{name:.+}")
    public ResponseEntity<String> deleteStation(@PathVariable(name = "name") String name) {
        trainStationService.deleteStation(name);
        return ResponseEntity.ok("OK");
    }

    /**
     * Create route between two stations
     * @param request data object containing route parameters
     * @return OK if no exception
     */
    @PostMapping(value = "/routes")
    public ResponseEntity<String> createRoute(@RequestBody CreateRouteRequest request) {
        trainStationService.createRoute(request.getStation1(), request.getStation2(), request.getCost());
        return ResponseEntity.ok("OK");
    }

    /**
     * Check if route exists between two stations, return it's cost
     * @param station1 first station
     * @param station2 second station
     * @return cost of route, HTTP 404 if route does not exist
     */
    @GetMapping(value = "/route/{station1}/{station2:.+}")
    public ResponseEntity<Double> routeExists(@PathVariable(name = "station1") String station1,
                                              @PathVariable(name = "station2") String station2) {
        Double cost = trainStationService.routeExists(station1, station2);

        if (cost != null) {
            return ResponseEntity.ok(cost);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete route between two stations, if exists
     * @param station1 first station
     * @param station2 second station
     * @return OK if no exception
     */
    @DeleteMapping(value = "/route/{station1}/{station2:.+}")
    public ResponseEntity<String> deleteRoute(@PathVariable(name = "station1") String station1,
                                              @PathVariable(name = "station2") String station2) {
        trainStationService.deleteRoute(station1, station2);
        return ResponseEntity.ok("OK");
    }

    /**
     * Find routes between two stations, also calculate total costs
     * @param station1 first station
     * @param station2 second station
     * @return list of found routes and their total costs
     */
    @GetMapping(value = "/routes/{station1}/{station2:.+}")
    public ResponseEntity<List<GraphRoute>> findRoutes(@PathVariable(name = "station1") String station1,
                                                       @PathVariable(name = "station2") String station2) {
        List<GraphRoute> routes = trainStationService.findRoutes(station1, station2);
        return ResponseEntity.ok(routes);
    }
}
