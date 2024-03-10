package com.nadir.vk_internship.controller;

import com.nadir.vk_internship.entity.AccessRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RequestMapping("/api/posts")
@RestController
@RequiredArgsConstructor
public class PostsController {
    private final ApiController apiController;


    //Listing all resources
    @GetMapping("")
    public ResponseEntity<String> getPosts() {
        return apiController.getResource("/posts", AccessRole.ROLE_POSTS);
    }


    //Getting a resource
    @GetMapping("/{id}")
    public ResponseEntity<String> getPost(@PathVariable("id") int postId) {
        return apiController.getResource("/posts/" + postId, AccessRole.ROLE_POSTS);
    }

    //Creating a resource
    @PostMapping("")
    public ResponseEntity<String> addPost(@RequestBody Object post) {
        return apiController.postResource("/posts", post, AccessRole.ROLE_POSTS);
    }

    //Updating a resource
    @PutMapping("/{id}")
    public ResponseEntity<String> updatePost(@RequestBody Object post, @PathVariable("id") int postId) {
        return apiController.putResource("/posts/", postId, post, AccessRole.ROLE_POSTS);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable("id") int postId) {
        return apiController.deleteResource("/posts/", postId, AccessRole.ROLE_POSTS);
    }
}
