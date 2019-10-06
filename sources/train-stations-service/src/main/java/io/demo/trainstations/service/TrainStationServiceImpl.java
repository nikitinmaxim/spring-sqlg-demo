package io.demo.trainstations.service;

import io.demo.storage.service.grpc.domain.*;
import io.demo.trainstations.config.StorageServiceProperties;
import io.demo.trainstations.domain.GraphRoute;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrainStationServiceImpl implements TrainStationService {

    public static final String V_LABEL = "train_station";
    public static final String E_LABEL = "route";

    @Autowired
    private StorageServiceProperties storageServiceProperties;

    private StorageServiceGrpc.StorageServiceBlockingStub storageService;

    @PostConstruct
    private void init() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(storageServiceProperties.getHost(), storageServiceProperties.getPort()).usePlaintext().build();
        storageService = StorageServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public void createStation(String name) {
        Vertex vertex = Vertex.newBuilder().setLabel(V_LABEL).setName(name).build();
        storageService.createVertex(vertex);
    }

    @Override
    public List<String> listAllStations() {
        ListVerticesRequest request = ListVerticesRequest.newBuilder().setLabel(V_LABEL).build();
        ListVerticesResponse response = storageService.listAllVertexNamesByLabel(request);
        return response.getNamesList();
    }

    @Override
    public void deleteStation(String name) {
        Vertex vertex = Vertex.newBuilder().setLabel(V_LABEL).setName(name).build();
        storageService.deleteVertex(vertex);
    }

    @Override
    public void createRoute(String station1, String station2, double cost) {
        Vertex vertex1 = Vertex.newBuilder().setLabel(V_LABEL).setName(station1).build();
        Vertex vertex2 = Vertex.newBuilder().setLabel(V_LABEL).setName(station2).build();
        Edge route = Edge.newBuilder()
                .setVertex1(vertex1)
                .setVertex2(vertex2)
                .setLabel(E_LABEL)
                .setWeight(DoubleValue.newBuilder().setValue(cost).build()).build();
        storageService.createEdgeBidirectional(route);
    }

    @Override
    public Double routeExists(String station1, String station2) {
        Vertex vertex1 = Vertex.newBuilder().setLabel(V_LABEL).setName(station1).build();
        Vertex vertex2 = Vertex.newBuilder().setLabel(V_LABEL).setName(station2).build();
        Edge route = Edge.newBuilder().setLabel(E_LABEL).setVertex1(vertex1).setVertex2(vertex2).build();
        EdgeExistsBidirectionalResponse response = storageService.edgeExistsBidirectional(route);

        if (response.hasWeight()) {
            return response.getWeight().getValue();
        }

        return null;
    }

    @Override
    public void deleteRoute(String station1, String station2) {
        Vertex vertex1 = Vertex.newBuilder().setLabel(V_LABEL).setName(station1).build();
        Vertex vertex2 = Vertex.newBuilder().setLabel(V_LABEL).setName(station2).build();
        Edge route = Edge.newBuilder().setLabel(E_LABEL).setVertex1(vertex1).setVertex2(vertex2).build();
        storageService.deleteEdgeBidirectional(route);
    }

    @Override
    public List<GraphRoute> findRoutes(String station1, String station2) {
        Vertex vertex1 = Vertex.newBuilder().setLabel(V_LABEL).setName(station1).build();
        Vertex vertex2 = Vertex.newBuilder().setLabel(V_LABEL).setName(station2).build();
        Edge edge = Edge.newBuilder().setLabel(E_LABEL).setVertex1(vertex1).setVertex2(vertex2).build();
        FindPathsBidirectionalResponse findPathsBidirectionalResponse = storageService.findPathsBidirectional(edge);

        List<GraphRoute> result = new ArrayList<>();

        findPathsBidirectionalResponse.getPathsList().forEach(graphRoute -> {
            GraphRoute route = new GraphRoute();
            route.setTotalCost(graphRoute.getTotalWeight().getValue());
            route.getStations().addAll(graphRoute.getVerticesList());
            result.add(route);
        });

        return result;
    }

    public StorageServiceGrpc.StorageServiceBlockingStub getStorageService() {
        return storageService;
    }

    public void setStorageService(StorageServiceGrpc.StorageServiceBlockingStub storageService) {
        this.storageService = storageService;
    }
}
