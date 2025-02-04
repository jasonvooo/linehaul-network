package com.example.linehaulnetwork.controller;

import com.example.linehaulnetwork.api.RouteApi;
import com.example.linehaulnetwork.api.UpdateApi;
import com.example.linehaulnetwork.model.GetTravelTime200Response;
import com.example.linehaulnetwork.model.UpdateLineHaulNetworkRequest;
import com.example.linehaulnetwork.route.IRouteService;
import com.example.linehaulnetwork.route.Route;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;

@RestController
public class LineHaulNetworkController implements UpdateApi, RouteApi {

    private final NativeWebRequest nativeWebRequest;
    private final IRouteService routeService;

    public LineHaulNetworkController(NativeWebRequest nativeWebRequest, IRouteService routeService) {
        this.nativeWebRequest = nativeWebRequest;
        this.routeService = routeService;
    }

    @Override
    public ResponseEntity<GetTravelTime200Response> getTravelTime(String from, String to) {
        return this.routeService.calculateRoute(from, to)
                .map(routePath ->
                        ResponseEntity.ok(new GetTravelTime200Response()
                                .path(routePath.path())
                                .travelTimeTotalSeconds(routePath.total_time()))
                )
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Override
    public ResponseEntity<UpdateLineHaulNetworkRequest> updateLineHaulNetwork(UpdateLineHaulNetworkRequest request) {
        this.routeService.updateRoutes(
                request.getRoutes().stream()
                        .map(r -> new Route(r.getFrom(), r.getTo(), r.getTravelTimeSeconds()))
                        .toList()
        );
        return ResponseEntity.ok(request);
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(nativeWebRequest);
    }
}
