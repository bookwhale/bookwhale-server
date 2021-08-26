package com.teamherb.bookstoreback.post.controller;

import com.teamherb.bookstoreback.post.dto.BookResponse;
import com.teamherb.bookstoreback.post.dto.NaverBookRequest;
import com.teamherb.bookstoreback.post.service.NaverBookAPIService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final NaverBookAPIService naverBookAPIService;

    @GetMapping("/naverBookAPI")
    public ResponseEntity<List<BookResponse>> findNaverBooks(
        @ModelAttribute NaverBookRequest naverBookRequest) {
        List<BookResponse> bookResponses = naverBookAPIService.getNaverBooks(naverBookRequest);
        return ResponseEntity.ok(bookResponses);
    }
}