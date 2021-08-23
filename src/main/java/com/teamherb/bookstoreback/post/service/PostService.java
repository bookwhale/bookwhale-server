package com.teamherb.bookstoreback.post.service;

import com.teamherb.bookstoreback.common.utils.upload.FileStoreUtil;
import com.teamherb.bookstoreback.image.domain.Image;
import com.teamherb.bookstoreback.image.domain.ImageRepository;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.post.domain.PostRepository;
import com.teamherb.bookstoreback.post.dto.PostRequest;
import com.teamherb.bookstoreback.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;

    private final ImageRepository imageRepository;

    private final FileStoreUtil fileStoreUtil;

    public Long createPost(User user, PostRequest postRequest, List<MultipartFile> images) {
        Post post = Post.create(user, postRequest);
        Post savedPost = postRepository.save(post);

        List<String> uploadFilePaths = getUploadFilePaths(images);
        if (uploadFilePaths != null) {
            List<Image> postImages = Image.createPostImage(savedPost, uploadFilePaths);
            imageRepository.saveAll(postImages);
        }
        return savedPost.getId();
    }

    private List<String> getUploadFilePaths(List<MultipartFile> files) {
        return files.size() > 0 ? fileStoreUtil.storeFiles(files) : null;
    }
}
