package com.ruttu.project_02_backend.controller.feed;

import com.ruttu.project_02_backend.config.CustomUserDetails;
import com.ruttu.project_02_backend.dto.feed.PostAllDto;
import com.ruttu.project_02_backend.dto.feed.PostDetailDto;
import com.ruttu.project_02_backend.dto.feed.PostDto;
import com.ruttu.project_02_backend.service.feed.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@Tag(name = "Post API", description = "커뮤니티 API")
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "게시판 작성",
            description = "게시판 작성"
    )
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<Void> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ParameterObject @ModelAttribute PostDto postDto,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        System.out.println("POST CONTROLLER ENTER");
        System.out.println("userDetails : " + userDetails);

        postService.createPost(userDetails.getUserId(), postDto, file);

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping
    @Operation(
            summary = "게시판 목록 조회",
            description = "게시판 최신순/조회순으로 목록 조회"
    )
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<List<PostAllDto>> getPosts(
            @RequestParam(defaultValue = "latest") String sort
    ) {
        return ResponseEntity.ok(postService.getPosts(sort));
    }

    @GetMapping("/{postId}")
    @Operation(
            summary = "게시글 상세 조회",
            description = "게시글 상세 조회"
    )
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<PostDetailDto> getPostOne(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(postService.getPostOne(postId));
    }

    @PutMapping(
            value = "/{postId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
            summary = "게시글 수정",
            description = "게시글 수정"
    )
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<Void> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ParameterObject @ModelAttribute PostDto postDto,
            @RequestParam(value = "image", required = false)
            MultipartFile file
    ) {

        postService.updatePost(
                postId,
                userDetails.getUserId(),
                postDto,
                file
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}")
    @Operation(
            summary = "게시글 삭제",
            description = "게시글 삭제"
    )
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        postService.deletePost(postId, userDetails.getUserId());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(
            summary = "내가 작성한 게시글 조회",
            description = "내가 쓴 글 조회"
    )
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<List<PostAllDto>> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        return ResponseEntity.ok(
                postService.getMyPosts(userDetails.getUserId())
        );
    }
}
