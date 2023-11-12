package com.sparta.team2project.posts.entity;

import com.sparta.team2project.commons.timestamped.TimeStamped;
import com.sparta.team2project.pictures.entity.Pictures;
import com.sparta.team2project.posts.dto.UpdateRequestDto;
import com.sparta.team2project.users.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = true)
    private String subTitle;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PostCategory postCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id",nullable = false)
    private Users users;

    // 사진모음(Pictures)와 양방향 관계
    @OneToMany(mappedBy = "posts", cascade = {CascadeType.REMOVE})
    private List<PostsPictures> postsPicturesList = new ArrayList<>(3);


    public Posts(String contents, String title, PostCategory postCategory,String subTitle,Users users) {
        this.contents = contents;
        this.title = title;
        this.postCategory = postCategory;
        this.subTitle =subTitle;
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
        this.subTitle =updateRequestDto.getSubTitle();
    }

    public void updateTime(UpdateRequestDto updateRequestDto, LocalDateTime postTime) {
        this.title =  updateRequestDto.getTitle();
        this.contents = updateRequestDto.getContents();
        this.postCategory = updateRequestDto.getPostCategory();
        this.subTitle =updateRequestDto.getSubTitle();
        this.setCreatedAt(postTime);
    }
}