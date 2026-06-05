package com.ruttu.project_02_backend.service.feed;

import com.ruttu.project_02_backend.dto.feed.PostAllDto;
import com.ruttu.project_02_backend.dto.feed.PostDto;
import com.ruttu.project_02_backend.entity.prod.feed.PostEntity;
import com.ruttu.project_02_backend.entity.prod.user.UserEntity;
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

    @Transactional(readOnly = true)
    public List<PostAllDto> getPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(PostAllDto::new)
                .toList();
    }
}
