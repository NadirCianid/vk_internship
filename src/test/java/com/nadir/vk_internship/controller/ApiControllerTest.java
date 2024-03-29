package com.nadir.vk_internship.controller;

import com.nadir.vk_internship.entity.AccessRole;
import com.nadir.vk_internship.entity.ApiUser;
import com.nadir.vk_internship.entity.Log.Log;
import com.nadir.vk_internship.repository.LogRepo;
import com.nadir.vk_internship.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.List;

import static com.nadir.vk_internship.controller.ApiController.convertToJson;
import static com.nadir.vk_internship.entity.AccessRole.ROLE_ADMIN;
import static com.nadir.vk_internship.entity.AccessRole.ROLE_POSTS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ApiControllerTest {
    private final String NO_ACCESS_MESSAGE = "Эта страница не доступна для вашей роли!";
    private final List<AccessRole> accessRoles = List.of(ROLE_ADMIN, ROLE_POSTS);
    ApiUser guest, admin;
    @Mock
    UserRepo userRepo;
    @Mock
    LogRepo logger;

    @InjectMocks
    ApiController apiController;

    @BeforeEach
    void init() {
        //given
        guest = new ApiUser(4, "guest", "1111", AccessRole.ROLE_GUEST);
        admin = new ApiUser(0, "admin", "admin", AccessRole.ROLE_ADMIN);
        doReturn(guest).when(userRepo).getReferenceById(4);

        apiController.setUser(userRepo.getReferenceById(4));
    }


    @Test
    void authUser_validData_userAuthorization() {
        //given
        doReturn(admin).when(userRepo).authenticate("admin", "admin");
        String authMessage = "Авторизация прошла успешно. Вы вошли, как пользователь:\n";

        var expectedResponse = new ResponseEntity<>(authMessage + admin, HttpStatus.OK);

        //when
        var responseEntity = apiController.authUser("admin", "admin");

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse.getBody(), responseEntity.getBody());
    }

    @Test
    void authUser_invalidData_userAuthorizationAsGuest() {
        //given
        String authMessage = "Пользователя с введенными данными нет в базе. \nВы авторизованы, как гость:\n";
        var expectedResponse = new ResponseEntity<>(authMessage + guest, HttpStatus.NOT_ACCEPTABLE);

        //when
        var responseEntity = apiController.authUser("adminnn", "admin");

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, responseEntity.getStatusCode());
        assertEquals(expectedResponse.getBody(), responseEntity.getBody());
    }

    @Test
    void addUser_byAdmin_uniqueLogin_createsUser() {
        //given
        ApiUser newUser = new ApiUser(0,"newUser", "pswd", AccessRole.values()[1]);
        doReturn(newUser).when(userRepo).save(any());
        apiController.setUser(admin);

        var expectedResponse = new ResponseEntity<>(newUser.toString(), HttpStatus.CREATED);

        //when
        var responseEntity = apiController.addUser("newUser", "pswd", 1);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(expectedResponse.getBody(), responseEntity.getBody());
    }

    @Test
    void addUser_notByAdmin_declinesOperation() {
        //given
        apiController.setUser(guest);

        var expectedResponse = new ResponseEntity<>(NO_ACCESS_MESSAGE, HttpStatus.NOT_ACCEPTABLE);

        //when
        var responseEntity = apiController.addUser("newUser", "pswd", 1);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, responseEntity.getStatusCode());
        assertEquals(expectedResponse.getBody(), responseEntity.getBody());
    }

    @Test
    void addUser_byAdmin_notUniqueLogin_declines() {
        //given
        doReturn((short) 1).when(userRepo).userCountByLogin("admin");
        apiController.setUser(admin);

        var expectedResponse = new ResponseEntity<>("Пользователь с таким логином уже существует", HttpStatus.NOT_ACCEPTABLE);

        //when
        var responseEntity = apiController.addUser("admin", "admin", 1);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, responseEntity.getStatusCode());
        assertEquals(expectedResponse.getBody(), responseEntity.getBody());
    }

    @Test
    void getResource_correctRole_returnsResource() {
        //given
        apiController.setUser(admin);

        LinkedHashMap<String, Object> firstPost = new LinkedHashMap<>();
        firstPost.put("userId", 1);
        firstPost.put("id", 1);
        firstPost.put("title", "sunt aut facere repellat provident occaecati excepturi optio reprehenderit");
        firstPost.put("body", "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto");

        var expectedResponse = new ResponseEntity<>(convertToJson(firstPost), HttpStatus.OK);

        //when
        var responseEntity = apiController.getResource("/posts/", "1", accessRoles);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse.getBody(), responseEntity.getBody());
    }

    @Test
    void getResource_incorrectRole_declines() {
        //given
        apiController.setUser(guest);

        var expectedResponse = new ResponseEntity<>(NO_ACCESS_MESSAGE, HttpStatus.NOT_ACCEPTABLE);

        //when
        var responseEntity = apiController.getResource("/posts/", "1", accessRoles);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, responseEntity.getStatusCode());
        assertEquals(expectedResponse.getBody(), responseEntity.getBody());
    }

    @Test
    void postResource_correctRole_returnsValidResponseEntity() {
        //given
        apiController.setUser(admin);

        LinkedHashMap<String, Object> newPost = new LinkedHashMap<>();
        newPost.put("userId", 1);
        newPost.put("id", 100);
        newPost.put("title", "vk internship");
        newPost.put("body", "is the best");


        var expectedResponse = new ResponseEntity<>(convertToJson(newPost), HttpStatus.CREATED);

        //when
        var responseEntity = apiController.postResource("/posts/", newPost, accessRoles);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(expectedResponse.getBody(), responseEntity.getBody());
    }

    @Test
    void checkAccessRole_correctRole_returnsNull() {
        //given
        apiController.setUser(admin);
        ApiUser postViewer = new ApiUser("postViewer", "pswd", AccessRole.ROLE_POSTS);

        //when
        var adminResponseEntity = apiController.checkAccessRole("/test", accessRoles, "test access", new Log());

        apiController.setUser(postViewer);
        var postViewerResponseEntity = apiController.checkAccessRole("/test", accessRoles, "test access", new Log());


        //then
        assertNull(adminResponseEntity);
        assertNull(postViewerResponseEntity);
    }

    @Test
    void checkAccessRole_incorrectRole_returnsMessage() {
        //given
        ApiUser userViewer = new ApiUser("userViewer", "pswd", AccessRole.ROLE_USERS);

        var expectedResponse = new ResponseEntity<>(NO_ACCESS_MESSAGE, HttpStatus.NOT_ACCEPTABLE);

        //when
        var guestResponseEntity = apiController.checkAccessRole("/test", accessRoles, "test access", new Log());

        apiController.setUser(userViewer);
        var userViewerResponseEntity = apiController.checkAccessRole("/test", accessRoles, "test access", new Log());


        //then
        assertNotNull(guestResponseEntity);
        assertNotNull(userViewerResponseEntity);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, guestResponseEntity.getStatusCode());
        assertEquals(HttpStatus.NOT_ACCEPTABLE, userViewerResponseEntity.getStatusCode());
        assertEquals(expectedResponse.getBody(), guestResponseEntity.getBody());
        assertEquals(expectedResponse.getBody(), userViewerResponseEntity.getBody());

    }
}