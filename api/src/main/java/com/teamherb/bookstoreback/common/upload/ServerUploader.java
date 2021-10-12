package com.teamherb.bookstoreback.common.upload;

import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.ErrorCode;
import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class ServerUploader extends FileUploader {

  @Value("${app.file-upload.dir}")
  private String fileDir;

  public String getFullPath(String uploadFileName) {
    return fileDir + uploadFileName;
  }

  @Override
  public String uploadFile(MultipartFile multipartFile) {
    String originalFilename = multipartFile.getOriginalFilename();
    String uploadFileName = createUploadFileName(originalFilename);
    try {
      multipartFile.transferTo(new File(getFullPath(uploadFileName)));
    } catch (IOException e) {
      log.error("IOException : {}", e.getMessage());
      throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
    return uploadFileName;
  }

  @Override
  public void deleteFile(String uploadFileUrl) {
    // 사용하지 않습니다.
  }
}
