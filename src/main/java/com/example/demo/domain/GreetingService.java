package com.example.demo.domain;

public interface GreetingService {
    default String getGreeting() {
        return "hello world";
    }
}
