package com.nadir.vk_internship.controller;

import com.nadir.vk_internship.entity.AccessRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.nadir.vk_internship.controller.ApiController.getResource;

@Slf4j
@RequestMapping("/api/posts")
@RestController
public class PostsController {

    //Listing all resources
    @GetMapping("")
    private ResponseEntity<String> getPosts() {
        return getResource("/posts/", AccessRole.ROLE_POSTS);
    }


    //Getting a resource
    @GetMapping("/{id}")
    private ResponseEntity<String> getPost(@PathVariable("id") int postId) {
        return getResource("/posts/" + postId, AccessRole.ROLE_POSTS);
    }

    //Creating a resource
    @PostMapping("")
    private ResponseEntity<String> addPost(@RequestBody Object post) {
        return ApiController.postResource("/posts", post, AccessRole.ROLE_POSTS);
    }
}
