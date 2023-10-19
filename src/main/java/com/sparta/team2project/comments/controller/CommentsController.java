package com.sparta.team2project.comments.controller;

import com.sparta.team2project.comments.dto.CommentsMeResponseDto;
import com.sparta.team2project.comments.dto.CommentsRequestDto;
import com.sparta.team2project.comments.dto.CommentsResponseDto;
import com.sparta.team2project.comments.service.CommentsService;
import com.sparta.team2project.commons.dto.MessageResponseDto;
import com.sparta.team2project.commons.security.UserDetailsImpl;
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
@Tag(name = "댓글 관련 API", description = "댓글 관련 API")
@RequiredArgsConstructor
public class CommentsController {
    private final CommentsService commentsService;

    // 댓글 생성
    @Operation(summary = "댓글 생성", description = "댓글 생성 api 입니다.")
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<MessageResponseDto> commentsCreate(@PathVariable("postId") Long postId,
                                                              @RequestBody CommentsRequestDto requestDto,
                                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(commentsService.commentsCreate(postId, requestDto, userDetails.getUsers()));
    }

    // 댓글 조회
    @Operation(summary = "게시글별 댓글 조회", description = "게시글별 댓글 조회 api 입니다.")
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Slice<CommentsResponseDto>> commentsList(@PathVariable("postId") Long postId,
                                                                   @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(commentsService.commentsList(postId, pageable));
    }

    // 마이페이지에서 내가 쓴 댓글 조회
    @Operation(summary = "사용자별 댓글 조회", description = "사용자가 쓴 댓글 조회 api 입니다.")
    @GetMapping("/commentsme")
    public ResponseEntity<Slice<CommentsMeResponseDto>> commentsMeList(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                       @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(commentsService.commentsMeList(userDetails.getUsers(), pageable));
    }

    // 댓글 수정
    @Operation(summary = "댓글 수정", description = "댓글 수정 api 입니다.")
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<MessageResponseDto> commentsUpdate( @PathVariable("commentId") Long commentId,
                                                              @RequestBody CommentsRequestDto request,
                                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
       return ResponseEntity.ok(commentsService.commentsUpdate( commentId, request, userDetails.getUsers()));
    }

    // 댓글 삭제
    @Operation(summary = "댓글 삭제", description = "댓글 삭제 api 입니다.")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<MessageResponseDto> commentsDelete(@PathVariable("commentId") Long commentId,
                                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(commentsService.commentsDelete(commentId, userDetails.getUsers()));
    }
}
