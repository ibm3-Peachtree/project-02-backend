package com.ruttu.project_02_backend.controller.feed;

import com.ruttu.project_02_backend.config.CustomUserDetails;
import com.ruttu.project_02_backend.dto.feed.PostAllDto;
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
            @RequestParam(value = "image", required = false) MultipartFile file
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
            description = "게시판 목록 조회"
    )
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<List<PostAllDto>> getPosts() {
        return ResponseEntity.ok(postService.getPosts());
    }
}
