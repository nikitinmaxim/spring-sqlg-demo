package io.demo.storage;

import io.demo.storage.service.grpc.domain.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Testing scenario:
 * 1. test basic operations like vertex creation/deletion
 * 2. create 5 vertexes (train stations)
 * 3. create following routes:
 *    from station 3 to 1, cost 38.5
 *    from station 3 to 2, cost 41.5
 *    from station 1 to 5, cost 10
 *    from station 5 to 4, cost 5
 *    from station 4 to 2, cost 10
 * 4. perform path search between stations 1 and 2
 *    As a result we should get 2 paths:
 *       1 - 3 - 2, with total cost 80
 *       1 - 5 - 4 - 2, with total cost 25
 */
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest()
@ActiveProfiles("test")
public class ITApplicationTests {

//    @Autowired
//    private StorageService storageService;

    private StorageServiceGrpc.StorageServiceBlockingStub storageService;

    @Before
    public void prepareForTest() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565).usePlaintext().build();
        storageService = StorageServiceGrpc.newBlockingStub(channel);
    }

    @Test
    public void testGraphLogicViaGrpc() {
        String vLabel = "train_station";
        String eLabel = "route";

        ListVerticesRequest listStationsRequest = ListVerticesRequest.newBuilder().setLabel(vLabel).build();
        ListVerticesResponse stations = storageService.listAllVertexNamesByLabel(listStationsRequest);
        Assert.assertEquals("vertices count should be 0", stations.getNamesList().size(), 0);

        Vertex station1 = Vertex.newBuilder().setLabel(vLabel).setName("Station 1").build();
        Vertex station2 = Vertex.newBuilder().setLabel(vLabel).setName("Station 2").build();
        Vertex station3 = Vertex.newBuilder().setLabel(vLabel).setName("Station 3").build();
        Vertex station4 = Vertex.newBuilder().setLabel(vLabel).setName("Station 4").build();
        Vertex station5 = Vertex.newBuilder().setLabel(vLabel).setName("Station 5").build();

        storageService.createVertex(station1);
        stations = storageService.listAllVertexNamesByLabel(listStationsRequest);
        Assert.assertEquals("vertices count should be 1", stations.getNamesList().size(), 1);

        storageService.deleteVertex(station1);
        stations = storageService.listAllVertexNamesByLabel(listStationsRequest);
        Assert.assertEquals("vertices count should be 0", stations.getNamesList().size(), 0);

        storageService.createVertex(station1);
        storageService.createVertex(station2);
        storageService.createVertex(station3);
        storageService.createVertex(station4);
        storageService.createVertex(station5);
        stations = storageService.listAllVertexNamesByLabel(listStationsRequest);
        Assert.assertEquals("vertices count should be 5", stations.getNamesList().size(), 5);

        Edge routeExistsRequest = Edge.newBuilder().setLabel(eLabel).setVertex1(station1).setVertex2(station2).build();
        EdgeExistsBidirectionalResponse routeStation1Station2 = storageService.edgeExistsBidirectional(routeExistsRequest);
        Assert.assertFalse("edge should not exist at this point", routeStation1Station2.hasWeight());

        Double weight = Double.valueOf("0.5");
        Edge route = Edge.newBuilder()
                .setVertex1(station1)
                .setVertex2(station2)
                .setLabel(eLabel)
                .setWeight(DoubleValue.newBuilder().setValue(weight).build()).build();

        storageService.createEdgeBidirectional(route);
        routeStation1Station2 = storageService.edgeExistsBidirectional(routeExistsRequest);
        Assert.assertEquals("edge should exist with weight 0.5", Double.valueOf(routeStation1Station2.getWeight().getValue()), weight);

        route = Edge.newBuilder()
                .setVertex1(station1)
                .setVertex2(station2)
                .setLabel(eLabel).build();

        storageService.deleteEdgeBidirectional(route);
        routeStation1Station2 = storageService.edgeExistsBidirectional(routeExistsRequest);
        Assert.assertFalse("edge should not exist again", routeStation1Station2.hasWeight());

        weight = Double.valueOf("38.5");
        route = Edge.newBuilder()
                .setVertex1(station3)
                .setVertex2(station1)
                .setLabel(eLabel)
                .setWeight(DoubleValue.newBuilder().setValue(weight).build()).build();

        storageService.createEdgeBidirectional(route);

        weight = Double.valueOf("41.5");
        route = Edge.newBuilder()
                .setVertex1(station3)
                .setVertex2(station2)
                .setLabel(eLabel)
                .setWeight(DoubleValue.newBuilder().setValue(weight).build()).build();

        storageService.createEdgeBidirectional(route);

        weight = Double.valueOf("10");
        route = Edge.newBuilder()
                .setVertex1(station1)
                .setVertex2(station5)
                .setLabel(eLabel)
                .setWeight(DoubleValue.newBuilder().setValue(weight).build()).build();

        storageService.createEdgeBidirectional(route);

        weight = Double.valueOf("5");
        route = Edge.newBuilder()
                .setVertex1(station5)
                .setVertex2(station4)
                .setLabel(eLabel)
                .setWeight(DoubleValue.newBuilder().setValue(weight).build()).build();

        storageService.createEdgeBidirectional(route);

        weight = Double.valueOf("10");
        route = Edge.newBuilder()
                .setVertex1(station4)
                .setVertex2(station2)
                .setLabel(eLabel)
                .setWeight(DoubleValue.newBuilder().setValue(weight).build()).build();

        storageService.createEdgeBidirectional(route);

        route = Edge.newBuilder()
                .setVertex1(station1)
                .setVertex2(station2)
                .setLabel(eLabel).build();

        FindPathsBidirectionalResponse findPathsBidirectionalResponse = storageService.findPathsBidirectional(route);
        Assert.assertEquals("2 routes should be found", findPathsBidirectionalResponse.getPathsList().size(), 2);
        Assert.assertEquals("first route should cost 80", Double.valueOf(findPathsBidirectionalResponse.getPaths(0).getTotalWeight().getValue()), Double.valueOf("80.0"));
        Assert.assertEquals("second route should cost 25", Double.valueOf(findPathsBidirectionalResponse.getPaths(1).getTotalWeight().getValue()), Double.valueOf("25.0"));
        Assert.assertEquals("first route should be 3 items length", findPathsBidirectionalResponse.getPaths(0).getVerticesList().size(), 3);
        Assert.assertEquals("second route should be 4 items length", findPathsBidirectionalResponse.getPaths(1).getVerticesList().size(), 4);
    }

