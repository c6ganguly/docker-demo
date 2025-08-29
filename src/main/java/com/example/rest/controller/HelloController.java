package com.example.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/api/hello")
    public String hello() {
        return "Welcome to your first RESTful Spring Boot app!";
    }

    @GetMapping("/api/hello1")
    public String hello1() {
        return "Welcome to your first RESTful Spring Boot app!";
    }

    @GetMapping("/api/hello2")
    public String hello2() {
        return "Welcome to your first RESTful Spring Boot app!";
    }
}
