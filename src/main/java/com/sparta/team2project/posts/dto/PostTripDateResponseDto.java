package com.sparta.team2project.posts.dto;

import com.sparta.team2project.posts.entity.PostCategory;
import com.sparta.team2project.tripdate.entity.TripDate;

import java.util.List;
import java.util.stream.Collectors;

public class PostTripDateResponseDto {
    private final String title;
    private final String contents;
    private final PostCategory postCategory;
    private final List<String> tagslist;
    private final List<TripDateOnlyResponseDto> tripDateList;

    public PostTripDateResponseDto(TotalRequestDto requestDto, List<TripDateOnlyResponseDto> tripDateOnlyResponseDtoList) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
        this.postCategory = requestDto.getPostCategory();
        this.tagslist = requestDto.getTagsList();
        this.tripDateList = tripDateOnlyResponseDtoList;
    }

}