//    @Test
//    public void testGraphLogic() {
//        String vLabel = "train_station";
//
//        List<String> stations = storageService.listAllVertexNamesByLabel(vLabel);
//        Assert.assertEquals("vertices count should be 0", stations.size(), 0);
//
//        storageService.createVertex(vLabel, "Station 1");
//        stations = storageService.listAllVertexNamesByLabel(vLabel);
//        Assert.assertEquals("vertices count should be 1", stations.size(), 1);
//
//        storageService.deleteVertex(vLabel, "Station 1");
//        stations = storageService.listAllVertexNamesByLabel(vLabel);
//        Assert.assertEquals("vertices count should be 0", stations.size(), 0);
//
//        storageService.createVertex(vLabel, "Station 1");
//        storageService.createVertex(vLabel, "Station 2");
//        storageService.createVertex(vLabel, "Station 3");
//        storageService.createVertex(vLabel, "Station 4");
//        storageService.createVertex(vLabel, "Station 5");
//        stations = storageService.listAllVertexNamesByLabel(vLabel);
//        Assert.assertEquals("vertices count should be 5", stations.size(), 5);
//
//        String eLabel = "route";
//
//        Double connected1and2 = storageService.edgeExistsBidirectional(vLabel, "Station 1", vLabel, "Station 2", eLabel);
//        Assert.assertEquals("edge should not exist at this point", connected1and2, null);
//
//        Double weight = Double.valueOf("0.5");
//
//        storageService.createEdgeBidirectional(vLabel, "Station 1", vLabel, "Station 2", eLabel, weight);
//        connected1and2 = storageService.edgeExistsBidirectional(vLabel, "Station 1", vLabel, "Station 2", eLabel);
//        Assert.assertEquals("edge should exist with weight 0.5", connected1and2, weight);
//
//        storageService.deleteEdgeBidirectional(vLabel, "Station 1", vLabel, "Station 2", eLabel);
//        connected1and2 = storageService.edgeExistsBidirectional(vLabel, "Station 1", vLabel, "Station 2", eLabel);
//        Assert.assertEquals("edge should not exist again", connected1and2, null);
//
//        weight = Double.valueOf("38.5");
//        storageService.createEdgeBidirectional(vLabel, "Station 3", vLabel, "Station 1", eLabel, weight);
//        weight = Double.valueOf("41.5");
//        storageService.createEdgeBidirectional(vLabel, "Station 3", vLabel, "Station 2", eLabel, weight);
//        weight = Double.valueOf("10");
//        storageService.createEdgeBidirectional(vLabel, "Station 1", vLabel, "Station 5", eLabel, weight);
//        weight = Double.valueOf("5");
//        storageService.createEdgeBidirectional(vLabel, "Station 5", vLabel, "Station 4", eLabel, weight);
//        weight = Double.valueOf("10");
//        storageService.createEdgeBidirectional(vLabel, "Station 4", vLabel, "Station 2", eLabel, weight);
//
//        List<GraphRoute> routes = storageService.findPathsBidirectional(vLabel, "Station 1", vLabel, "Station 2", eLabel);
//        Assert.assertEquals("2 routes should be found", routes.size(), 2);
//        Assert.assertEquals("first route should cost 80", routes.get(0).getTotalWeight(), Double.valueOf("80.0"));
//        Assert.assertEquals("second route should cost 25", routes.get(1).getTotalWeight(), Double.valueOf("25.0"));
//        Assert.assertEquals("first route should be 3 items length", routes.get(0).getVertices().size(), 3);
//        Assert.assertEquals("second route should be 4 items length", routes.get(1).getVertices().size(), 4);
//    }
}
