package com.ruttu.project_02_backend.service.feed;

import com.ruttu.project_02_backend.exception.feed.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class PostFileService {
    private static final String DIR = "uploads/post/";
    // 이미지 저장
    public String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("이미지만 업로드 가능합니다.");
        }

        String fileName = UUID.randomUUID().toString() + "-"
                + file.getOriginalFilename();
        Path path = Paths.get(DIR + fileName);
        try {
            Files.createDirectories(path.getParent());
            file.transferTo(path);
        } catch (IOException e) {
            throw new FileUploadException("파일 저장 실패");
        }
        return "/uploads/post/" + fileName;
    }
    // 이미지 삭제
    public void deleteFile(String imageUrl) {

        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        try {
            String fileName = imageUrl.replace("/uploads/post/", "");
            Path path = Paths.get(DIR + fileName);

            Files.deleteIfExists(path);

        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 실패", e);
        }
    }
}
