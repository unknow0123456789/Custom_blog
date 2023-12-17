package com.example.CustomBlog.web_information;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Web_Information_Repository extends JpaRepository<Web_Information,Long> {
    Optional<Web_Information> findByName(String name);
}
