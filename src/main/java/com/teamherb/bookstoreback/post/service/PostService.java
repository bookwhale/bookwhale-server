package com.teamherb.bookstoreback.post.service;

import com.teamherb.bookstoreback.Interest.domain.InterestRepository;
import com.teamherb.bookstoreback.common.Pagination;
import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.common.utils.upload.FileUploader;
import com.teamherb.bookstoreback.image.domain.Images;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.post.domain.PostRepository;
import com.teamherb.bookstoreback.post.dto.PostRequest;
import com.teamherb.bookstoreback.post.dto.PostResponse;
import com.teamherb.bookstoreback.post.dto.PostStatusUpdateRequest;
import com.teamherb.bookstoreback.post.dto.PostUpdateRequest;
import com.teamherb.bookstoreback.post.dto.PostsRequest;
import com.teamherb.bookstoreback.post.dto.PostsResponse;
import com.teamherb.bookstoreback.user.domain.User;
import java.util.List;
import java.util.stream.Collectors;
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

  private final InterestRepository interestRepository;

  public Long createPost(User user, PostRequest postRequest, List<MultipartFile> images) {
    Post post = Post.create(user, postRequest);
    saveAllImages(post, images);
    return postRepository.save(post).getId();
  }

  @Transactional(readOnly = true)
  public PostResponse findPost(User user, Long postId) {
    Post post = validatePostIdAndGetPostWithSeller(postId);
    return PostResponse.of(
        post,
        post.isMyPost(user),
        interestRepository.existsByUserAndPost(user, post)
    );
  }

  public Post validatePostIdAndGetPostWithSeller(Long postId) {
    return postRepository.findPostWithSellerById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_POST_ID));
  }

  @Transactional(readOnly = true)
  public List<PostsResponse> findPosts(PostsRequest request, Pagination pagination) {
    PageRequest pageable = PageRequest.of(pagination.getPage(), pagination.getSize());
    List<Post> posts = postRepository.findAllByPostsReqOrderByCreatedDateDesc(request, pageable)
        .getContent();
    return posts.stream().map(p -> PostsResponse.of(p, p.getImages().getFirstImageUrl()))
        .collect(Collectors.toList());
  }

  public void updatePost(User user, Long postId, PostUpdateRequest request,
      List<MultipartFile> updateImages) {
    Post post = validatePostIdAndGetPost(postId);
    post.validateIsMyPost(user);
    post.update(request);
    updateImages(post, updateImages);
  }

  public void updateImages(Post post, List<MultipartFile> updateImages) {
    deleteAllImages(post);
    saveAllImages(post, updateImages);
  }

  public void saveAllImages(Post post, List<MultipartFile> images) {
    List<String> uploadUrls = getImageUrlsAndUploadImages(images);
    if (uploadUrls != null) {
      post.getImages().addAll(post, uploadUrls);
    }
  }

  public List<String> getImageUrlsAndUploadImages(List<MultipartFile> images) {
    return images == null || images.isEmpty() ? null : fileUploader.uploadFiles(images);
  }

  public void updatePostStatus(User user, Long postId, PostStatusUpdateRequest request) {
    Post post = validatePostIdAndGetPost(postId);
    post.validateIsMyPost(user);
    post.updatePostStatus(request.getPostStatus());
  }

  public Post validatePostIdAndGetPost(Long postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_POST_ID));
  }

  public void deletePost(User user, Long postId) {
    Post post = validatePostIdAndGetPost(postId);
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
