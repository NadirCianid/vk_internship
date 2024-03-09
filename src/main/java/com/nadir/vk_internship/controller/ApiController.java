package com.nadir.vk_internship.controller;

import com.nadir.vk_internship.entity.AccessRole;
import com.nadir.vk_internship.entity.User;
import com.nadir.vk_internship.repository.UserRepo;
import com.nadir.vk_internship.controller.AuthorizationController;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.nadir.vk_internship.controller.AuthorizationController.checkUserAuth;
import static com.nadir.vk_internship.controller.AuthorizationController.getAllResources;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ApiController {
    private final UserRepo userRepo;
    @Getter
    @Setter
    private static User user;


    @GetMapping("/api")
    public String authentication() {
        return "Hello!";
    }

    @PostMapping("/api/auth")
    public User authUser(@RequestParam String login, @RequestParam String pswd) {
        user = userRepo.authenticate(login, pswd);
        checkUserAuth();

        log.info("Authentication by " + user);
        return user;
    }

    @GetMapping("/api/allUsers")
    private List<String> getAllUsers() {
        return userRepo.findAll().stream().map(User::toString).toList();
    }

    @GetMapping("/api/posts")
    private String getPosts() {
        return getAllResources("/posts", AccessRole.ROLE_POSTS);
    }

    @GetMapping("/api/users")
    private String getUsers() {
        return getAllResources("/users", AccessRole.ROLE_USERS);
    }

    @GetMapping("/api/albums")
    private String getAlbums() {
        return getAllResources("/albums", AccessRole.ROLE_ALBUMS);
    }
}
