package com.nadir.vk_internship.controller;

import com.nadir.vk_internship.entity.AccessRole;
import com.nadir.vk_internship.entity.ApiUser;
import com.nadir.vk_internship.entity.Log;
import com.nadir.vk_internship.repository.LogRepo;
import com.nadir.vk_internship.repository.UserRepo;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
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
    private final LogRepo logger;
    @Getter
    @Setter
    private ApiUser user;

    @Autowired
    public ApiController(UserRepo userRepo, LogRepo logger) {
        this.userRepo = userRepo;
        this.logger = logger;
        user = null;
    }

    @GetMapping("/api")
    public String basePage() {
        if (user == null) {
            user = userRepo.getReferenceById(4);
        }

        Log log = new Log()
                .setDateTime(ZonedDateTime.now())
                .setStatus("OK")
                .setLevel("info")
                .setUserLogin(user.getLogin())
                .setUserRole(user.getRole())
                .setHasAccess(true)
                .setResource("/api")
                .setHTTP_method("GET")
                .setDescription("Visited start page");

        logger.save(log);

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
        String authMessage = "Авторизация прошла успешно. Вы вошли, как пользователь:\n";

        user = userRepo.authenticate(login, pswd);

        if (user == null) {
            user = userRepo.getReferenceById(4);
            authMessage = "Пользователя с введенными данными нет в базе. \nВы авторизованы, как гость:\n";
        }

        log.info("Authentication by " + user);

        return new ResponseEntity<>(authMessage + user.toString(), HttpStatus.OK);
    }

    @GetMapping("/api/auth/allUsers")
    public List<String> getAllUsers() {
        if (user.getRole().equals(AccessRole.ROLE_ADMIN)) {
            log.info(user + " printed all users list.");
            return userRepo.findAll().stream().map(ApiUser::toString).toList();
        }
        return new ArrayList<>(Collections.singleton(NO_ACCESS_MESSAGE));
    }

    @PostMapping("/api/auth/addUser")
    public ResponseEntity<String> addUser(@RequestParam String login, @RequestParam String pswd, @RequestParam int roleId) {
        ResponseEntity<String> checkResult = checkAccessRole("/api/auth/addUser", AccessRole.ROLE_ADMIN, " tried to get resource ");
        if (checkResult != null) {
            return checkResult;
        }

        if (userRepo.userCountByLogin(login) > 0) {
            log.info(user + " tried to create new user: login=" + login + "; pswd=" + pswd + "; roleId=" + roleId + ".");
            return new ResponseEntity<>("Пользователь с таким логином уже существует", HttpStatus.OK);
        }

        ApiUser newUser = new ApiUser(login, pswd, AccessRole.values()[roleId]);

        log.info(newUser + " created by " + user);

        return new ResponseEntity<>(userRepo.save(newUser).toString(), HttpStatus.OK);
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

        ResponseEntity<String> addedResource = REST_TEMPLATE.postForEntity(RESOURCE_URL + path, body, String.class);

        log.info(getUser() + " added resource " + addedResource.getBody() + ".");

        return addedResource;
    }

    public ResponseEntity<String> putResource(String path, int resourceId, Object body, AccessRole accessRole) {
        ResponseEntity<String> checkResult = checkAccessRole(path, accessRole, " tried to update resource ");
        if (checkResult != null) {
            return checkResult;
        }

        HttpEntity<Object> entity = new HttpEntity<>(body);
        ResponseEntity<String> updatedResource = REST_TEMPLATE.exchange(RESOURCE_URL + path + resourceId,
                HttpMethod.PUT,
                entity,
                String.class,
                resourceId);

        log.info(getUser() + " updated resource " + updatedResource.getBody() + ".");

        return updatedResource;
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

    public ResponseEntity<String> checkAccessRole(String path, AccessRole accessRole, String action) {
        if (!user.getRole().equals(AccessRole.ROLE_ADMIN) && !user.getRole().equals(accessRole)) {
            log.error(getUser() + action + path + ".");
            return new ResponseEntity<>(NO_ACCESS_MESSAGE, HttpStatus.OK);
        }

        return null;
    }
}
