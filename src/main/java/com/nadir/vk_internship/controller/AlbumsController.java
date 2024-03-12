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
@RequestMapping("/api/albums")
@RestController
@RequiredArgsConstructor
public class AlbumsController {
    private final ApiController apiController;


    //Listing all resources
    @GetMapping("")
    public ResponseEntity<String> getAlbums() {
        return apiController.getResource("/albums", "", AccessRole.ROLE_ALBUMS);
    }


    //Getting a resource
    @GetMapping("/{id}")
    public ResponseEntity<String> getAlbum(@PathVariable("id") int albumId) {
        return apiController.getResource("/albums/", String.valueOf(albumId), AccessRole.ROLE_ALBUMS);
    }


    //Creating a resource
    @PostMapping("")
    public ResponseEntity<String> addAlbum(@RequestBody Object album) {
        return apiController.postResource("/albums/", album, AccessRole.ROLE_ALBUMS);
    }

    //Updating a resource
    @PutMapping("/{id}")
    public ResponseEntity<String> updateAlbum(@RequestBody Object album, @PathVariable("id") int albumId) {
        return apiController.putResource("/albums/", albumId, album, AccessRole.ROLE_ALBUMS);
    }

    //Deleting a resource
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAlbum(@PathVariable("id") int albumId) {
        return apiController.deleteResource("/albums/", albumId, AccessRole.ROLE_ALBUMS);
    }

    //Filtering resources
    @GetMapping(value = "", params = "userId")
    public ResponseEntity<String> getAlbumsByUser(@RequestParam("userId") int userId) {
        return apiController.getResource("/albums" + "?userId=" + userId, "", AccessRole.ROLE_ALBUMS);
    }

    @GetMapping(value = "", params = "title")
    public ResponseEntity<String> getAlbumsByTitle(@RequestParam("title") String title) {
        return apiController.getResource("/albums" + "?title=" + title, "", AccessRole.ROLE_ALBUMS);
    }

    //Listing nested resources
    @GetMapping("/{id}/photos")
    public ResponseEntity<String> getPhotosOfAlbum(@PathVariable("id") int albumId) {
        return apiController.getResource("/albums/" + albumId + "/photos", "", AccessRole.ROLE_ALBUMS);
    }
}
