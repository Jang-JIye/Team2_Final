package com.sparta.team2project.replies.controller;

import com.sparta.team2project.commons.dto.MessageResponseDto;
import com.sparta.team2project.commons.security.UserDetailsImpl;
import com.sparta.team2project.replies.dto.RepliesMeResponseDto;
import com.sparta.team2project.replies.dto.RepliesRequestDto;
import com.sparta.team2project.replies.dto.RepliesResponseDto;
import com.sparta.team2project.replies.service.RepliesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "대댓글 관련 API", description = "대댓글 관련 API")
@RequiredArgsConstructor
public class RepliesController {
    private final RepliesService repliesService;

    // 대댓글 생성
    @Operation(summary = "대댓글 생성", description = "대댓글 생성 api 입니다.")
    @PostMapping("comments/{commentId}/replies")
    public ResponseEntity<MessageResponseDto> repliesCreate(@PathVariable("commentId") Long commentId,
                                                            @RequestBody RepliesRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(repliesService.repliesCreate(commentId, requestDto, userDetails.getUsers()));
    }

    // 대댓글 조회
    @Operation(summary = "댓글별 대댓글 조회", description = "댓글별 대댓글 조회 api 입니다.")
    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<Slice<RepliesResponseDto>> repliesList(@PathVariable("commentId") Long commentId,
                                                                 @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(repliesService.repliesList(commentId, pageable));
    }

    // 마이페이지에서 내가 쓴 대댓글 조회
    @Operation(summary = "사용자별 대댓글 조회", description = "사용자가 쓴 대댓글 조회 api 입니다.")
    @GetMapping("/repliesme")
    public ResponseEntity<Slice<RepliesMeResponseDto>> repliesMeList(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                     @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(repliesService.repliesMeList(userDetails.getUsers(), pageable));
    }

    // 대댓글 수정
    @Operation(summary = "대댓글 수정", description = "대댓글 수정 api 입니다.")
    @PutMapping("replies/{repliesId}")
    public ResponseEntity<MessageResponseDto> repliesUpdate( @PathVariable("repliesId") Long repliesId,
                                                             @RequestBody RepliesRequestDto request,
                                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(repliesService.repliesUpdate(repliesId, request, userDetails.getUsers()));
    }

    // 대댓글 삭제
    @Operation(summary = "대댓글 삭제", description = "대댓글 삭제 api 입니다.")
    @DeleteMapping("replies/{repliesId}")
    public ResponseEntity<MessageResponseDto> repliesDelete(@PathVariable("repliesId") Long repliesId,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(repliesService.repliesDelete(repliesId, userDetails.getUsers()));
    }
}
