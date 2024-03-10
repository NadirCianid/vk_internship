package com.nadir.vk_internship.controller;

import com.nadir.vk_internship.entity.AccessRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.nadir.vk_internship.controller.AuthorizationController.getAllResources;

@Slf4j
@RestController
public class PostsController {

    @GetMapping("/api/posts")
    private String getPosts() {
        return getAllResources("/posts", AccessRole.ROLE_POSTS);
    }
}
