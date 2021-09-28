package com.teamherb.bookstoreback.common.utils.upload;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public abstract class FileUploader {

  public abstract String uploadFile(MultipartFile multipartFile);

  public abstract void deleteFile(String uploadFileUrl);

  public List<String> uploadFiles(List<MultipartFile> multipartFiles) {
    List<String> uploadFileResult = new ArrayList<>();
    for (MultipartFile multipartFile : multipartFiles) {
      uploadFileResult.add(uploadFile(multipartFile));
    }
    return uploadFileResult;
  }

  public void deleteFiles(List<String> uploadFileUrls) {
    for (String url : uploadFileUrls) {
      deleteFile(url);
    }
  }

  public String createUploadFileName(String originalFilename) {
    String uuid = UUID.randomUUID().toString();
    String ext = extractExt(originalFilename);
    return uuid + "." + ext;
  }

  public String extractExt(String originalFilename) {
    int pos = originalFilename.lastIndexOf(".");
    return originalFilename.substring(pos + 1);
  }
}
