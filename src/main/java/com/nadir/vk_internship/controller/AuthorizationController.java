package com.nadir.vk_internship.controller;

import com.nadir.vk_internship.entity.AccessRole;
import com.nadir.vk_internship.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthorizationController {
    private static final String RESOURCE_URL = "https://jsonplaceholder.typicode.com";
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    static void checkUserAuth() {
        if (ApiController.getUser() == null) {
            ApiController.setUser(new User("guest", "1111", AccessRole.ROLE_GUEST));
        }
    }

    public static String getAllResources(String resources, AccessRole accessRole) {
        checkUserAuth();

        User user = ApiController.getUser();

        if(user.getRole().equals(AccessRole.ROLE_ADMIN) || user.getRole().equals(accessRole)) {
            String remoteUrl = RESOURCE_URL + resources;

            return REST_TEMPLATE.getForObject(remoteUrl, String.class);
        } else {
            return "This page isn't available for yours role!";
        }
    }
}
