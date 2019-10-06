package io.demo.storage.service.grpc;

import io.demo.storage.domain.GraphRoute;
import io.demo.storage.service.StorageService;
import io.demo.storage.service.grpc.domain.*;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Implementation of gRPC interface
 */
@GRpcService
public class StorageServiceGrpcImpl extends StorageServiceGrpc.StorageServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageServiceGrpcImpl.class);

    @Autowired
    private StorageService storageService;

    @Override
    public void createVertex(Vertex request, StreamObserver<CreateVertexResponse> responseObserver) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("server received {}", request);
        }

        storageService.createVertex(request.getLabel(), request.getName());
        CreateVertexResponse response = CreateVertexResponse.newBuilder().build();

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("server responded {}", response);
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void listAllVertexNamesByLabel(ListVerticesRequest request, StreamObserver<ListVerticesResponse> responseObserver) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("server received {}", request);
        }

        List<String> vertices = storageService.listAllVertexNamesByLabel(request.getLabel());
        ListVerticesResponse response = ListVerticesResponse.newBuilder().addAllNames(vertices).build();

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("server responded {}", response);
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteVertex(Vertex request, StreamObserver<DeleteVertexResponse> responseObserver) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("server received {}", request);
        }

        storageService.deleteVertex(request.getLabel(), request.getName());
        DeleteVertexResponse response = DeleteVertexResponse.newBuilder().build();

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("server responded {}", response);
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void createEdgeBidirectional(Edge request, StreamObserver<CreateEdgeBidirectionalResponse> responseObserver) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("server received {}", request);
        }

        storageService.createEdgeBidirectional(
                request.getVertex1().getLabel(), request.getVertex1().getName(),
                request.getVertex2().getLabel(), request.getVertex2().getName(),
                request.getLabel(), request.getWeight().getValue());
        CreateEdgeBidirectionalResponse response = CreateEdgeBidirectionalResponse.newBuilder().build();

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("server responded {}", response);
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void edgeExistsBidirectional(Edge request, StreamObserver<EdgeExistsBidirectionalResponse> responseObserver) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("server received {}", request);
        }

        Double weight = storageService.edgeExistsBidirectional(
                request.getVertex1().getLabel(), request.getVertex1().getName(),
                request.getVertex2().getLabel(), request.getVertex2().getName(),
                request.getLabel());

        EdgeExistsBidirectionalResponse.Builder builder = EdgeExistsBidirectionalResponse.newBuilder();

        if (weight != null) {
            builder.setWeight(DoubleValue.newBuilder().setValue(weight).build());
        }

        EdgeExistsBidirectionalResponse response = builder.build();

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("server responded {}", response);
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteEdgeBidirectional(Edge request, StreamObserver<DeleteEdgeBidirectionalResponse> responseObserver) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("server received {}", request);
        }

        storageService.deleteEdgeBidirectional(
                request.getVertex1().getLabel(), request.getVertex1().getName(),
                request.getVertex2().getLabel(), request.getVertex2().getName(),
                request.getLabel());
        DeleteEdgeBidirectionalResponse response = DeleteEdgeBidirectionalResponse.newBuilder().build();

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("server responded {}", response);
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void findPathsBidirectional(Edge request, StreamObserver<FindPathsBidirectionalResponse> responseObserver) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("server received {}", request);
        }

        List<GraphRoute> paths = storageService.findPathsBidirectional(
                request.getVertex1().getLabel(), request.getVertex1().getName(),
                request.getVertex2().getLabel(), request.getVertex2().getName(),
                request.getLabel());

        FindPathsBidirectionalResponse.Builder builder = FindPathsBidirectionalResponse.newBuilder();

        paths.forEach(path -> {
            builder.addPaths(io.demo.storage.service.grpc.domain.GraphRoute.newBuilder()
                    .setTotalWeight(DoubleValue.newBuilder().setValue(path.getTotalWeight()).build())
                    .addAllVertices(path.getVertices())
                    .build());
        });

        FindPathsBidirectionalResponse response = builder.build();

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("server responded {}", response);
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
