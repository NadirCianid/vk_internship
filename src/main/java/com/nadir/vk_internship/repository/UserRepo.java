package com.nadir.vk_internship.repository;

import com.nadir.vk_internship.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

    @Query(value = "SELECT * FROM users WHERE login=?1 AND pswd=?2",nativeQuery = true)
    User authenticate(String login, String pswd);
}
