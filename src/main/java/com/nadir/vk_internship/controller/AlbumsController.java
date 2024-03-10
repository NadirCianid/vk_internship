package com.nadir.vk_internship.controller;

import com.nadir.vk_internship.entity.AccessRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RequestMapping("/api/albums")
@RestController
public class AlbumsController {
    private final ApiController apiController;

    public AlbumsController(ApiController apiController) {
        this.apiController = apiController;
    }

    //Listing all resources
    @GetMapping("")
    private ResponseEntity<String> getAlbums() {
        return apiController.getResource("/albums", AccessRole.ROLE_ALBUMS);
    }
}
