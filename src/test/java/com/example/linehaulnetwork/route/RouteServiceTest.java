package com.example.linehaulnetwork.route;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RouteServiceTest {

    private RouteService routeService;

    @BeforeEach
    void setUp() {
        this.routeService = new RouteService();
    }

    @Test
    void testOneStep() {
        this.routeService.updateRoutes(List.of(new Route("a", "b", 10)));

        Optional<RoutePath> path = this.routeService.calculateRoute("a", "b");
        assertEquals(List.of("a", "b"), path.get().path());
        assertEquals(10, path.get().total_time());
    }

    @Test
    void testOneStepReverse() {
        this.routeService.updateRoutes(List.of(new Route("a", "b", 10)));

        Optional<RoutePath> path = this.routeService.calculateRoute("b", "a");
        assertEquals(List.of("b", "a"), path.get().path());
        assertEquals(10, path.get().total_time());
    }

    @Test
    void testPathSourceDoesntExist() {
        this.routeService.updateRoutes(List.of(new Route("a", "b", 10)));

        assertThrows(IllegalArgumentException.class, () -> this.routeService.calculateRoute("c", "b"));
    }

    @Test
    void testPathDestinationDoesntExist() {
        this.routeService.updateRoutes(List.of(new Route("a", "b", 10)));

        assertThrows(IllegalArgumentException.class, () -> this.routeService.calculateRoute("b", "c"));
    }

    @Test
    void testPathNotFound() {
        this.routeService.updateRoutes(List.of(new Route("a", "b", 10), new Route("c", "d", 15)));

        assertThrows(NoRouteFoundException.class, () -> this.routeService.calculateRoute("a", "c"));
    }


    @Test
    void testMultiStepPath() {
        this.routeService.updateRoutes(
                List.of(
                        new Route("a", "b", 5),
                        new Route("b", "c", 10),
                        new Route("c", "d", 7)
                )
        );

        Optional<RoutePath> path = this.routeService.calculateRoute("a", "d");
        assertEquals(List.of("a", "b", "c", "d"), path.get().path());
        assertEquals(22, path.get().total_time());
    }

    @Test
    void testCycleWithShorterPath() {
        this.routeService.updateRoutes(
                List.of(
                        new Route("a", "b", 5),
                        new Route("b", "c", 10),
                        new Route("c", "d", 7),
                        new Route("d", "a", 2)

                )
        );

        Optional<RoutePath> path = this.routeService.calculateRoute("a", "c");
        assertEquals(List.of("a", "d", "c"), path.get().path());
        assertEquals(9, path.get().total_time());
    }

    @Test
    void testMultiStepPathWithEqualWeight() {
        this.routeService.updateRoutes(
                List.of(
                        new Route("a", "b", 5),
                        new Route("a", "c", 11),
                        new Route("b", "c", 6),
                        new Route("b", "d", 9),
                        new Route("c", "d", 7),
                        new Route("d", "a", 7)

                )
        );

        Optional<RoutePath> path = this.routeService.calculateRoute("a", "c");
        assertEquals(List.of("a", "c"), path.get().path());
        assertEquals(11, path.get().total_time());
    }
}
