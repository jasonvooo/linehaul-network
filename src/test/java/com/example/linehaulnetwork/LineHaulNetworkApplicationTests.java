package com.example.linehaulnetwork;

import com.example.linehaulnetwork.controller.LineHaulNetworkController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LineHaulNetworkApplicationTests {

    @Autowired
    private LineHaulNetworkController lineHaulNetworkController;

    @Test
    void contextLoads() {
        assertThat(lineHaulNetworkController).isNotNull();
    }
}
