package com.ruttu.project_02_backend.service.feed;

import com.ruttu.project_02_backend.dto.feed.PostAllDto;
import com.ruttu.project_02_backend.dto.feed.PostDetailDto;
import com.ruttu.project_02_backend.dto.feed.PostDto;
import com.ruttu.project_02_backend.entity.prod.feed.PostEntity;
import com.ruttu.project_02_backend.entity.prod.user.UserEntity;
import com.ruttu.project_02_backend.exception.feed.AccessDeniedException;
import com.ruttu.project_02_backend.exception.feed.PostNotFoundException;
import com.ruttu.project_02_backend.exception.user.UserNotFoundException;
import com.ruttu.project_02_backend.repository.prod.feed.PostRepository;
import com.ruttu.project_02_backend.repository.prod.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostFileService postFileService;
    private final UserRepository userRepository;
    @Transactional
    // 게시글 작성
    public void createPost(Long userId, PostDto postDto, MultipartFile file) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        String imageUrl = null;

        if (file != null && !file.isEmpty()) {
            imageUrl = postFileService.saveFile(file);
        }

        PostEntity postEntity = PostEntity.builder()
                .userId(user.getId())
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .image(imageUrl)
                .transportType(postDto.getTransportType())
                .lineNumber(postDto.getLineNumber())
                .stationName(postDto.getStationName())
                .issueType(postDto.getIssueType())
                .build();

        postRepository.save(postEntity);
    }
    // 게시글 목록 조회(최신순/조회순)
    @Transactional(readOnly = true)
    public List<PostAllDto> getPosts(String sort) {
        List<PostEntity> posts;

        if ("view".equals(sort)) {
            posts = postRepository.findAllByOrderByViewCountDesc();
        } else {
            posts = postRepository.findAllByOrderByCreatedAtDesc();
        }

        return posts.stream()
                .map(PostAllDto::new)
                .toList();
    }
    // 게시글 상세 조회
    @Transactional
    public PostDetailDto getPostOne(Long postId) {

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new PostNotFoundException("게시글을 찾을 수 없습니다."));

        post.setViewCount(post.getViewCount() + 1);

        return postRepository.findById(postId)
                .map(PostDetailDto::new)
                .orElseThrow();
    }
    // 게시글 수정
    @Transactional
    public void updatePost(
            Long postId,
            Long userId,
            PostDto postDto,
            MultipartFile file
    ) {

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new PostNotFoundException("게시글을 찾을 수 없습니다."));

        if (!post.getUserId().equals(userId)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setTransportType(postDto.getTransportType());
        post.setLineNumber(postDto.getLineNumber());
        post.setStationName(postDto.getStationName());
        post.setIssueType(postDto.getIssueType());

        if (file != null && !file.isEmpty()) {
            String imageUrl = postFileService.saveFile(file);
            post.setImage(imageUrl);
        }
    }
    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId, Long userId) {

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new PostNotFoundException("게시글을 찾을 수 없습니다."));

        if (!post.getUserId().equals(userId)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        postFileService.deleteFile(post.getImage());

        postRepository.delete(post);
    }
    // 내가 쓴 글 조회
    @Transactional(readOnly = true)
    public List<PostAllDto> getMyPosts(Long userId) {

        return postRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(PostAllDto::new)
                .toList();
    }
}
