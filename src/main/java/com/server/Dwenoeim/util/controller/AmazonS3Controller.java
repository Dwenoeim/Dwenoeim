package com.server.Dwenoeim.util.controller;

import com.server.Dwenoeim.domain.User;
import com.server.Dwenoeim.dto.FileResponse;
import com.server.Dwenoeim.dto.FileUploadResponse;
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

    @PostMapping
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") List<MultipartFile> multipartFiles){
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return ResponseEntity.badRequest().body(new FileUploadResponse("파일이 없습니다.", null));
        }

        List<String> fileUrls = awsS3Service.uploadFile(multipartFiles);
        FileUploadResponse responseDto = new FileUploadResponse("등록이 완료되었습니다.", fileUrls);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @DeleteMapping
    public ResponseEntity<String> deleteFile(@RequestParam("fileName") String fileName){
        awsS3Service.deleteFile(fileName);
        return ResponseEntity.ok(fileName);
    }

    @GetMapping("/view")
    public ResponseEntity<FileResponse> getFileUrl(@RequestParam String fileName) {
        String fileUrl = awsS3Service.getFileUrl(fileName);
        return ResponseEntity.status(HttpStatus.OK).body(new FileResponse(fileUrl));
    }



}
