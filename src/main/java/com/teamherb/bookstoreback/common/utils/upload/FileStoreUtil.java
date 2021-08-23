package com.teamherb.bookstoreback.common.utils.upload;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public abstract class FileStoreUtil {

    public abstract List<String> storeFiles(List<MultipartFile> multipartFiles);

    public abstract String storeFile(MultipartFile multipartFile);

    public String createStoreFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalFilename);
        return uuid + "." + ext;
    }

    public String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}
