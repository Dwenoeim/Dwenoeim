package com.server.Dwenoeim.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.server.Dwenoeim.domain.Image;
import com.server.Dwenoeim.domain.User;
import com.server.Dwenoeim.repository.ImageRepository;
import com.server.Dwenoeim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AwsS3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    public List<String> uploadFile(List<MultipartFile> multipartFiles, Long userId){
        List<String> fileUrlList = new ArrayList<>();

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // forEach 구문을 통해 multipartFiles 리스트로 넘어온 파일들을 순차적으로 fileNameList 에 추가
        multipartFiles.forEach(file -> {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try(InputStream inputStream = file.getInputStream()){
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));

                String fileUrl =amazonS3.getUrl(bucket,fileName).toString();
                fileUrlList.add(fileUrl);

                Image image = Image.builder()
                        .url(fileUrl)
                        .user(user)
                        .build();

                imageRepository.save(image); //??


            } catch (IOException e){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
            }

        });

        return fileUrlList;
    }

    // 파일명을 난수화하기 위해 UUID 를 활용하여 난수를 돌린다.
    public String createFileName(String fileName){
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    //  "."의 존재 유무만 판단
    private String getFileExtension(String fileName){
        try{
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일" + fileName + ") 입니다.");
        }
    }


    public void deleteFile(String fileName){
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
        System.out.println("delete "+fileName);
    }

    public String getFileUrl(String fileName) {
        // S3에 파일 존재 여부 확인
        if (!amazonS3.doesObjectExist(bucket, fileName)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 파일이 존재하지 않습니다: " + fileName);
        }
        // 존재하면 해당 파일의 URL 반환
        return amazonS3.getUrl(bucket, fileName).toString();
    }


    //user의 사진url list 검색
    public List<String> getUserImages(Long userId) {
        // 사용자 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 사용자의 이미지 리스트 조회
        List<Image> images = imageRepository.findByUser(user);

        // 이미지 URL 리스트로 변환
        return images.stream()
                .map(Image::getUrl)
                .collect(Collectors.toList());
    }
}
