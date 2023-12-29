package com.example.CustomBlog.category;

import com.example.CustomBlog.content.Content;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Category_Repository extends JpaRepository<Category,Long> {
    Optional<Category> findByName(String CategoryName);

    Optional<Category> findByNameContainingIgnoreCase(String search);
}
