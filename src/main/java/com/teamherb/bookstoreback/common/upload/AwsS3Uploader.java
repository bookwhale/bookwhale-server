package com.teamherb.bookstoreback.common.upload;

import static java.lang.String.valueOf;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class AwsS3Uploader extends FileUploader {

  private final AmazonS3 amazonS3;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  @Override
  public String uploadFile(MultipartFile multipartFile) {
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentType(multipartFile.getContentType());
    String uploadFileName = createUploadFileName(multipartFile.getOriginalFilename());
    try {
      amazonS3.putObject(
          new PutObjectRequest(bucket, uploadFileName, multipartFile.getInputStream(),
              objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
    } catch (IOException e) {
      log.error("IOException : {}", e.getMessage());
      throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
    return valueOf(amazonS3.getUrl(bucket, uploadFileName));
  }

  public void deleteFile(String uploadFileUrl) {
    String uploadFileName = extractUploadFileName(uploadFileUrl);
    amazonS3.deleteObject(new DeleteObjectRequest(bucket, uploadFileName));
  }

  public static String extractUploadFileName(String uploadFileUrl) {
    int pos = uploadFileUrl.lastIndexOf("/");
    return uploadFileUrl.substring(pos + 1);
  }
}
