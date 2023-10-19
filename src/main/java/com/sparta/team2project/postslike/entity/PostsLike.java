package com.sparta.team2project.postslike.entity;

import com.sparta.team2project.posts.entity.Posts;
import com.sparta.team2project.users.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "posts_like")
public class PostsLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="posts_id")
    private Posts posts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users users;

    public PostsLike(Posts posts, Users users) {
        this.posts = posts;
        this.users = users;

    }
}
