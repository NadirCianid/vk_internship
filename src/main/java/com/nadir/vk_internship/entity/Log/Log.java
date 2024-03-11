package com.nadir.vk_internship.entity.Log;

import com.nadir.vk_internship.entity.AccessRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

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
    private String HTTPMethod;
    @Column(length = 1023)
    private String description;


    public void setStatus(HttpStatus status) {
        this.status = status.toString();
    }

    public void setLevel(LogLevel logLevel) {
        this.level = logLevel.name();
    }

    public void setUserRole(AccessRole userRole) {
        this.userRole = userRole.name();
    }
}
