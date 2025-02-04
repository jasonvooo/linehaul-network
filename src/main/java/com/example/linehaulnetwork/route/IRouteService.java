package com.example.linehaulnetwork.route;

import java.util.List;
import java.util.Optional;

public interface IRouteService {

    void updateRoutes(List<Route> route);

    Optional<RoutePath> calculateRoute(String source, String destination);
}
