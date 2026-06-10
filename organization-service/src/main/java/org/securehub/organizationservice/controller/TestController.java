package org.securehub.organizationservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/organizations/test")
    public String test() {
        return "Test";
    }
}
