package com.sparta.team2project.comments.repository;


import com.sparta.team2project.comments.entity.Comments;
import com.sparta.team2project.posts.entity.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments, Long> {

    List<Comments> findByPostsOrderByCreatedAtDesc(Posts posts);

    List<Comments> findByPosts(Posts posts);

    int countByPosts(Posts posts);

    Slice<Comments> findByPosts_IdOrderByCreatedAtDesc(Long postId, Pageable pageable);

    Slice<Comments> findByPosts_IdAndEmailOrderByCreatedAtDesc(Long postId, String email, Pageable pageable);
}

