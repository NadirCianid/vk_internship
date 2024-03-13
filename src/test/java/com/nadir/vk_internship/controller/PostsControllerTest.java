package com.nadir.vk_internship.controller;

import com.nadir.vk_internship.entity.AccessRole;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.nadir.vk_internship.entity.AccessRole.ROLE_ADMIN;
import static com.nadir.vk_internship.entity.AccessRole.ROLE_POSTS;
import static com.nadir.vk_internship.entity.AccessRole.ROLE_POSTS_EDITOR;
import static com.nadir.vk_internship.entity.AccessRole.ROLE_POSTS_VIEWER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class PostsControllerTest {
    private final RestTemplate REST_TEMPLATE = new RestTemplate();
    private List<AccessRole> accessRoles = Lists.newArrayList(ROLE_ADMIN, ROLE_POSTS);

    @Mock
    ApiController apiController;

    @InjectMocks
    PostsController postsController;

    @Test
    public void getPostsTest() {
        //given
        ResponseEntity<String> posts = REST_TEMPLATE.getForEntity("https://jsonplaceholder.typicode.com/posts", String.class);
        accessRoles.add(ROLE_POSTS_VIEWER);
        doReturn(posts).when(this.apiController).getResource("/posts", "", accessRoles);

        //when
        var responseEntity = this.postsController.getPosts();

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(posts.getBody(), responseEntity.getBody());

    }


    @Test
    public void getPostTest() {
        //given
        ResponseEntity<String> post = REST_TEMPLATE.getForEntity("https://jsonplaceholder.typicode.com/posts/1", String.class);

        accessRoles.add(ROLE_POSTS_VIEWER);
        doReturn(post).when(this.apiController).getResource("/posts/","1", accessRoles);

        //when
        var responseEntity = this.postsController.getPost(1);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(post.getBody(), responseEntity.getBody());
    }


    @Test
    public void addPostTest() {
        //given
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("userId", 1);
        map.put("title", "vk internship");
        map.put("body", "is the best");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map);

        ResponseEntity<String> response = REST_TEMPLATE.postForEntity("https://jsonplaceholder.typicode.com/posts",
                entity, String.class);
        accessRoles.add(ROLE_POSTS_EDITOR);
        doReturn(response).when(this.apiController).postResource(eq("/posts/"), any(),  eq(accessRoles));
        //when
        var responseEntity = this.postsController.addPost(map);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(response.getBody(), responseEntity.getBody());
    }

    @Test
    public void updatePostTest() {
        //given
        Map<String, Object> map = new HashMap<>();
        map.put("userId", 1);
        map.put("id", 1);
        map.put("title", "vk internship");
        map.put("body", "is the best");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map);

        ResponseEntity<String> response = REST_TEMPLATE.exchange("https://jsonplaceholder.typicode.com/posts/1",
                                                                    HttpMethod.PUT, entity, String.class, 1);
        accessRoles.add(ROLE_POSTS_EDITOR);
        doReturn(response).when(this.apiController).putResource(eq("/posts/"), eq(1), any(),  eq(accessRoles));

        //when
        var responseEntity = this.postsController.updatePost(map, 1);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response.getBody(), responseEntity.getBody());
    }

    @Test
    public void deletePostTest() {
        //given
        ResponseEntity<String> response = new ResponseEntity<>("{}", HttpStatus.OK);
        accessRoles.add(ROLE_POSTS_EDITOR);
        doReturn(response).when(this.apiController).deleteResource("/posts/", 1, accessRoles);

        //when
        var responseEntity = this.postsController.deletePost(1);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response.getBody(), responseEntity.getBody());
    }
}