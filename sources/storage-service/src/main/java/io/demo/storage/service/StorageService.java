package io.demo.storage.service;

import io.demo.storage.domain.GraphRoute;

import java.util.List;

/**
 * A {@link StorageService} is a core service used for graph manipulations
 * Under the hood, delegates calls to {@link io.demo.storage.util.GremlinManager} utility class
 */
public interface StorageService {

    /**
     * Create Vertex
     * @param label vertex label
     * @param name value of vertex "name" property
     */
    void createVertex(String label, String name);

    /**
     * Find all Vertices with specified label
     * @param label vertex label
     * @return a list of names
     */
    List<String> listAllVertexNamesByLabel(String label);

    /**
     * Delete Vertex found by label and name
     * @param label vertex label
     * @param name value of vertex "name" property
     */
    void deleteVertex(String label, String name);

    /**
     * Create an edge between two vertices, set it's "weight" property. If edge exists already, update it's "weight" property
     * @param vLabel1 vertex label of first vertex
     * @param name1 name of first vertex
     * @param vLabel2 vertex label of second vertex
     * @param name2 name of second vertex
     * @param eLabel edge label
     * @param weight value of edge "weight" property
     */
    void createEdgeBidirectional(String vLabel1, String name1, String vLabel2, String name2, String eLabel, double weight);

    /**
     * Check if edge does exist, return it's weight
     * @param vLabel1 vertex label of first vertex
     * @param name1 name of first vertex
     * @param vLabel2 vertex label of second vertex
     * @param name2 name of second vertex
     * @param eLabel edge label
     * @return value of edge "weight" property
     */
    Double edgeExistsBidirectional(String vLabel1, String name1, String vLabel2, String name2, String eLabel);

    /**
     * Delete edge, if exists
     * @param vLabel1 vertex label of first vertex
     * @param name1 name of first vertex
     * @param vLabel2 vertex label of second vertex
     * @param name2 name of second vertex
     * @param eLabel edge label
     */
    void deleteEdgeBidirectional(String vLabel1, String name1, String vLabel2, String name2, String eLabel);

    /**
     * Find paths between two vertices, calculate total weights as well
     * @param vLabel1 vertex label of first vertex
     * @param name1 name of first vertex
     * @param vLabel2 vertex label of second vertex
     * @param name2 name of second vertex
     * @param eLabel edge label
     * @return list of paths found
     */
    List<GraphRoute> findPathsBidirectional(String vLabel1, String name1, String vLabel2, String name2, String eLabel);
}
