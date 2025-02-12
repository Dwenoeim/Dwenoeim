package com.server.Dwenoeim.dto;

import com.server.Dwenoeim.domain.Image;

import java.util.List;

public record FileUploadResponse(boolean success, int code, String message, List<String> fileNames) {

    public static FileUploadResponse from(List<String> fileNames) {
        return new FileUploadResponse(true, 200, "파일 업로드 성공", fileNames);
    }

    public static FileUploadResponse error(String errorMessage) {
        return new FileUploadResponse(false, 500, errorMessage, null);
    }


}


