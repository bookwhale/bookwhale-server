package com.teamherb.bookstoreback.post.controller;

import com.teamherb.bookstoreback.post.dto.PostRequest;
import com.teamherb.bookstoreback.post.service.PostService;
import com.teamherb.bookstoreback.security.CurrentUser;
import com.teamherb.bookstoreback.user.domain.User;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Void> createPost(@CurrentUser User user,
        @Valid @RequestPart("postRequest") PostRequest postRequest,
        @RequestPart(name = "images", required = false) List<MultipartFile> images) throws URISyntaxException {
        Long postId = postService.createPost(user, postRequest, images);
        return ResponseEntity.created(new URI("/api/post/" + postId)).build();
    }
}
