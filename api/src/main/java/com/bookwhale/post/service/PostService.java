package com.bookwhale.post.service;

import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.common.upload.FileUploader;
import com.bookwhale.dto.Pagination;
import com.bookwhale.image.domain.Images;
import com.bookwhale.like.domain.LikeRepository;
import com.bookwhale.post.domain.Post;
import com.bookwhale.post.domain.PostRepository;
import com.bookwhale.post.dto.PostRequest;
import com.bookwhale.post.dto.PostResponse;
import com.bookwhale.post.dto.PostStatusUpdateRequest;
import com.bookwhale.post.dto.PostUpdateRequest;
import com.bookwhale.post.dto.PostsRequest;
import com.bookwhale.post.dto.PostsResponse;
import com.bookwhale.user.domain.User;
import java.util.List;
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

    private final FileUploader fileUploader;

    private final LikeRepository likeRepository;

    public Long createPost(User user, PostRequest request, List<MultipartFile> images) {
        Post post = Post.create(user, request.toEntity());
        saveAllImages(post, images);
        return postRepository.save(post).getId();
    }

    public PostResponse findPost(User user, Long postId) {
        Post post = validatePostIdAndGetPostWithSeller(postId);
        post.increaseOneViewCount();
        return PostResponse.of(
            post,
            post.isMyPost(user),
            likeRepository.existsByUserAndPost(user, post)
        );
    }

    public Post validatePostIdAndGetPostWithSeller(Long postId) {
        return postRepository.findPostWithSellerById(postId)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_POST_ID));
    }

    @Transactional(readOnly = true)
    public List<PostsResponse> findPosts(PostsRequest req, Pagination pagination) {
        PageRequest pageable = PageRequest.of(pagination.getPage(), pagination.getSize());
        List<Post> posts = postRepository.findAllOrderByCreatedDateDesc(req.getTitle(),
                req.getAuthor(),
                req.getPublisher(), pageable)
            .getContent();
        return PostsResponse.listOf(posts);
    }

    public void updatePost(User user, Long postId, PostUpdateRequest request,
        List<MultipartFile> images) {
        Post post = getPostByPostId(postId);
        post.validateIsMyPost(user);
        post.update(request.toEntity());
        updateImages(post, images, request.getDeleteImgUrls());
    }

    public void updateImages(Post post, List<MultipartFile> images, List<String> deleteImgUrls) {
        if (deleteImgUrls != null && !deleteImgUrls.isEmpty()) {
            fileUploader.deleteFiles(deleteImgUrls);
            post.getImages().deleteImageUrls(deleteImgUrls);
        }
        saveAllImages(post, images);
    }

    public void saveAllImages(Post post, List<MultipartFile> images) {
        if (images != null && !images.isEmpty()) {
            List<String> uploadImageUrls = fileUploader.uploadFiles(images);
            post.getImages().addAll(post, uploadImageUrls);
        }
    }

    public void updatePostStatus(User user, Long postId, PostStatusUpdateRequest request) {
        Post post = getPostByPostId(postId);
        post.validateIsMyPost(user);
        post.updatePostStatus(request.getPostStatus());
    }

    public Post getPostByPostId(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_POST_ID));
    }

    public void deletePost(User user, Long postId) {
        Post post = getPostByPostId(postId);
        post.validateIsMyPost(user);
        deleteAllImages(post);
        postRepository.delete(post);
    }

    public void deleteAllImages(Post post) {
        Images images = post.getImages();
        if (!images.isEmpty()) {
            fileUploader.deleteFiles(images.getImageUrls());
            images.deleteAll();
        }
    }
}
