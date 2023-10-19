package com.sparta.team2project.posts.dto;

import com.sparta.team2project.posts.entity.Posts;
import lombok.Getter;

import java.util.List;

@Getter
public class PostMessageResponseDto {
    private final Long postId;
    private final List<Long> tripDateId;
    private final String msg;
    private final int statusCode;

    public PostMessageResponseDto(String msg, int statusCode, Posts posts, List<Long> tripDateId){
        this.postId= posts.getId();
        this.tripDateId = tripDateId;
        this.msg = msg;
        this.statusCode = statusCode;
    }
}


