package com.nadir.vk_internship.controller;

import com.nadir.vk_internship.entity.AccessRole;
import com.nadir.vk_internship.entity.ApiUser;
import com.nadir.vk_internship.repository.UserRepo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ApiController {
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final String RESOURCE_URL = "https://jsonplaceholder.typicode.com";
    private static final String NO_ACCESS_MESSAGE = "Эта страница не доступна для вашей роли!";
    private final UserRepo userRepo;
    @Getter
    @Setter
    private static ApiUser user;


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
        checkUserAuth();

        log.info("Authentication by " + user);

        if(user.getRole().equals(AccessRole.ROLE_GUEST)) {
            guestMessage = "Пользователя с введенными данными нет в базе. \nВы авторизованы, как гость:\n";
        }

        return new ResponseEntity<>(guestMessage + user.toString(), HttpStatus.OK);
    }

    @GetMapping("/api/auth/allUsers")
    private List<String> getAllUsers() {
        checkUserAuth();

        if(user.getRole().equals(AccessRole.ROLE_ADMIN)) {
            return userRepo.findAll().stream().map(ApiUser::toString).toList();
        }
        return  new ArrayList<>(Collections.singleton(NO_ACCESS_MESSAGE));
    }

    @PostMapping("/api/auth/addUser")
    private String addUser(@RequestParam String login, @RequestParam String pswd, @RequestParam int roleId) {
        checkUserAuth();

        if(!user.getRole().equals(AccessRole.ROLE_ADMIN)) {
            return  NO_ACCESS_MESSAGE;
        }

        if(userRepo.userCountByLogin(login) > 0) {
            return "Пользователь с таким логином уже существует";
        }

         return  userRepo.save(new ApiUser(login, pswd, AccessRole.values()[roleId])).toString();
    }

    static void checkUserAuth() {
        if (user == null) {
            user = new ApiUser("guest", "1111", AccessRole.ROLE_GUEST);
        }
    }

    public static ResponseEntity<String> getResource(String path, AccessRole accessRole) {
        checkUserAuth();

        if(!user.getRole().equals(AccessRole.ROLE_ADMIN) && !user.getRole().equals(accessRole)) {
            log.error(getUser() + " tried to get path " + path + ".");
            return new ResponseEntity<>(NO_ACCESS_MESSAGE, HttpStatus.OK);
        }

        return REST_TEMPLATE.getForEntity(RESOURCE_URL + path, String.class);
    }


    public static ResponseEntity<String> postResource(String path, Object body, AccessRole accessRole) {
        checkUserAuth();

        if(!user.getRole().equals(AccessRole.ROLE_ADMIN) && !user.getRole().equals(accessRole)) {
            log.error(getUser() + " tried to get path " + path + ".");
            return new ResponseEntity<>(NO_ACCESS_MESSAGE, HttpStatus.OK);
        }

        return REST_TEMPLATE.postForEntity(RESOURCE_URL + path, body, String.class);
    }
}
