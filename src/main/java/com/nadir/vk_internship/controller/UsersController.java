package com.nadir.vk_internship.controller;

import com.nadir.vk_internship.entity.AccessRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@Slf4j
@RequestMapping("/api/users")
@RestController
public class UsersController {
    private final ApiController apiController;

    public UsersController(ApiController apiController) {
        this.apiController = apiController;
    }

    //Listing all resources
    @GetMapping("")
    private ResponseEntity<String> getUsers() {
        return apiController.getResource("/users", AccessRole.ROLE_USERS);
    }
}
