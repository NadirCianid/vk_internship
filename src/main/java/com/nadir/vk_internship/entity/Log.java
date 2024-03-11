package com.nadir.vk_internship.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "logs")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(nullable = false)
    private ZonedDateTime dateTime;
    @Column(nullable = false, length = 31)
    private String status;
    @Column(nullable = false, length = 31)
    private String level;
    @Column(nullable = false)
    private String userLogin;
    @Column(nullable = false, length = 31)
    private String userRole;
    @Column(nullable = false)
    private boolean hasAccess;
    @Column(nullable = false)
    private String resource;
    private String requestParams;
    @Column(length = 1023)
    private String requestBody;
    @Column(length = 7)
    private String HTTP_method;
    @Column(length = 1023)
    private String description;

    public Log setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public Log setStatus(String status) {
        this.status = status;
        return this;
    }

    public Log setLevel(String level) {
        this.level = level;
        return this;
    }

    public Log setUserLogin(String userLogin) {
        this.userLogin = userLogin;
        return this;
    }

    public Log setUserRole(AccessRole userRole) {
        this.userRole = userRole.name();
        return this;
    }

    public Log setHasAccess(boolean hasAccess) {
        this.hasAccess = hasAccess;
        return this;
    }

    public Log setResource(String resource) {
        this.resource = resource;
        return this;
    }

    public Log setRequestParams(String requestParams) {
        this.requestParams = requestParams;
        return this;
    }

    public Log setRequestBody(String requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public Log setHTTP_method(String HTTP_method) {
        this.HTTP_method = HTTP_method;
        return this;
    }

    public Log setDescription(String description) {
        this.description = description;
        return this;
    }

}
