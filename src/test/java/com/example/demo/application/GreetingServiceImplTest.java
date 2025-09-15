package com.example.demo.application;

import com.example.demo.domain.GreetingService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GreetingServiceImplTest {
    @Test
    void testGreeting() {
        GreetingService service = new GreetingServiceImpl();
        assertEquals("Hello, World!", service.getGreeting());
    }
}
