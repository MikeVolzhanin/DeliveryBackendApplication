package ru.volzhanin.deliverybackendapplication;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DeliveryBackendApplicationTests {
    @Test
    void contextLoads() {
    }

    @Test
    void concatStrings(){
        String stringOne = "Hello ";
        String stringTwo = "World";

        assertEquals("Hello World", stringOne + stringTwo);
    }
}
