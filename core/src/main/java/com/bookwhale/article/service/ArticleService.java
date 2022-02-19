package com.bookwhale.article.service;

import com.bookwhale.article.domain.Article;
import com.bookwhale.article.domain.ArticleRepository;
import com.bookwhale.article.dto.ArticleRequest;
import com.bookwhale.article.dto.ArticleResponse;
import com.bookwhale.article.dto.ArticleStatusUpdateRequest;
import com.bookwhale.article.dto.ArticleUpdateRequest;
import com.bookwhale.article.dto.ArticlesRequest;
import com.bookwhale.article.dto.ArticlesResponse;
import com.bookwhale.common.dto.Pagination;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.common.upload.FileUploader;
import com.bookwhale.favorite.domain.FavoriteRepository;
import com.bookwhale.image.domain.Images;
import com.bookwhale.user.domain.User;
import com.bookwhale.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final FileUploader fileUploader;
    private final FavoriteRepository favoriteRepository;
    private final UserService userService;

    public Long createArticle(User user, ArticleRequest request, List<MultipartFile> images) {
        User targetUser = userService.findUserByEmail(user.getEmail());
        Article article = Article.create(targetUser, request.toEntity());
        saveAllImages(article, images);
        return articleRepository.save(article).getId();
    }

    @Transactional(readOnly = true)
    public List<ArticlesResponse> findMyArticles(User user) {
        User targetUser = userService.findUserByEmail(user.getEmail());
        return ArticlesResponse.listOf(articleRepository.findAllBySeller(targetUser));
    }

    public ArticleResponse findArticle(User user, Long articleId) {
        User targetUser = userService.findUserByEmail(user.getEmail());
        Article article = validateArticleIdAndGetArticleWithSeller(articleId);
        article.increaseOneViewCount(targetUser);
        return ArticleResponse.of(
            article,
            article.isMyArticle(targetUser),
            favoriteRepository.existsByUserAndArticle(targetUser, article)
        );
    }

    public Article validateArticleIdAndGetArticleWithSeller(Long articleId) {
        return articleRepository.findArticleWithSellerById(articleId)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_ARTICLE_ID));
    }

    @Transactional(readOnly = true)
    public List<ArticlesResponse> findArticles(ArticlesRequest request, Pagination pagination) {
        PageRequest pageable = PageRequest.of(pagination.getPage(), pagination.getSize());
        List<Article> articles = articleRepository.findAllBySearch(request.getSearch(), pageable);
        return ArticlesResponse.listOf(articles);
    }

    public void updateArticle(User user, Long articleId, ArticleUpdateRequest request,
        List<MultipartFile> images) {
        User targetUser = userService.findUserByEmail(user.getEmail());
        Article article = getArticleByArticleId(articleId);
        article.validateIsMyArticle(targetUser);
        article.update(request.toEntity());
        updateImages(article, images, request.getDeleteImgUrls());
    }

    public void updateImages(Article article, List<MultipartFile> images,
        List<String> deleteImgUrls) {
        if (deleteImgUrls != null && !deleteImgUrls.isEmpty()) {
            fileUploader.deleteFiles(deleteImgUrls);
            article.getImages().deleteImageUrls(deleteImgUrls);
        }
        saveAllImages(article, images);
    }

    public void saveAllImages(Article article, List<MultipartFile> images) {
        if (images != null && !images.isEmpty()) {
            List<String> uploadImageUrls = fileUploader.uploadFiles(images);
            article.getImages().addAll(article, uploadImageUrls);
        }
    }

    public void updateArticleStatus(User user, Long articleId, ArticleStatusUpdateRequest request) {
        User targetUser = userService.findUserByEmail(user.getEmail());
        Article article = getArticleByArticleId(articleId);
        article.validateIsMyArticle(targetUser);
        article.updateArticleStatus(request.getArticleStatus());
    }

    public Article getArticleByArticleId(Long articleId) {
        return articleRepository.findById(articleId)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_ARTICLE_ID));
    }

    public void deleteArticle(User user, Long articleId) {
        User targetUser = userService.findUserByEmail(user.getEmail());
        Article article = getArticleByArticleId(articleId);
        article.validateIsMyArticle(targetUser);
        deleteAllImages(article);
        articleRepository.delete(article);
    }

    public void deleteAllImages(Article article) {
        Images images = article.getImages();
        if (!images.isEmpty()) {
            fileUploader.deleteFiles(images.getImageUrls());
            images.deleteAll();
        }
    }
}
