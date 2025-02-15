package com.server.Dwenoeim.util.controller;

import com.server.Dwenoeim.domain.User;
import com.server.Dwenoeim.dto.FileResponse;
import com.server.Dwenoeim.dto.FileUploadResponse;
import com.server.Dwenoeim.dto.ImageResponse;
import com.server.Dwenoeim.service.AwsS3Service;
import io.jsonwebtoken.io.IOException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @Operation(summary = "사진 업로드", description = "유저의 사진 업로드",
            security = @SecurityRequirement(name = "Authorization"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(
            @Parameter(description = "사진 파일", required = true)
            @RequestPart("file") List<MultipartFile> multipartFiles,

            @Parameter(description = "유저 아이디", required = true, example = "123")
            @RequestPart("userId") Long userId){
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return ResponseEntity.badRequest().body(new FileUploadResponse("파일이 없습니다.", null));
        }

        List<String> fileUrls = awsS3Service.uploadFile(multipartFiles ,userId);
        FileUploadResponse responseDto = new FileUploadResponse("등록이 완료되었습니다.", fileUrls);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @DeleteMapping("/delete")  // 사진 삭제
    @Operation(summary="사진 삭제", description = "파일명으로 사진 하나를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<String> deleteFile(@RequestParam("fileName") String fileName){
        awsS3Service.deleteFile(fileName);
        return ResponseEntity.ok(fileName);
    }


    @Operation(summary="사진 url 한개 검색", description = "파일명으로 사진 하나를 검색")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/view") //파일 이름으로 사진url검색
    public ResponseEntity<FileResponse> getFileUrl(@RequestParam String fileName) {
        String fileUrl = awsS3Service.getFileUrl(fileName);
        return ResponseEntity.status(HttpStatus.OK).body(new FileResponse(fileUrl));
    }

    @Operation(summary = "사진 리스트(앨범) 조회", description = "유저id로 사진url 리스트 조회",
            security = @SecurityRequirement(name = "Authorization"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/user") //유저id로 사진url 리스트 검색
    public ResponseEntity<ImageResponse> getUserImages(
            @Parameter(description = "유저 아이디", required = true)
            @RequestParam("userId") Long userId) {
        List<String> userImages = awsS3Service.getUserImages(userId);
        return ResponseEntity.status(HttpStatus.OK).body(new ImageResponse(userImages));
    }


}
