package com.bookwhale.post.controller;

import com.bookwhale.dto.Pagination;
import com.bookwhale.post.dto.BookResponse;
import com.bookwhale.common.dto.ConditionListResponse;
import com.bookwhale.post.dto.NaverBookRequest;
import com.bookwhale.post.dto.PostRequest;
import com.bookwhale.post.dto.PostResponse;
import com.bookwhale.post.dto.PostStatusUpdateRequest;
import com.bookwhale.post.dto.PostUpdateRequest;
import com.bookwhale.post.dto.PostsRequest;
import com.bookwhale.post.dto.PostsResponse;
import com.bookwhale.post.service.NaverBookAPIService;
import com.bookwhale.post.service.PostService;
import com.bookwhale.security.CurrentUser;
import com.bookwhale.user.domain.User;
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
        @PathVariable Long postId,
        @Valid @RequestPart("postUpdateRequest") PostUpdateRequest request,
        @RequestPart(name = "images", required = false) List<MultipartFile> images) {
        postService.updatePost(user, postId, request, images);
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

    @GetMapping("/conditions/bookStatus")
    public ResponseEntity<List<ConditionListResponse>> getAllBookStatus() {
        return ResponseEntity.ok(ConditionListResponse.listOfBookStatus());
    }

    @GetMapping("/conditions/locations")
    public ResponseEntity<List<ConditionListResponse>> getAllSellingLocation() {
        return ResponseEntity.ok(ConditionListResponse.listOfSellingLocation());
    }

}
