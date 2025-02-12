package com.server.Dwenoeim.util.controller;

import com.server.Dwenoeim.domain.User;
import com.server.Dwenoeim.service.AwsS3Service;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<String>> uploadFile(@RequestParam("file") List<MultipartFile> multipartFiles){
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(awsS3Service.uploadFile(multipartFiles));
    }


    @DeleteMapping
    public ResponseEntity<String> deleteFile(@RequestParam("fileName") String fileName){
        awsS3Service.deleteFile(fileName);
        return ResponseEntity.ok(fileName);
    }

    @GetMapping("/view")
    public ResponseEntity<String> getFileUrl(@RequestParam String fileName) {
        String fileUrl = awsS3Service.getFileUrl(fileName);
        return ResponseEntity.ok(fileUrl);
    }



}
