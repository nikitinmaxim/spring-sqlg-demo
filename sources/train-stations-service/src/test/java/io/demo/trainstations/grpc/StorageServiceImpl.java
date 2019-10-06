package io.demo.trainstations.grpc;

import io.demo.storage.service.grpc.domain.*;
import io.grpc.stub.StreamObserver;

/**
 * Stub implementation
 */
public class StorageServiceImpl extends StorageServiceGrpc.StorageServiceImplBase {

    @Override
    public void createVertex(Vertex request, StreamObserver<CreateVertexResponse> responseObserver) {
        CreateVertexResponse response = CreateVertexResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void listAllVertexNamesByLabel(ListVerticesRequest request, StreamObserver<ListVerticesResponse> responseObserver) {
        ListVerticesResponse response = ListVerticesResponse.newBuilder()
                .addNames("Station 1")
                .addNames("Station 2")
                .addNames("Station 3")
                .addNames("Station 4")
                .addNames("Station 5")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteVertex(Vertex request, StreamObserver<DeleteVertexResponse> responseObserver) {
        DeleteVertexResponse response = DeleteVertexResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void createEdgeBidirectional(Edge request, StreamObserver<CreateEdgeBidirectionalResponse> responseObserver) {
        CreateEdgeBidirectionalResponse response = CreateEdgeBidirectionalResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void edgeExistsBidirectional(Edge request, StreamObserver<EdgeExistsBidirectionalResponse> responseObserver) {
        EdgeExistsBidirectionalResponse response;

        if (request.getVertex1().getName().equals("Station 1") && request.getVertex2().getName().equals("Station 2")) {
            response = EdgeExistsBidirectionalResponse.newBuilder().setWeight(DoubleValue.newBuilder().setValue(0.123).build()).build();
        } else {
            response = EdgeExistsBidirectionalResponse.newBuilder().build();
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteEdgeBidirectional(Edge request, StreamObserver<DeleteEdgeBidirectionalResponse> responseObserver) {
        DeleteEdgeBidirectionalResponse response = DeleteEdgeBidirectionalResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void findPathsBidirectional(Edge request, StreamObserver<FindPathsBidirectionalResponse> responseObserver) {
        FindPathsBidirectionalResponse response = FindPathsBidirectionalResponse.newBuilder()
                .addPaths(GraphRoute.newBuilder()
                        .setTotalWeight(DoubleValue.newBuilder().setValue(1).build())
                        .addVertices(request.getVertex1().getName())
                        .addVertices(request.getVertex2().getName())
                        .build())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
