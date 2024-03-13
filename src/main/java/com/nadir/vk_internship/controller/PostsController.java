package com.nadir.vk_internship.controller;

import com.google.common.collect.Lists;
import com.nadir.vk_internship.entity.AccessRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.nadir.vk_internship.entity.AccessRole.ROLE_ADMIN;
import static com.nadir.vk_internship.entity.AccessRole.ROLE_POSTS;
import static com.nadir.vk_internship.entity.AccessRole.ROLE_POSTS_EDITOR;
import static com.nadir.vk_internship.entity.AccessRole.ROLE_POSTS_VIEWER;


@Slf4j
@RequestMapping("/api/posts")
@RestController
@RequiredArgsConstructor
public class PostsController {
    private final ApiController apiController;
    private List<AccessRole> accessRoles = Lists.newArrayList(ROLE_ADMIN, ROLE_POSTS);


    //Listing all resources
    @GetMapping("")
    public ResponseEntity<String> getPosts() {
        accessRoles.add(ROLE_POSTS_VIEWER);
        return apiController.getResource("/posts", "", accessRoles);
    }


    //Getting a resource
    @GetMapping("/{id}")
    public ResponseEntity<String> getPost(@PathVariable("id") int postId) {
        accessRoles.add(ROLE_POSTS_VIEWER);
        return apiController.getResource("/posts/", String.valueOf(postId), accessRoles);
    }

    //Creating a resource
    @PostMapping("")
    public ResponseEntity<String> addPost(@RequestBody Object post) {
        accessRoles.add(ROLE_POSTS_EDITOR);
        return apiController.postResource("/posts/", post, accessRoles);
    }

    //Updating a resource
    @PutMapping("/{id}")
    public ResponseEntity<String> updatePost(@RequestBody Object post, @PathVariable("id") int postId) {
        accessRoles.add(ROLE_POSTS_EDITOR);
        return apiController.putResource("/posts/", postId, post, accessRoles);
    }

    //Deleting a resource
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable("id") int postId) {
        accessRoles.add(ROLE_POSTS_EDITOR);
        return apiController.deleteResource("/posts/", postId, accessRoles);
    }

    //Filtering resources
    @GetMapping(value = "", params = "userId")
    public ResponseEntity<String> getPostsByUser(@RequestParam("userId") int userId) {
        accessRoles.add(ROLE_POSTS_VIEWER);
        return apiController.getResource("/posts" + "?userId=" + userId, "", accessRoles);
    }

    @GetMapping(value = "", params = "title")
    public ResponseEntity<String> getPostsByTitle(@RequestParam("title") String title) {
        accessRoles.add(ROLE_POSTS_VIEWER);
        return apiController.getResource("/posts" + "?title=" + title, "", accessRoles);
    }

    @GetMapping(value = "", params = "body")
    public ResponseEntity<String> getPostsByBody(@RequestParam("body") String body) {
        accessRoles.add(ROLE_POSTS_VIEWER);
        return apiController.getResource("/posts" + "?userId=" + body, "", accessRoles);
    }

    //Listing nested resources
    @GetMapping("/{id}/comments")
    public ResponseEntity<String> getCommentsOfPost(@PathVariable("id") int postId) {
        accessRoles.add(ROLE_POSTS_VIEWER);
        return apiController.getResource("/posts/" + postId + "/comments", "", accessRoles);
    }
}
