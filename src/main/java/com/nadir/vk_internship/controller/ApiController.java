package com.nadir.vk_internship.controller;

import com.nadir.vk_internship.entity.AccessRole;
import com.nadir.vk_internship.entity.ApiUser;
import com.nadir.vk_internship.entity.Log;
import com.nadir.vk_internship.entity.LogLevel;
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

        Log dbLog = new Log()
                .setDateTime(ZonedDateTime.now())
                .setStatus(HttpStatus.OK)
                .setLevel(LogLevel.INFO)
                .setUserLogin(user.getLogin())
                .setUserRole(user.getRole())
                .setHasAccess(true)
                .setResource("/api")
                .setHTTP_method("GET")
                .setDescription("Visited start page");

        logger.save(dbLog);

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
        LogLevel logLevel = LogLevel.INFO;
        HttpStatus httpStatus = HttpStatus.OK;
        String authMessage = "Авторизация прошла успешно. Вы вошли, как пользователь:\n";
        String action = "Authorized as intended";

        user = userRepo.authenticate(login, pswd);

        if (user == null) {
            user = userRepo.getReferenceById(4);
            authMessage = "Пользователя с введенными данными нет в базе. \nВы авторизованы, как гость:\n";
            action = "Such ApiUser not found. Authorized as guest.";
            logLevel = LogLevel.WARN;
            httpStatus = HttpStatus.NOT_ACCEPTABLE;
        }

        Log dbLog = new Log()
                .setDateTime(ZonedDateTime.now())
                .setStatus(httpStatus)
                .setLevel(logLevel)
                .setUserLogin(user.getLogin())
                .setUserRole(user.getRole())
                .setHasAccess(true)
                .setResource("/api/auth")
                .setHTTP_method("GET")
                .setRequestParams("login=" + login + "&pswd=" + pswd)
                .setDescription(action);

        logger.save(dbLog);

        log.info("Authentication by " + user);

        return new ResponseEntity<>(authMessage + user.toString(), httpStatus);
    }

    @GetMapping("/api/auth/allUsers")
    public ResponseEntity<List<String>> getAllUsers() {
        Log dbLog = new Log()
                .setDateTime(ZonedDateTime.now())
                .setStatus(HttpStatus.OK)
                .setLevel(LogLevel.INFO)
                .setUserLogin(user.getLogin())
                .setUserRole(user.getRole())
                .setHasAccess(true)
                .setResource("/api/auth/allUsers")
                .setHTTP_method("GET");

        if (user.getRole().equals(AccessRole.ROLE_ADMIN)) {
            dbLog.setDescription("Printed all users list");
            logger.save(dbLog);

            log.info(user + " printed all users list.");

            return new ResponseEntity<>(userRepo.findAll().stream().map(ApiUser::toString).toList(), HttpStatus.OK);
        }

        dbLog.setLevel(LogLevel.WARN)
                .setStatus(HttpStatus.NOT_ACCEPTABLE)
                .setHasAccess(false)
                .setDescription("False try");
        logger.save(dbLog);

        return new ResponseEntity<>(new ArrayList<>(Collections.singleton(NO_ACCESS_MESSAGE)), HttpStatus.NOT_ACCEPTABLE);
    }

    @PostMapping("/api/auth/addUser")
    public ResponseEntity<String> addUser(@RequestParam String login, @RequestParam String pswd, @RequestParam int roleId) {
        Log dbLog = new Log()
                .setDateTime(ZonedDateTime.now())
                .setStatus(HttpStatus.CREATED)
                .setLevel(LogLevel.INFO)
                .setUserLogin(user.getLogin())
                .setUserRole(user.getRole())
                .setHasAccess(true)
                .setResource("/api/auth/addUser")
                .setHTTP_method("POST")
                .setRequestParams("login=" + login + "&pswd=" + pswd + "&roleId=" + roleId)
                .setDescription("Created new ApiUser");

        ResponseEntity<String> checkResult = checkAccessRole("/api/auth/addUser", AccessRole.ROLE_ADMIN, " Tried to get resource ", dbLog);

        if (checkResult != null) {
            return checkResult;
        }

        if (userRepo.userCountByLogin(login) > 0) {
            dbLog.setStatus(HttpStatus.NOT_ACCEPTABLE)
                    .setLevel(LogLevel.WARN)
                    .setHasAccess(true)
                    .setDescription("Tried to create new ApiUser. Not unique login.");
            logger.save(dbLog);

            log.info(user + " tried to create new user: login=" + login + "; pswd=" + pswd + "; roleId=" + roleId + ".");
            return new ResponseEntity<>("Пользователь с таким логином уже существует", HttpStatus.NOT_ACCEPTABLE);
        }

        ApiUser newUser = new ApiUser(login, pswd, AccessRole.values()[roleId]);

        logger.save(dbLog);
        log.info(newUser + " created by " + user);

        return new ResponseEntity<>(userRepo.save(newUser).toString(), HttpStatus.CREATED);
    }

    public ResponseEntity<String> getResource(String path, AccessRole accessRole) {
        Log dbLog = new Log()
                .setDateTime(ZonedDateTime.now())
                .setStatus(HttpStatus.OK)
                .setLevel(LogLevel.INFO)
                .setUserLogin(user.getLogin())
                .setUserRole(user.getRole())
                .setHasAccess(true)
                .setResource("/api" + path)
                .setHTTP_method("GET")
                .setDescription("Got resource(s)");

        ResponseEntity<String> checkResult = checkAccessRole(path, accessRole, " Tried to get resource ", dbLog);

        if (checkResult != null) {
            return checkResult;
        }

        logger.save(dbLog);

        log.info(getUser() + " got resource(s) " + path + ".");

        return REST_TEMPLATE.getForEntity(RESOURCE_URL + path, String.class);
    }


    public ResponseEntity<String> postResource(String path, Object body, AccessRole accessRole) {
        Log dbLog = new Log()
                .setDateTime(ZonedDateTime.now())
                .setLevel(LogLevel.INFO)
                .setUserLogin(user.getLogin())
                .setUserRole(user.getRole())
                .setHasAccess(true)
                .setResource("/api" + path)
                .setRequestBody(body.toString())
                .setHTTP_method("POST")
                .setDescription("Posted resource(s)");

        ResponseEntity<String> checkResult = checkAccessRole(path, accessRole, " Tried to post resource ", dbLog);

        if (checkResult != null) {
            return checkResult;
        }

        ResponseEntity<String> addedResource = REST_TEMPLATE.postForEntity(RESOURCE_URL + path, body, String.class);

        HttpStatus httpStatus = HttpStatus.resolve(addedResource.getStatusCode().value());
        dbLog.setStatus(httpStatus != null ? httpStatus : HttpStatus.EXPECTATION_FAILED);
        logger.save(dbLog);

        log.info(getUser() + " Posted resource " + addedResource.getBody() + ".");

        return addedResource;
    }

    public ResponseEntity<String> putResource(String path, int resourceId, Object body, AccessRole accessRole) {
        Log dbLog = new Log()
                .setDateTime(ZonedDateTime.now())
                .setLevel(LogLevel.INFO)
                .setUserLogin(user.getLogin())
                .setUserRole(user.getRole())
                .setHasAccess(true)
                .setResource("/api" + path)
                .setRequestBody(body.toString())
                .setHTTP_method("Put")
                .setDescription("Updated resource(s)");

        ResponseEntity<String> checkResult = checkAccessRole(path, accessRole, " Tried to update resource ", dbLog);
        if (checkResult != null) {
            return checkResult;
        }

        HttpEntity<Object> entity = new HttpEntity<>(body);
        ResponseEntity<String> updatedResource = REST_TEMPLATE.exchange(RESOURCE_URL + path + resourceId,
                HttpMethod.PUT,
                entity,
                String.class,
                resourceId);

        HttpStatus httpStatus = HttpStatus.resolve(updatedResource.getStatusCode().value());
        dbLog.setStatus(httpStatus != null ? httpStatus : HttpStatus.EXPECTATION_FAILED);
        logger.save(dbLog);

        log.info(getUser() + " updated resource " + updatedResource.getBody() + ".");

        return updatedResource;
    }

    public ResponseEntity<String> deleteResource(String path, int resourceId, AccessRole accessRole) {
        Log dbLog = new Log()
                .setDateTime(ZonedDateTime.now())
                .setLevel(LogLevel.INFO)
                .setUserLogin(user.getLogin())
                .setUserRole(user.getRole())
                .setHasAccess(true)
                .setResource("/api" + path + resourceId)
                .setHTTP_method("DELETE")
                .setDescription("Deleted resource(s)");

        ResponseEntity<String> checkResult = checkAccessRole(path + resourceId, accessRole, " Tried to delete resource ", dbLog);
        if (checkResult != null) {
            return checkResult;
        }

        dbLog.setStatus(HttpStatus.OK);
        logger.save(dbLog);

        log.info(getUser() + " deleted resource " + path + resourceId + ".");

        REST_TEMPLATE.delete(RESOURCE_URL + path + resourceId);
        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    public ResponseEntity<String> checkAccessRole(String path, AccessRole accessRole, String action, Log dbLog) {
        if (!user.getRole().equals(AccessRole.ROLE_ADMIN) && !user.getRole().equals(accessRole)) {
            dbLog.setStatus(HttpStatus.NOT_ACCEPTABLE)
                    .setLevel(LogLevel.WARN)
                    .setHasAccess(false)
                    .setDescription(action + path);
            logger.save(dbLog);

            log.warn(getUser() + action + path + ".");
            return new ResponseEntity<>(NO_ACCESS_MESSAGE, HttpStatus.NOT_ACCEPTABLE);
        }

        return null;
    }
}
