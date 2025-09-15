package com.example.demo.application;

import com.example.demo.domain.GreetingService;
import org.springframework.stereotype.Service;

@Service
public class GreetingServiceImpl implements GreetingService {
    @Override
    public String getGreeting() {
        return "Hello, World!";
    }
}
