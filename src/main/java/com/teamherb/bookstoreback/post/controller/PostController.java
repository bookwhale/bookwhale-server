package com.teamherb.bookstoreback.post.controller;

import com.teamherb.bookstoreback.common.Pagination;
import com.teamherb.bookstoreback.post.dto.BookResponse;
import com.teamherb.bookstoreback.post.dto.NaverBookRequest;
import com.teamherb.bookstoreback.post.dto.PostRequest;
import com.teamherb.bookstoreback.post.dto.PostResponse;
import com.teamherb.bookstoreback.post.dto.PostStatusUpdateRequest;
import com.teamherb.bookstoreback.post.dto.PostUpdateRequest;
import com.teamherb.bookstoreback.post.dto.PostsRequest;
import com.teamherb.bookstoreback.post.dto.PostsResponse;
import com.teamherb.bookstoreback.post.service.NaverBookAPIService;
import com.teamherb.bookstoreback.post.service.PostService;
import com.teamherb.bookstoreback.security.CurrentUser;
import com.teamherb.bookstoreback.user.domain.User;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;

  private final NaverBookAPIService naverBookAPIService;

  @GetMapping("/naverBookAPI")
  public ResponseEntity<List<BookResponse>> findNaverBooks(
      @Valid @ModelAttribute NaverBookRequest naverBookRequest) {
    List<BookResponse> bookResponses = naverBookAPIService.getNaverBooks(naverBookRequest);
    return ResponseEntity.ok(bookResponses);
  }

  @PostMapping
  public ResponseEntity<Void> createPost(@CurrentUser User user,
      @Valid @RequestPart("postRequest") PostRequest postRequest,
      @RequestPart(name = "images") List<MultipartFile> images) throws URISyntaxException {
    Long postId = postService.createPost(user, postRequest, images);
    return ResponseEntity.created(new URI("/api/post/" + postId)).build();
  }

  @GetMapping("/{postId}")
  public ResponseEntity<PostResponse> findPost(@CurrentUser User user,
      @PathVariable Long postId) {
    return ResponseEntity.ok(postService.findPost(user, postId));
  }

  @GetMapping
  public ResponseEntity<List<PostsResponse>> findPosts(PostsRequest postsRequest,
      @Valid Pagination pagination) {
    return ResponseEntity.ok(postService.findPosts(postsRequest, pagination));
  }

  @PatchMapping("/{postId}")
  public ResponseEntity<Void> updatePost(@CurrentUser User user,
      @PathVariable Long postId, @Valid @RequestPart("postUpdateRequest") PostUpdateRequest request,
      @RequestPart(name = "updateImages") List<MultipartFile> updateImages) {
    postService.updatePost(user, postId, request, updateImages);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/postStatus/{postId}")
  public ResponseEntity<Void> updatePostStatus(@CurrentUser User user, @PathVariable Long postId,
      @Valid @RequestBody PostStatusUpdateRequest request) {
    postService.updatePostStatus(user, postId, request);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<Void> deletePost(@CurrentUser User user, @PathVariable Long postId) {
    postService.deletePost(user, postId);
    return ResponseEntity.ok().build();
  }
}
