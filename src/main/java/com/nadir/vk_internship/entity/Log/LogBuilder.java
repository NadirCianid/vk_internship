package com.nadir.vk_internship.entity.Log;

import com.nadir.vk_internship.entity.AccessRole;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public class LogBuilder {
    private ZonedDateTime dateTime;
    private HttpStatus status;
    private LogLevel level;
    private String userLogin;
    private AccessRole userRole;
    private boolean hasAccess;
    private String resource;
    private String requestParams;
    private String requestBody;
    private String HTTPMethod;
    private String description;

    public LogBuilder dateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public LogBuilder status(HttpStatus status) {
        this.status = status;
        return this;
    }

    public LogBuilder level(LogLevel logLevel) {
        this.level = logLevel;
        return this;
    }

    public LogBuilder userLogin(String userLogin) {
        this.userLogin = userLogin;
        return this;
    }

    public LogBuilder userRole(AccessRole userRole) {
        this.userRole = userRole;
        return this;
    }

    public LogBuilder hasAccess(boolean hasAccess) {
        this.hasAccess = hasAccess;
        return this;
    }

    public LogBuilder resource(String resource) {
        this.resource = resource;
        return this;
    }

    public LogBuilder requestParams(String requestParams) {
        this.requestParams = requestParams;
        return this;
    }

    public LogBuilder requestBody(String requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public LogBuilder HTTPMethod(String HTTPMethod) {
        this.HTTPMethod = HTTPMethod;
        return this;
    }

    public LogBuilder description(String description) {
        this.description = description;
        return this;
    }

    public Log build() {
        Log log = new Log();
        log.setDateTime(dateTime);
        log.setStatus(status);
        log.setLevel(level);
        log.setUserLogin(userLogin);
        log.setUserRole(userRole);
        log.setHasAccess(hasAccess);
        log.setResource(resource);
        log.setRequestParams(requestParams);
        log.setRequestBody(requestBody);
        log.setHTTPMethod(HTTPMethod);
        log.setDescription(description);
        return log;
    }
}
