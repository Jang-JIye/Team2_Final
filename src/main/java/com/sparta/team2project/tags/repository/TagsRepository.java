package com.sparta.team2project.tags.repository;

import com.sparta.team2project.posts.entity.Posts;
import com.sparta.team2project.tags.entity.Tags;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface TagsRepository extends JpaRepository<Tags, Long> {
     @EntityGraph(attributePaths = {"posts"})
     List<Tags> findByPosts(Posts post);


}
