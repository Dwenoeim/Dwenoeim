package com.server.Dwenoeim.util.controller;

import com.server.Dwenoeim.domain.User;
import com.server.Dwenoeim.dto.FileResponse;
import com.server.Dwenoeim.dto.FileUploadResponse;
import com.server.Dwenoeim.dto.ImageResponse;
import com.server.Dwenoeim.service.AwsS3Service;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class AmazonS3Controller {

    private final AwsS3Service awsS3Service;

    @PostMapping("/upload") // 사진 업로드
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") List<MultipartFile> multipartFiles,
                                                         @RequestParam("userId") Long userId){
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return ResponseEntity.badRequest().body(new FileUploadResponse("파일이 없습니다.", null));
        }

        List<String> fileUrls = awsS3Service.uploadFile(multipartFiles ,userId);
        FileUploadResponse responseDto = new FileUploadResponse("등록이 완료되었습니다.", fileUrls);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @DeleteMapping("/delete")  // 사진 삭제
    public ResponseEntity<String> deleteFile(@RequestParam("fileName") String fileName){
        awsS3Service.deleteFile(fileName);
        return ResponseEntity.ok(fileName);
    }

    @GetMapping("/view") //파일 이름으로 사진url검색
    public ResponseEntity<FileResponse> getFileUrl(@RequestParam String fileName) {
        String fileUrl = awsS3Service.getFileUrl(fileName);
        return ResponseEntity.status(HttpStatus.OK).body(new FileResponse(fileUrl));
    }

    @GetMapping("/user") //유저id로 사진url 리스트 검색
    public ResponseEntity<ImageResponse> getUserImages(@RequestParam("userId") Long userId) {
        List<String> userImages = awsS3Service.getUserImages(userId);
        return ResponseEntity.status(HttpStatus.OK).body(new ImageResponse(userImages));
    }


}
