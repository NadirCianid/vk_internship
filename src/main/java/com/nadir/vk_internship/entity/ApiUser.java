package com.nadir.vk_internship.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class ApiUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String login;

    private String pswd;

    private AccessRole role;

    public ApiUser(String login, String pswd, AccessRole role) {
        this.login = login;
        this.pswd = pswd;
        this.role = role;
    }

    public ApiUser(int id, String login, String pswd, AccessRole role) {
        this.id = id;
        this.login = login;
        this.pswd = pswd;
        this.role = role;
    }

    public ApiUser() {

    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", pswd='" + pswd + '\'' +
                ", role=" + role +
                '}';
    }
}
