package com.nadir.vk_internship.controller;

import com.nadir.vk_internship.entity.AccessRole;
import com.nadir.vk_internship.entity.ApiUser;
import com.nadir.vk_internship.repository.UserRepo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.accessibility.AccessibleComponent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
public class ApiController {
    private final RestTemplate REST_TEMPLATE = new RestTemplate();
    private final String RESOURCE_URL = "https://jsonplaceholder.typicode.com";
    private final String NO_ACCESS_MESSAGE = "Эта страница не доступна для вашей роли!";
    private final UserRepo userRepo;
    @Getter
    @Setter
    private ApiUser user;

    @Autowired
    public ApiController(UserRepo userRepo) {
        this.userRepo = userRepo;
        user = new ApiUser("guest", "1111", AccessRole.ROLE_GUEST);
    }

    @GetMapping("/api")
    public String basePage() {
        return """
                Здравствуйте! Чтобы использовать мой API, вам необходимо авторизоваться (/api/auth).
                                
                На данный момент API поддерживает следующие роли пользователей:
                - ROLE_ADMIN (имеет доступ ко всем ресурсам);
                - ROLE_POSTS (имеет доступ к постам и операциям с ними);
                - ROLE_USERS (имеет доступ к пользователям (сайта %s) и операциям с ними);
                - ROLE_ALBUMS (имеет доступ к альбомам и операциям с ними);
                - ROLE_GUEST (не имеет доступа ресурсам и выдается до авторизации в системе);
                """.formatted(RESOURCE_URL);

    }

    @PostMapping("/api/auth")
    public ResponseEntity<String> authUser(@RequestParam String login, @RequestParam String pswd) {
        String guestMessage = "Авторизация прошла успешно. Вы вошли, как пользователь:\n";

        user = userRepo.authenticate(login, pswd);

        log.info("Authentication by " + user);

        if (user.getRole().equals(AccessRole.ROLE_GUEST)) {
            guestMessage = "Пользователя с введенными данными нет в базе. \nВы авторизованы, как гость:\n";
        }

        return new ResponseEntity<>(guestMessage + user.toString(), HttpStatus.OK);
    }

    @GetMapping("/api/auth/allUsers")
    private List<String> getAllUsers() {
        if (user.getRole().equals(AccessRole.ROLE_ADMIN)) {
            return userRepo.findAll().stream().map(ApiUser::toString).toList();
        }
        return new ArrayList<>(Collections.singleton(NO_ACCESS_MESSAGE));
    }

    @PostMapping("/api/auth/addUser")
    private String addUser(@RequestParam String login, @RequestParam String pswd, @RequestParam int roleId) {
        if (!user.getRole().equals(AccessRole.ROLE_ADMIN)) {
            return NO_ACCESS_MESSAGE;
        }

        if (userRepo.userCountByLogin(login) > 0) {
            return "Пользователь с таким логином уже существует";
        }

        ApiUser newUser = new ApiUser(login, pswd, AccessRole.values()[roleId]);

        log.info(newUser + "created by " + user);

        return userRepo.save(newUser).toString();
    }

    public ResponseEntity<String> getResource(String path, AccessRole accessRole) {
        ResponseEntity<String> checkResult = checkAccessRole(path, accessRole, " tried to get resource ");
        if (checkResult != null) {
            return checkResult;
        }

        log.info(getUser() + " got resource(s) " + path + ".");

        return REST_TEMPLATE.getForEntity(RESOURCE_URL + path, String.class);
    }


    public ResponseEntity<String> postResource(String path, Object body, AccessRole accessRole) {
        ResponseEntity<String> checkResult = checkAccessRole(path, accessRole, " tried to add resource ");
        if (checkResult != null) {
            return checkResult;
        }

        log.info(getUser() + " added resource " + body + ".");

        return REST_TEMPLATE.postForEntity(RESOURCE_URL + path, body, String.class);
    }

    public ResponseEntity<String> putResource(String path, int resourceId, Object body, AccessRole accessRole) {
        ResponseEntity<String> checkResult = checkAccessRole(path, accessRole, " tried to update resource ");
        if (checkResult != null) {
            return checkResult;
        }

        HttpEntity<Object> entity = new HttpEntity<>(body);

        log.info(getUser() + " updated resource " + body + ".");

        return REST_TEMPLATE.exchange(RESOURCE_URL + path + resourceId, HttpMethod.PUT, entity, String.class, resourceId);
    }

    public ResponseEntity<String> deleteResource(String path, int resourceId, AccessRole accessRole) {
        ResponseEntity<String> checkResult = checkAccessRole(path, accessRole, " tried to delete resource ");
        if (checkResult != null) {
            return checkResult;
        }
        log.info(getUser() + " deleted resource " + path + resourceId + ".");

        REST_TEMPLATE.delete(RESOURCE_URL + path + resourceId);
        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    private ResponseEntity<String> checkAccessRole(String path, AccessRole accessRole, String action) {
        if (!user.getRole().equals(AccessRole.ROLE_ADMIN) && !user.getRole().equals(accessRole)) {
            log.error(getUser() + action + path + ".");
            return new ResponseEntity<>(NO_ACCESS_MESSAGE, HttpStatus.OK);
        }

        return null;
    }
}
