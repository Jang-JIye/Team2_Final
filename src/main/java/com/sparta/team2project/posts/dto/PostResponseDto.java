package com.sparta.team2project.posts.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.team2project.posts.entity.PostCategory;
import com.sparta.team2project.posts.entity.Posts;
import com.sparta.team2project.tags.entity.Tags;
import com.sparta.team2project.tripdate.entity.TripDate;
import com.sparta.team2project.users.Users;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponseDto {

    private final Long postId;
    private final String title;
    private final String contents;
    private final String nickName;
    private final LocalDateTime createdAt;
    private List<String> tagsList;
    private Integer likeNum;
    private Integer viewNum;
    private PostCategory postCategory;
    private Integer commentNum;
    private LocalDateTime modifiedAt;
    private List<Long> tripDateIdList;
    private List<String> subTitleList;
    private List<String> chosenDateList;

     // 전체 게시글 관련 반환시
    public PostResponseDto(Posts posts, List<Tags> tagsList,Users users,int commentNum){
        this.postId = posts.getId();
        this.title = posts.getTitle();
        this.tagsList = tagsList.stream().map(Tags::getPurpose).toList();
        this.contents = posts.getContents();
        this.nickName = users.getNickName();
        this.likeNum = posts.getLikeNum();
        this.viewNum =  posts.getViewNum();
        this.commentNum = commentNum;
        this.postCategory = posts.getPostCategory();
        this.createdAt = posts.getCreatedAt();
    }
    // 상세 게시글 관련 반환시
    public PostResponseDto(Posts posts, Users users, List<Tags> tagsList, int commentNum, LocalDateTime modifiedAt){
        this.postId = posts.getId();
        this.title = posts.getTitle();
        this.tagsList = tagsList.stream().map(Tags::getPurpose).toList();
        this.contents = posts.getContents();
        this.nickName = users.getNickName();
        this.likeNum = posts.getLikeNum();
        this.viewNum =  posts.getViewNum();
        this.commentNum = commentNum;
        this.postCategory = posts.getPostCategory();
        this.createdAt = posts.getCreatedAt();
        this.modifiedAt = modifiedAt;
    }

    // 사용자가 누른 게시물 관련 반환시
    public PostResponseDto(Posts posts,Users users){
        this.postId = posts.getId();
        this.title = posts.getTitle();
        this.contents = posts.getContents();
        this.nickName = users.getNickName();
        this.createdAt = posts.getCreatedAt();
    }

    // 마이페이지에서 사용자가 작성한 게시글 반환시
    public PostResponseDto(Posts posts, List<Tags> tag,Users users, int commentNum,List<TripDate> tripDateList){
        this.postId = posts.getId();
        this.title = posts.getTitle();
        this.tagsList = tag.stream().map(Tags::getPurpose).toList();
        this.contents = posts.getContents();
        this.nickName = users.getNickName();
        this.likeNum = posts.getLikeNum();
        this.viewNum =  posts.getViewNum();
        this.commentNum = commentNum;
        this.postCategory = posts.getPostCategory();
        this.createdAt = posts.getCreatedAt();
        this.tripDateIdList = tripDateList.stream().map(TripDate::getId).toList();
        this.subTitleList = tripDateList.stream().map(TripDate::getSubTitle).toList();
        this.chosenDateList = tripDateList.stream().map(TripDate::getChosenDate).map(LocalDate::toString).toList();
    }
}
