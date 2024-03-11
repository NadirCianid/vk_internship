package com.nadir.vk_internship.repository;

import com.nadir.vk_internship.entity.Log.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepo extends JpaRepository<Log, Integer> {


}