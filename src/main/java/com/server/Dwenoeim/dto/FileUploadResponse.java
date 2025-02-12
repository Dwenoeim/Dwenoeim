package com.server.Dwenoeim.dto;

import com.server.Dwenoeim.domain.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FileUploadResponse {
    private String message;
    private List<String> fileName;

}


