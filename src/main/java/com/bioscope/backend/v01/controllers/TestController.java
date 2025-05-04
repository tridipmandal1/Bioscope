package com.bioscope.backend.v01.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/test")
public class TestController {

    @GetMapping
    public String index() {

        return "<h1>Test works </h1>";
    }
}
