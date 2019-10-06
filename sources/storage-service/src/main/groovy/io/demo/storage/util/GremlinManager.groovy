package io.demo.storage.util

import io.demo.storage.domain.GraphRoute
import org.apache.tinkerpop.gremlin.process.traversal.Path
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.structure.T
import org.apache.tinkerpop.gremlin.structure.Vertex

/**
 * A {@link GremlinManager} is a {@link io.demo.storage.service.StorageService} backend implementation, based on gremlin-groovy
 * If sqlg-mariadb is used, transaction commit is necessary after any operation on the graph
 */
class GremlinManager {

    static final String NAME = "name"
    static final String WEIGHT = "weight"
    static final String VERTEX = "vertex"
    static final String EDGE = "edge"

    /**
     * Create Vertex
     * @param graph TinkerPop graph
     * @param commit if true, perform transaction commit at the end
     * @param label vertex label
     * @param name value of vertex "name" property
     */
    static void createVertex(Graph graph, boolean commit, String label, String name) {
        GraphTraversalSource g = graph.traversal()
        g.addV(label).property("name", name).next()

        if (commit) {
            graph.tx().commit()
        }
    }

    /**
     * Find all Vertices with specified label
     * @param graph TinkerPop graph
     * @param commit if true, perform transaction commit at the end
     * @param label vertex label
     * @return a list of names
     */
    static List<String> listAllVertexNamesByLabel(Graph graph, boolean commit, String label) {
        List<String> result = new ArrayList<>()
        GraphTraversalSource g = graph.traversal()
        Iterator<String> it = g.V().has(T.label, label).values(NAME).iterator()

        while (it.hasNext()) {
            result.add(it.next())
        }

        if (commit) {
            graph.tx().commit()
        }

        result
    }

    /**
     * Delete Vertex found by label and name
     * @param graph TinkerPop graph
     * @param commit if true, perform transaction commit at the end
     * @param label vertex label
     * @param name value of vertex "name" property
     */
    static void deleteVertex(Graph graph, boolean commit, String label, String name) {
        GraphTraversalSource g = graph.traversal()
        GraphTraversal search = g.V().has(label, NAME, name)

        if (search.hasNext()) {
            Vertex vertex = search.next()
            vertex.remove()
        }

        if (commit) {
            graph.tx().commit()
        }
    }

    /**
     * Create an edge between two vertices, set it's "weight" property. If edge exists already, update it's "weight" property
     * @param graph TinkerPop graph
     * @param commit if true, perform transaction commit at the end
     * @param vLabel1 vertex label of first vertex
     * @param name1 name of first vertex
     * @param vLabel2 vertex label of second vertex
     * @param name2 name of second vertex
     * @param eLabel edge label
     * @param weight value of edge "weight" property
     */
    static void createEdgeBidirectional(Graph graph, boolean commit, String vLabel1, String name1, String vLabel2, String name2, String eLabel, double weight) {
        graph.traversal()
            .V().has(vLabel1, NAME, name1).as(VERTEX)
            .V().has(vLabel2, NAME, name2).coalesce(
                __.bothE(eLabel).where(__.bothV().as(VERTEX)).property(WEIGHT, weight),
                __.addE(eLabel).from(VERTEX).property(WEIGHT, weight)).next()

        if (commit) {
            graph.tx().commit()
        }
    }

    /**
     * Check if edge does exist, return it's weight
     * @param graph TinkerPop graph
     * @param commit if true, perform transaction commit at the end
     * @param vLabel1 vertex label of first vertex
     * @param name1 name of first vertex
     * @param vLabel2 vertex label of second vertex
     * @param name2 name of second vertex
     * @param eLabel edge label
     * @return value of edge "weight" property
     */
    static Double edgeExistsBidirectional(Graph graph, boolean commit, String vLabel1, String name1, String vLabel2, String name2, String eLabel) {
        GraphTraversal search = graph.traversal()
            .V().has(vLabel1, NAME, name1)
                .bothE(eLabel).as(EDGE)
                .bothV().has(vLabel2, NAME, name2).as(VERTEX).select(EDGE).values(WEIGHT)

        if (search.hasNext()) {
            return (Double) search.next()
        }

        if (commit) {
            graph.tx().commit()
        }

        null
    }

    /**
     * Delete edge between two vertices, if exists
     * @param graph TinkerPop graph
     * @param commit if true, perform transaction commit at the end
     * @param vLabel1 vertex label of first vertex
     * @param name1 name of first vertex
     * @param vLabel2 vertex label of second vertex
     * @param name2 name of second vertex
     * @param eLabel edge label
     */
    static void deleteEdgeBidirectional(Graph graph, boolean commit, String vLabel1, String name1, String vLabel2, String name2, String eLabel) {
        GraphTraversal search = graph.traversal()
            .V().has(vLabel1, NAME, name1)
                .bothE(eLabel).as(EDGE)
                .bothV().has(vLabel2, NAME, name2).select(EDGE)

        if (search.hasNext()) {
            Edge edge = (Edge) search.next()
            edge.remove()
        }

        if (commit) {
            graph.tx().commit()
        }
    }

    /**
     * Perform BFS (Breadth First Search), and also calculate total weights of paths
     * @param graph TinkerPop graph
     * @param commit if true, perform transaction commit at the end
     * @param vLabel1 vertex label of first vertex
     * @param name1 name of first vertex
     * @param vLabel2 vertex label of second vertex
     * @param name2 name of second vertex
     * @param eLabel edge label
     * @return list of paths found and their total weights
     */
    static List<GraphRoute> findPathsBidirectional(Graph graph, boolean commit, String vLabel1, String name1, String vLabel2, String name2, String eLabel) {
        GraphTraversalSource g = graph.traversal()

        GraphTraversal search = g.V().has(vLabel1, NAME, name1)
            .repeat(__.bothE().bothV().simplePath())
                .until(__.has(vLabel2, NAME, name2)).path().as(eLabel)
            .map(__.unfold().coalesce(__.values(WEIGHT), __.constant(0.0)).sum()).as(WEIGHT)
                .select(WEIGHT, eLabel)

        List<GraphRoute> result = new ArrayList<>();

        while (search.hasNext()) {
            Map<String, Object> path = (Map) search.next()
            GraphRoute graphRoute = new GraphRoute()
            graphRoute.setTotalWeight(((BigDecimal) path.get(WEIGHT)).toDouble());

            Path route = (Path) path.get(eLabel)
            Iterator<Object> it = route.iterator()

            while (it.hasNext()) {
                Object item = it.next()

                if (item instanceof Vertex) {
                    graphRoute.getVertices().add(((Vertex) item).value(NAME))
                }
            }

            result.add(graphRoute)
        }

        if (commit) {
            graph.tx().commit()
        }

        result
    }
}
