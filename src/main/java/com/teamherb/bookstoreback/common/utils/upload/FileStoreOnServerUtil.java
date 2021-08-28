package com.teamherb.bookstoreback.common.utils.upload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileStoreOnServerUtil extends FileStoreUtil {

    @Value("${app.file-upload.dir}")
    private String fileDir;

    public String getFullPath(String storeFileName) {
        return fileDir + storeFileName;
    }

    @Override
    public List<String> storeFiles(List<MultipartFile> multipartFiles) {
        List<String> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile));
            }
        }
        return storeFileResult;
    }

    @Override
    public String storeFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return null;
        }
        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);
        try {
            multipartFile.transferTo(new File(getFullPath(storeFileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return storeFileName;
    }
}
