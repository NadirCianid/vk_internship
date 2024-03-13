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
import static com.nadir.vk_internship.entity.AccessRole.ROLE_ALBUMS_EDITOR;
import static com.nadir.vk_internship.entity.AccessRole.ROLE_ALBUMS_VIEWER;

@Slf4j
@RequestMapping("/api/albums")
@RestController
@RequiredArgsConstructor
public class AlbumsController {
    private final ApiController apiController;
    private List<AccessRole> accessRoles = Lists.newArrayList(ROLE_ADMIN, ROLE_ALBUMS);


    //Listing all resources
    @GetMapping("")
    public ResponseEntity<String> getAlbums() {
        accessRoles.add(ROLE_ALBUMS_VIEWER);
        return apiController.getResource("/albums", "", accessRoles);
    }


    //Getting a resource
    @GetMapping("/{id}")
    public ResponseEntity<String> getAlbum(@PathVariable("id") int albumId) {
        accessRoles.add(ROLE_ALBUMS_VIEWER);
        return apiController.getResource("/albums/", String.valueOf(albumId), accessRoles);
    }


    //Creating a resource
    @PostMapping("")
    public ResponseEntity<String> addAlbum(@RequestBody Object album) {
        accessRoles.add(ROLE_ALBUMS_EDITOR);
        return apiController.postResource("/albums/", album, accessRoles);
    }

    //Updating a resource
    @PutMapping("/{id}")
    public ResponseEntity<String> updateAlbum(@RequestBody Object album, @PathVariable("id") int albumId) {
        accessRoles.add(ROLE_ALBUMS_EDITOR);
        return apiController.putResource("/albums/", albumId, album, accessRoles);
    }

    //Deleting a resource
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAlbum(@PathVariable("id") int albumId) {
        accessRoles.add(ROLE_ALBUMS_EDITOR);
        return apiController.deleteResource("/albums/", albumId, accessRoles);
    }

    //Filtering resources
    @GetMapping(value = "", params = "userId")
    public ResponseEntity<String> getAlbumsByUser(@RequestParam("userId") int userId) {
        accessRoles.add(ROLE_ALBUMS_VIEWER);
        return apiController.getResource("/albums" + "?userId=" + userId, "", accessRoles);
    }

    @GetMapping(value = "", params = "title")
    public ResponseEntity<String> getAlbumsByTitle(@RequestParam("title") String title) {
        accessRoles.add(ROLE_ALBUMS_VIEWER);
        return apiController.getResource("/albums" + "?title=" + title, "", accessRoles);
    }

    //Listing nested resources
    @GetMapping("/{id}/photos")
    public ResponseEntity<String> getPhotosOfAlbum(@PathVariable("id") int albumId) {
        accessRoles.add(ROLE_ALBUMS_VIEWER);
        return apiController.getResource("/albums/" + albumId + "/photos", "", accessRoles);
    }
}
