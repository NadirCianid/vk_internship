package com.nadir.vk_internship.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class ApiController {

    @GetMapping("/api")
    public String authentication() {
        return "Hello!";
    }

    @PostMapping("/api")
    public String authUser(@RequestParam String login, @RequestParam String pswd) {
        if(Objects.equals(login, "admin") && Objects.equals(pswd, "admin")) {
            return "Hello, admin!";
        }

        return "Hello, anonim";
    }
}
