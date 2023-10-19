package com.sparta.team2project.replies.dto;

import com.sparta.team2project.replies.entity.Replies;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RepliesMeResponseDto {
    private Long repliesId;
    private String contents;
    private String title;
    private LocalDateTime createAt;

    public RepliesMeResponseDto(Replies replies, String postTitle) {
        this.repliesId = replies.getId();
        this.contents = replies.getContents();
        this.title = postTitle;
        this.createAt = replies.getCreatedAt();
    }
}
