package com.nadir.vk_internship.repository;

import com.nadir.vk_internship.entity.ApiUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
@Component
@Repository
public interface UserRepo extends JpaRepository<ApiUser, Integer> {

    @Query(value = "SELECT * FROM users WHERE login=?1 AND pswd=?2",nativeQuery = true)
    ApiUser authenticate(String login, String pswd);

    @Query(value = "SELECT COUNT(1) FROM users WHERE login = ?1",nativeQuery = true)
    short userCountByLogin(String login);
}
