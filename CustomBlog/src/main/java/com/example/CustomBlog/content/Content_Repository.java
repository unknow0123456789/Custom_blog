package com.example.CustomBlog.content;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Content_Repository extends JpaRepository<Content,Long>  {

    Optional<Content> findByTitle(String title);
    List<Content> findByTitleContainingIgnoreCaseAndPendingIsFalse(String search);

    List<Content> findTop30ByPendingFalseOrderByViewsDesc();
    List<Content> findTop30ByPendingFalseOrderByLastUploadDesc();

    List<Content> findByPendingIsFalse();
    List<Content> findByPendingIsTrue();


}
