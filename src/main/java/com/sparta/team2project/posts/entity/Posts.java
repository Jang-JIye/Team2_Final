package com.sparta.team2project.posts.entity;

import com.sparta.team2project.commons.timestamped.TimeStamped;
import com.sparta.team2project.posts.dto.UpdateRequestDto;
import com.sparta.team2project.users.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor
public class Posts extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int likeNum;

    @Column(nullable = false)
    private int viewNum;


    @Column(nullable = true)
    private String title;

    @Column(nullable = true,length = 500)
    private String contents;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PostCategory postCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id",nullable = false)
    private Users users;


    public Posts(String contents, String title, PostCategory postCategory,Users users) {
        this.contents = contents;
        this.title = title;
        this.postCategory = postCategory;
        this.users = users;
    }

    public void unlike() {
        this.likeNum-=1;
    }

    public void like() {
        this.likeNum+=1;
    }

    public void viewCount(){this.viewNum+=1;}

    public void update(UpdateRequestDto updateRequestDto) {
        this.postCategory = updateRequestDto.getPostCategory();
        this.title =  updateRequestDto.getTitle();
        this.contents = updateRequestDto.getContents();
    }
}