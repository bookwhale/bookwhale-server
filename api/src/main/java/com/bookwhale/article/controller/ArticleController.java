package com.bookwhale.article.controller;

import com.bookwhale.article.dto.ArticlesResponse;
import com.bookwhale.common.dto.Pagination;
import com.bookwhale.article.dto.BookResponse;
import com.bookwhale.common.dto.ConditionListResponse;
import com.bookwhale.article.dto.NaverBookRequest;
import com.bookwhale.article.dto.ArticleRequest;
import com.bookwhale.article.dto.ArticleResponse;
import com.bookwhale.article.dto.ArticleStatusUpdateRequest;
import com.bookwhale.article.dto.ArticleUpdateRequest;
import com.bookwhale.article.dto.ArticlesRequest;
import com.bookwhale.article.service.NaverBookAPIService;
import com.bookwhale.article.service.ArticleService;
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
@RequestMapping("/api/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    private final NaverBookAPIService naverBookAPIService;

    @GetMapping("/naverBookAPI")
    public ResponseEntity<List<BookResponse>> findNaverBooks(
        @Valid @ModelAttribute NaverBookRequest naverBookRequest) {
        List<BookResponse> bookResponses = naverBookAPIService.getNaverBooks(naverBookRequest);
        return ResponseEntity.ok(bookResponses);
    }

    @PostMapping
    public ResponseEntity<Void> createArticle(@CurrentUser User user,
        @Valid @RequestPart("articleRequest") ArticleRequest articleRequest,
        @RequestPart(name = "images") List<MultipartFile> images) throws URISyntaxException {
        Long articleId = articleService.createArticle(user, articleRequest, images);
        return ResponseEntity.created(new URI("/api/article/" + articleId)).build();
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleResponse> findArticle(@CurrentUser User user,
        @PathVariable Long articleId) {
        return ResponseEntity.ok(articleService.findArticle(user, articleId));
    }

    @GetMapping
    public ResponseEntity<List<ArticlesResponse>> findArticles(ArticlesRequest articlesRequest,
        @Valid Pagination pagination) {
        return ResponseEntity.ok(articleService.findArticles(articlesRequest, pagination));
    }

    @PatchMapping("/{articleId}")
    public ResponseEntity<Void> updateArticle(@CurrentUser User user,
        @PathVariable Long articleId,
        @Valid @RequestPart("articleUpdateRequest") ArticleUpdateRequest request,
        @RequestPart(name = "images", required = false) List<MultipartFile> images) {
        articleService.updateArticle(user, articleId, request, images);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/articleStatus/{articleId}")
    public ResponseEntity<Void> updateArticleStatus(@CurrentUser User user, @PathVariable Long articleId,
        @Valid @RequestBody ArticleStatusUpdateRequest request) {
        articleService.updateArticleStatus(user, articleId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> deleteArticle(@CurrentUser User user, @PathVariable Long articleId) {
        articleService.deleteArticle(user, articleId);
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
