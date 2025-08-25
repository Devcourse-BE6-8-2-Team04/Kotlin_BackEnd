package com.team04.back.domain.image;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/images")
public class ImageUploadController {

    private final String uploadDir = System.getProperty("user.dir") + "/uploaded-images";

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 비어 있습니다.");
        }

        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + ext;
        File dest = new File(uploadDir, filename);
        dest.getParentFile().mkdirs();
        file.transferTo(dest);

        // 서버 내 파일 경로를 반환 (실제 서비스에서는 도메인 포함 URL 권장)
        String imageUrl = "/api/v1/images/file/" + filename;
        return ResponseEntity.ok(Map.of("url", imageUrl));
    }

    @GetMapping("/file/{filename}")
    public ResponseEntity<?> getImage(@PathVariable String filename) throws IOException {
        File file = new File(uploadDir, filename);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        // Content-Type은 실제 이미지 확장자에 따라 동적으로 처리 필요함
        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg")
                .body(java.nio.file.Files.readAllBytes(file.toPath()));
    }
}