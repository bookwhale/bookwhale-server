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

  private final ImageRepository imageRepository;

  private final FileStoreUtil fileStoreUtil;

  private final InterestRepository interestRepository;

  public Long createPost(User user, PostRequest postRequest, List<MultipartFile> images) {
    Post post = Post.create(user, postRequest);
    saveImages(post, images);
    return postRepository.save(post).getId();
  }

  @Transactional(readOnly = true)
  public PostResponse findPost(User user, Long postId) {
    Post post = validatePostIdAndGetPostWithSeller(postId);
    boolean isMyPost = post.isMyPost(user);
    boolean isMyInterest = interestRepository.existsByUserAndPost(user, post);
    return PostResponse.of(post, isMyPost, isMyInterest);
  }

  public Post validatePostIdAndGetPostWithSeller(Long postId) {
    return postRepository.findWithSellerById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_POST_ID));
  }

  @Transactional(readOnly = true)
  public List<PostsResponse> findPosts(PostsRequest request, Pagination pagination) {
    PageRequest pageable = PageRequest.of(pagination.getPage(), pagination.getSize());
    List<Post> posts = postRepository.findAllByPostsReqOrderByCreatedDateDesc(request, pageable)
        .getContent();
    return posts.stream().map(p -> {
          List<Image> images = p.getImages().getImages();
          /*
          사용자가 게시글에 책 이미지를 등록하지 않았을 경우 대표 이미지는 null 로 반환한다.
          사용자가 게시글에 책 이미지를 등록한 경우 대표 이미지는 첫 번째 이미지를 반환한다.
          */
          return PostsResponse.of(p, images.isEmpty() ? null : images.get(0).getPath());
        })
        .collect(Collectors.toList());
  }

  //TODO : 게시글 업데이트, 게시글 삭제, API 문서, 전체 테스트, 게시글 전반적인 코드 정리, S3 연동
  public void updatePost(User user, Long postId, PostUpdateRequest request,
      List<MultipartFile> updateImages) {
    Post post = validatePostIdAndGetPost(postId);
    post.validateIsMyPost(user);
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
    List<String> imagePaths = getUploadImagePaths(images);
    if (imagePaths != null) {
      post.getImages().addImages(post, imagePaths);
    }
  }

  public List<String> getUploadImagePaths(List<MultipartFile> images) {
    return images == null || images.isEmpty() ? null : fileStoreUtil.storeFiles(images);
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
}
