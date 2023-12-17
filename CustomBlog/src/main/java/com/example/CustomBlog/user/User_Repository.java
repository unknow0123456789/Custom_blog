package com.example.CustomBlog.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface User_Repository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String username);
}
