package com.example.linehaulnetwork.route;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service layer which handles saving routes and calculating path
 */
@Service
public class RouteService implements IRouteService {

    // Using singleton here just to keep task simple
    private final List<Route> routes = new ArrayList<>();

    /**
     * Assumption made that when update is called it removes all previous routes
     * Since there is no CRUD api for routes there is no way to remove existing so this is the only way
     *
     * @param r routes
     */
    @Override
    public void updateRoutes(List<Route> r) {
        routes.clear();
        routes.addAll(r);
    }

    /**
     * Utilises jgrapht library to build graph representation of graph with travel time as weights. Uses Dijkstras
     * algorithm to find the shortest path between source and destination
     *
     * @param source      source
     * @param destination destination
     * @return RoutePath if found
     */
    @Override
    public Optional<RoutePath> calculateRoute(String source, String destination) {
        SimpleWeightedGraph<String, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        routes.forEach(r -> {
            graph.addVertex(r.source());
            graph.addVertex(r.destination());
            DefaultWeightedEdge edge1 = graph.addEdge(r.source(), r.destination());
            graph.setEdgeWeight(edge1, r.travel_time());
        });

        if (!graph.containsVertex(source)) {
            throw new IllegalArgumentException(source + " does not exist");
        }

        if (!graph.containsVertex(destination)) {
            throw new IllegalArgumentException(destination + " does not exist");
        }

        final DijkstraShortestPath<String, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<>(graph);
        final GraphPath<String, DefaultWeightedEdge> path = dijkstra.getPath(source, destination);

        if (path == null) {
            throw new NoRouteFoundException("No path found");

        }

        return Optional.of(new RoutePath(path.getVertexList(), (int) Math.round(path.getWeight())));
    }
}
