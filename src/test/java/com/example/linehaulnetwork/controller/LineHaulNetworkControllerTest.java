package com.example.linehaulnetwork.controller;


import com.example.linehaulnetwork.model.GetTravelTime200Response;
import com.example.linehaulnetwork.model.UpdateLineHaulNetworkRequest;
import com.example.linehaulnetwork.model.UpdateLineHaulNetworkRequestRoutesInner;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LineHaulNetworkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // JSON serializer

    @BeforeEach
    void setUp() {
    }

    @Nested
    class UpdateRoutesTest {
        @Test
        void testSuccess() throws Exception {
            final UpdateLineHaulNetworkRequest request = new UpdateLineHaulNetworkRequest()
                    .addRoutesItem(new UpdateLineHaulNetworkRequestRoutesInner("A", "B", 421))
                    .addRoutesItem(new UpdateLineHaulNetworkRequestRoutesInner("B", "C", 324))
                    .addRoutesItem(new UpdateLineHaulNetworkRequestRoutesInner("F", "G", 11));

            requestUpdate(request).andExpect(status().isOk());
        }

        @Test
        void testInvalidRequest() throws Exception {
            final String junk = """
                    {
                        "routes1": [
                            { "from": "DepotA", "to": "DepotB", "travelTime": 120 }
                        ]
                    }
                    """;

            requestUpdate(junk).andExpect(status().isBadRequest());
        }

        @Test
        void testInvalidTravelTime() throws Exception {
            final UpdateLineHaulNetworkRequest request = new UpdateLineHaulNetworkRequest()
                    .addRoutesItem(new UpdateLineHaulNetworkRequestRoutesInner("A", "B", -1))
                    .addRoutesItem(new UpdateLineHaulNetworkRequestRoutesInner("B", "C", 50));

            requestUpdate(request)
                    .andExpect(content().json("{\"message\":\"routes[0].travelTimeSeconds must be greater than or equal to 0\"}"));
        }

        @Test
        void testInvalidSource() throws Exception {
            final UpdateLineHaulNetworkRequest request = new UpdateLineHaulNetworkRequest()
                    .addRoutesItem(new UpdateLineHaulNetworkRequestRoutesInner("", "B", 10))
                    .addRoutesItem(new UpdateLineHaulNetworkRequestRoutesInner("B", "C", 50));

            requestUpdate(request)
                    .andExpect(content().json("{\"message\":\"routes[0].from size must be between 1 and 2147483647\"}"));
        }

        @Test
        void testInvalidDestination() throws Exception {
            final UpdateLineHaulNetworkRequest request = new UpdateLineHaulNetworkRequest()
                    .addRoutesItem(new UpdateLineHaulNetworkRequestRoutesInner("A", "", 10))
                    .addRoutesItem(new UpdateLineHaulNetworkRequestRoutesInner("B", "C", 50));

            requestUpdate(request)
                    .andExpect(content().json("{\"message\":\"routes[0].to size must be between 1 and 2147483647\"}"));
        }
    }

    @Nested
    class GetRoutesTest {
        @BeforeEach
        void beforeEach() throws Exception {
            final UpdateLineHaulNetworkRequest request = new UpdateLineHaulNetworkRequest()
                    .addRoutesItem(new UpdateLineHaulNetworkRequestRoutesInner("A", "B", 421))
                    .addRoutesItem(new UpdateLineHaulNetworkRequestRoutesInner("B", "C", 324))
                    .addRoutesItem(new UpdateLineHaulNetworkRequestRoutesInner("C", "D", 44))
                    .addRoutesItem(new UpdateLineHaulNetworkRequestRoutesInner("F", "G", 11));

            requestUpdate(request);
        }

        @Test
        void testSuccess() throws Exception {
            GetTravelTime200Response response = new GetTravelTime200Response().path(List.of("A", "B", "C")).travelTimeTotalSeconds(745);
            getRoute("A", "C").andExpect(status().isOk()).andExpect(content().json(objectMapper.writeValueAsString(response)));
        }

        @Test
        void testInvalidFrom() throws Exception {
            getRoute("DONT_EXIST", "C").andExpect(status().isBadRequest()).andExpect(content().json("{\"message\":\"DONT_EXIST does not exist\"}"));
        }

        @Test
        void testInvalidTo() throws Exception {
            getRoute("A", "DONT_EXIST").andExpect(status().isBadRequest()).andExpect(content().json("{\"message\":\"DONT_EXIST does not exist\"}"));
        }

        @Test
        void testPathNotFound() throws Exception {
            getRoute("F", "A").andExpect(status().isNotFound()).andExpect(content().json("{\"message\":\"No path found\"}"));
        }

    }

    protected ResultActions requestUpdate(UpdateLineHaulNetworkRequest updateLineHaulNetworkRequest) throws Exception {
        return requestUpdate(objectMapper.writeValueAsString(updateLineHaulNetworkRequest));
    }

    protected ResultActions requestUpdate(String content) throws Exception {
        return mockMvc.perform(post("/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));
    }

    protected ResultActions getRoute(String from, String to) throws Exception {
        return mockMvc.perform(get("/route")
                .contentType(MediaType.APPLICATION_JSON).queryParam("from", from).queryParam("to", to));
    }
}
