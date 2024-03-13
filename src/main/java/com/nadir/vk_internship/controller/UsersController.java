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

import java.util.List;

import static com.nadir.vk_internship.entity.AccessRole.ROLE_ADMIN;
import static com.nadir.vk_internship.entity.AccessRole.ROLE_ALBUMS;
import static com.nadir.vk_internship.entity.AccessRole.ROLE_USERS;
import static com.nadir.vk_internship.entity.AccessRole.ROLE_USERS_EDITOR;
import static com.nadir.vk_internship.entity.AccessRole.ROLE_USERS_VIEWER;


@Slf4j
@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UsersController {
    private final ApiController apiController;
    private List<AccessRole> accessRoles = Lists.newArrayList(ROLE_ADMIN, ROLE_USERS);

    //Listing all resources
    @GetMapping("")
    private ResponseEntity<String> getUsers() {
        accessRoles.add(ROLE_USERS_VIEWER);
        return apiController.getResource("/users", "", accessRoles);
    }

    //Getting a resource
    @GetMapping("/{id}")
    public ResponseEntity<String> getUser(@PathVariable("id") int userId) {
        accessRoles.add(ROLE_USERS_VIEWER);
        return apiController.getResource("/users/", String.valueOf(userId), accessRoles);
    }


    //Creating a resource
    @PostMapping("")
    public ResponseEntity<String> addUser(@RequestBody Object user) {
        accessRoles.add(ROLE_USERS_EDITOR);
        return apiController.postResource("/users/", user, accessRoles);
    }


    //Updating a resource
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@RequestBody Object user, @PathVariable("id") int userId) {
        accessRoles.add(ROLE_USERS_EDITOR);
        return apiController.putResource("/users/", userId, user, accessRoles);
    }


    //Deleting a resource
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") int userId) {
        accessRoles.add(ROLE_USERS_EDITOR);
        return apiController.deleteResource("/users/", userId, accessRoles);
    }


    //Filtering resources
    @GetMapping(value = "", params = "name")
    public ResponseEntity<String> getUsersByName(@RequestParam("name") String name) {
        accessRoles.add(ROLE_USERS_VIEWER);
        return apiController.getResource("/users" + "?name=" + name, "", accessRoles);
    }

    @GetMapping(value = "", params = "username")
    public ResponseEntity<String> getUsersByUsername(@RequestParam("username") String username) {
        accessRoles.add(ROLE_USERS_VIEWER);
        return apiController.getResource("/users" + "?username=" + username, "", accessRoles);
    }


    //Listing nested resources
    @GetMapping("/{id}/albums")
    public ResponseEntity<String> getAlbumsOfUser(@PathVariable("id") int userId) {
        accessRoles.add(ROLE_USERS_VIEWER);
        return apiController.getResource("/users/" + userId + "/albums", "", accessRoles);
    }

    @GetMapping("/{id}/posts")
    public ResponseEntity<String> getPostsOfUser(@PathVariable("id") int userId) {
        accessRoles.add(ROLE_USERS_VIEWER);
        return apiController.getResource("/users/" + userId + "/posts", "", accessRoles);
    }

    @GetMapping("/{id}/todos")
    public ResponseEntity<String> getTodosOfUser(@PathVariable("id") int userId) {
        accessRoles.add(ROLE_USERS_VIEWER);
        return apiController.getResource("/users/" + userId + "/todos", "", accessRoles);
    }
}
