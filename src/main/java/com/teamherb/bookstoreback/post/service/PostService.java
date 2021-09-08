package com.teamherb.bookstoreback.post.service;

import com.teamherb.bookstoreback.common.Pagination;
import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.common.utils.upload.FileStoreUtil;
import com.teamherb.bookstoreback.image.domain.Image;
import com.teamherb.bookstoreback.image.domain.ImageRepository;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.post.domain.PostRepository;
import com.teamherb.bookstoreback.post.domain.PostStatus;
import com.teamherb.bookstoreback.post.dto.FullPostRequest;
import com.teamherb.bookstoreback.post.dto.FullPostResponse;
import com.teamherb.bookstoreback.post.dto.PostRequest;
import com.teamherb.bookstoreback.post.dto.PostResponse;
import com.teamherb.bookstoreback.post.dto.StatusChangeRequest;
import com.teamherb.bookstoreback.user.domain.User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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

    @Transactional(readOnly = true)
    public PostResponse findPost(User user, Long postId) {
        Post findPost = postRepository.findById(postId)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_POST_ID));

        List<Image> findImages = imageRepository.findAllByPost(findPost);

        return PostResponse.of(findPost, findImages, findPost.isMyPost(user));
    }

    @Transactional(readOnly = true)
    public List<FullPostResponse> findPosts(FullPostRequest req, Pagination pagination) {
        PageRequest pageable = PageRequest.of(pagination.getPage(), pagination.getSize());
        return postRepository.findAllByFullPostReqOrderByCreatedDateDesc(req, pageable)
            .getContent();
    }

    @Transactional
    public void changeStatus(StatusChangeRequest req) {
        Optional<Post> res = postRepository.findById(req.getId());
        res.ifPresent(post -> {
            boolean result = post.changeStatus(req.getStatus());
            postRepository.save(res.get());
        });
    }
}
