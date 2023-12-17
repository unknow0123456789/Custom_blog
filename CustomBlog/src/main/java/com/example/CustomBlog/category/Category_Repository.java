package com.example.CustomBlog.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Category_Repository extends JpaRepository<Category,Long> {
    Optional<Category> findByName(String CategoryName);
}
