package io.demo.storage.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * A Path between two Vertices
 */
public class GraphRoute {

    private Double totalWeight;
    private List<String> vertices = new ArrayList<>();

    /**
     * Get the sum of weights between all "subpaths"
     * @return total weight
     */
    public Double getTotalWeight() {
        return totalWeight;
    }

    /**
     * Set the sum of weights between all "subpaths"
     * @param totalWeight total weight
     */
    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }

    /**
     * A list of names of vertices contained in this path
     * @return list of names of vertices
     */
    public List<String> getVertices() {
        return vertices;
    }
}
