package com.teamherb.bookstoreback.post.controller;

import com.teamherb.bookstoreback.post.dto.BookResponse;
import com.teamherb.bookstoreback.post.dto.NaverBookRequest;
import com.teamherb.bookstoreback.post.service.NaverBookAPIService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final NaverBookAPIService naverBookAPIService;

    @PostMapping("/naverBookAPI")
    public ResponseEntity<List<BookResponse>> findNaverBooks(
        @RequestBody NaverBookRequest naverBookRequest) {
        List<BookResponse> bookResponses = naverBookAPIService.getNaverBooks(naverBookRequest);
        return ResponseEntity.ok(bookResponses);
    }
}