package com.sparta.team2project.postslike.repository;

import com.sparta.team2project.posts.entity.Posts;
import com.sparta.team2project.postslike.entity.PostsLike;
import com.sparta.team2project.users.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostsLikeRepository extends JpaRepository<PostsLike,Long> {
    PostsLike findByPostsAndUsers(Posts posts, Users users);

    List<PostsLike> findByPosts(Posts posts);

    List<PostsLike> findByUsers(Users existUser);
}