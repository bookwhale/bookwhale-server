package com.teamherb.bookstoreback.post.service;

import com.teamherb.bookstoreback.Interest.domain.InterestRepository;
import com.teamherb.bookstoreback.common.Pagination;
import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.common.utils.upload.FileStoreUtil;
import com.teamherb.bookstoreback.image.domain.Image;
import com.teamherb.bookstoreback.image.domain.ImageRepository;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.post.domain.PostRepository;
import com.teamherb.bookstoreback.post.dto.FullPostRequest;
import com.teamherb.bookstoreback.post.dto.FullPostResponse;
import com.teamherb.bookstoreback.post.dto.PostRequest;
import com.teamherb.bookstoreback.post.dto.PostResponse;
import com.teamherb.bookstoreback.post.dto.PostStatusUpdateRequest;
import com.teamherb.bookstoreback.post.dto.PostUpdateRequest;
import com.teamherb.bookstoreback.user.domain.User;
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

  private final ImageRepository imageRepository;

  private final FileStoreUtil fileStoreUtil;

  private final InterestRepository interestRepository;

  public Long createPost(User loginUser, PostRequest postRequest, List<MultipartFile> images) {
    Post newPost = Post.create(loginUser, postRequest);
    Post savedPost = postRepository.save(newPost);
    saveImages(savedPost, images);
    return savedPost.getId();
  }

  @Transactional(readOnly = true)
  public PostResponse findPost(User loginUser, Long postId) {
    Post post = validatePostIdAndGetPostWithSeller(postId);
    List<Image> findImages = imageRepository.findAllByPost(post);
    boolean isMyPost = post.isMyPost(loginUser);
    boolean isMyInterest = interestRepository.existsByUserAndPost(loginUser, post);
    return PostResponse.of(post, findImages, isMyPost, isMyInterest);
  }

  public Post validatePostIdAndGetPostWithSeller(Long postId) {
    return postRepository.findWithSellerById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_POST_ID));
  }

  @Transactional(readOnly = true)
  public List<FullPostResponse> findPosts(FullPostRequest request, Pagination pagination) {
    PageRequest pageable = PageRequest.of(pagination.getPage(), pagination.getSize());
    return postRepository.findAllByFullPostReqOrderByCreatedDateDesc(request, pageable)
        .getContent();
  }

  public void updatePost(User loginUser, Long postId, PostUpdateRequest request,
      List<MultipartFile> updateImages) {
    Post post = validatePostIdAndGetPost(postId);
    post.validateIsMyPost(loginUser);
    post.update(request);
    updateImages(post, updateImages);
  }

  public void updateImages(Post post, List<MultipartFile> updateImages) {
    deleteImages(post);
    saveImages(post, updateImages);
  }

  public void deleteImages(Post post) {
    List<Image> images = imageRepository.findAllByPost(post);
    if (!images.isEmpty()) {
      //TODO : S3 연동하면 S3 이미지를 삭제하는 로직을 추가해야합니다.
      imageRepository.deleteAll(images);
    }
  }

  public void saveImages(Post post, List<MultipartFile> images) {
    List<String> uploadFilePaths = getUploadFilePaths(images);
    if (uploadFilePaths != null) {
      imageRepository.saveAll(Image.createPostImage(post, uploadFilePaths));
    }
  }

  public List<String> getUploadFilePaths(List<MultipartFile> images) {
    return images == null || images.isEmpty() ? null : fileStoreUtil.storeFiles(images);
  }

  public void updatePostStatus(User loginUser, Long postId, PostStatusUpdateRequest request) {
    Post post = validatePostIdAndGetPost(postId);
    post.validateIsMyPost(loginUser);
    post.updatePostStatus(request.getPostStatus());
  }

  public Post validatePostIdAndGetPost(Long postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_POST_ID));
  }
}
