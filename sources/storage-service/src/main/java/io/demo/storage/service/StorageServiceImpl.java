package io.demo.storage.service;

import io.demo.storage.domain.GraphRoute;
import io.demo.storage.util.GremlinManager;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.umlg.sqlg.structure.SqlgGraph;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class StorageServiceImpl implements StorageService {

    @Autowired
    private Graph graph;

    private boolean transactional = true;

    @PostConstruct
    private void init() {
        if (graph instanceof SqlgGraph) {
            transactional = true;
        } else {
            transactional = false;
        }
    }

    @Override
    public void createVertex(String label, String name) {
        GremlinManager.createVertex(graph, transactional, label, name);
    }

    @Override
    public List<String> listAllVertexNamesByLabel(String label) {
        return GremlinManager.listAllVertexNamesByLabel(graph, transactional, label);
    }

    @Override
    public void deleteVertex(String label, String name) {
        GremlinManager.deleteVertex(graph, transactional, label, name);
    }

    @Override
    public void createEdgeBidirectional(String vLabel1, String name1, String vLabel2, String name2, String eLabel, double weight) {
        GremlinManager.createEdgeBidirectional(graph, transactional, vLabel1, name1, vLabel2, name2, eLabel, weight);
    }

    @Override
    public Double edgeExistsBidirectional(String vLabel1, String name1, String vLabel2, String name2, String eLabel) {
        return GremlinManager.edgeExistsBidirectional(graph, transactional, vLabel1, name1, vLabel2, name2, eLabel);
    }

    @Override
    public void deleteEdgeBidirectional(String vLabel1, String name1, String vLabel2, String name2, String eLabel) {
        GremlinManager.deleteEdgeBidirectional(graph, transactional, vLabel1, name1, vLabel2, name2, eLabel);
    }

    @Override
    public List<GraphRoute> findPathsBidirectional(String vLabel1, String name1, String vLabel2, String name2, String eLabel) {
        return GremlinManager.findPathsBidirectional(graph, transactional, vLabel1, name1, vLabel2, name2, eLabel);
    }
}
