package com.teamherb.bookstoreback.post.controller;

import com.teamherb.bookstoreback.post.dto.NaverBookRequest;
import com.teamherb.bookstoreback.post.dto.SearchBook;
import com.teamherb.bookstoreback.post.service.NaverBookAPIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

    private final NaverBookAPIService naverBookAPIService;

    @PostMapping("/NaverBookAPI")
    public ResponseEntity<SearchBook> findNaverBooks(
        @RequestBody NaverBookRequest naverBookRequest) {
        String XmlString = naverBookAPIService.getNaverBooksXml(naverBookRequest);
        return null;
    }
}