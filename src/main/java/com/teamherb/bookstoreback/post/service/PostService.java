package com.teamherb.bookstoreback.post.service;

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

  public Long createPost(User user, PostRequest postRequest, List<MultipartFile> images) {
    Post newPost = Post.create(user, postRequest);
    Post savedPost = postRepository.save(newPost);
    saveImages(images, savedPost);
    return savedPost.getId();
  }

  @Transactional(readOnly = true)
  public PostResponse findPost(User user, Long postId) {
    Post findPost = postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_POST_ID));

    List<Image> findImages = imageRepository.findAllByPost(findPost);

    return PostResponse.of(findPost, findImages, findPost.isMyPost(user));
  }

  @Transactional(readOnly = true)
  public List<FullPostResponse> findPosts(FullPostRequest request, Pagination pagination) {
    PageRequest pageable = PageRequest.of(pagination.getPage(), pagination.getSize());
    return postRepository.findAllByFullPostReqOrderByCreatedDateDesc(request, pageable)
        .getContent();
  }

  public void updatePost(User user, PostUpdateRequest request, List<MultipartFile> updateImages) {
    Post findPost = postRepository.findById(request.getPostId())
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_POST_ID));

    if (!findPost.isMyPost(user)) {
      throw new CustomException(ErrorCode.USER_ACCESS_DENIED);
    }

    findPost.update(request);
    updateImages(findPost, updateImages);
  }

  public void updateImages(Post post, List<MultipartFile> updateImages) {
    imageRepository.deleteAllByPost(post);
    saveImages(updateImages, post);
  }

  public void saveImages(List<MultipartFile> images, Post post) {
    List<String> uploadFilePaths = getUploadFilePaths(images);
    if (uploadFilePaths != null) {
      List<Image> postImages = Image.createPostImage(post, uploadFilePaths);
      imageRepository.saveAll(postImages);
    }
  }

  public List<String> getUploadFilePaths(List<MultipartFile> images) {
    return images == null || images.isEmpty() ? null : fileStoreUtil.storeFiles(images);
  }
}
