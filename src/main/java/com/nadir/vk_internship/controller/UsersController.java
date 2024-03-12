package com.nadir.vk_internship.controller;

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



@Slf4j
@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UsersController {
    private final ApiController apiController;

    //Listing all resources
    @GetMapping("")
    private ResponseEntity<String> getUsers() {
        return apiController.getResource("/users", "", AccessRole.ROLE_USERS);
    }

    //Getting a resource
    @GetMapping("/{id}")
    public ResponseEntity<String> getUser(@PathVariable("id") int userId) {
        return apiController.getResource("/users/", String.valueOf(userId), AccessRole.ROLE_USERS);
    }


    //Creating a resource
    @PostMapping("")
    public ResponseEntity<String> addUser(@RequestBody Object user) {
        return apiController.postResource("/users/", user, AccessRole.ROLE_USERS);
    }


    //Updating a resource
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@RequestBody Object user, @PathVariable("id") int userId) {
        return apiController.putResource("/users/", userId, user, AccessRole.ROLE_USERS);
    }


    //Deleting a resource
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") int userId) {
        return apiController.deleteResource("/users/", userId, AccessRole.ROLE_USERS);
    }


    //Filtering resources
    @GetMapping(value = "", params = "name")
    public ResponseEntity<String> getUsersByName(@RequestParam("name") String name) {
        return apiController.getResource("/users" + "?name=" + name, "", AccessRole.ROLE_USERS);
    }

    @GetMapping(value = "", params = "username")
    public ResponseEntity<String> getUsersByUsername(@RequestParam("username") String username) {
        return apiController.getResource("/users" + "?username=" + username, "", AccessRole.ROLE_USERS);
    }


    //Listing nested resources
    @GetMapping("/{id}/albums")
    public ResponseEntity<String> getAlbumsOfUser(@PathVariable("id") int userId) {
        return apiController.getResource("/users/" + userId + "/albums", "", AccessRole.ROLE_USERS);
    }

    @GetMapping("/{id}/posts")
    public ResponseEntity<String> getPostsOfUser(@PathVariable("id") int userId) {
        return apiController.getResource("/users/" + userId + "/posts", "", AccessRole.ROLE_USERS);
    }

    @GetMapping("/{id}/todos")
    public ResponseEntity<String> getTodosOfUser(@PathVariable("id") int userId) {
        return apiController.getResource("/users/" + userId + "/todos", "", AccessRole.ROLE_USERS);
    }
}
