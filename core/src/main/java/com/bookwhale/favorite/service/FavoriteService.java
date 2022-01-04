package com.bookwhale.favorite.service;

import com.bookwhale.article.domain.Article;
import com.bookwhale.article.domain.ArticleRepository;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.favorite.domain.Favorite;
import com.bookwhale.favorite.domain.FavoriteRepository;
import com.bookwhale.user.domain.User;
import com.bookwhale.user.dto.FavoriteRequest;
import com.bookwhale.user.dto.FavoriteResponse;
import com.bookwhale.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 접속 중인 사용자(user)의 관심목록 관련 요청을 처리
 */
@RequiredArgsConstructor
@Transactional
@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ArticleRepository articleRepository;
    private final UserService userService;

    /**
     * 현재 접속중인 사용자가 대상 판매글(article)에 관심 추가
     *
     * @param user    현재 접속 중인 사용자
     * @param request 관심목록 추가 처리 요청
     */
    @Transactional
    public void addFavorite(User user, FavoriteRequest request) {
        User targetUser = userService.findUserByEmail(user.getEmail());
        Article article = getArticleById(request.getArticleId());
        validateIsDuplicatedFavorite(targetUser, article);

        favoriteRepository.save(Favorite.create(targetUser, article));
        article.increaseOneFavoriteCount();
    }

    public Article getArticleById(Long articleId) {
        return articleRepository.findById(articleId)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_ARTICLE_ID));
    }

    public void validateIsDuplicatedFavorite(User user, Article article) {
        User targetUser = userService.findUserByEmail(user.getEmail());
        if (favoriteRepository.existsByUserAndArticle(targetUser, article)) {
            throw new CustomException(ErrorCode.DUPLICATED_FAVORITE);
        }
    }

    /**
     * 사용자의 관심목록 중 선택한 관심을 취소 처리
     *
     * @param user       현재 접속 중인 사용자
     * @param favoriteId 좋아요 id
     */
    @Transactional
    public void deleteFavorite(User user, Long favoriteId) {
        User targetUser = userService.findUserByEmail(user.getEmail());
        Favorite favorite = getFavoriteById(favoriteId);
        favorite.validateIsMyFavorite(targetUser);

        favoriteRepository.delete(favorite);
        favorite.getArticle().decreaseOneFavoriteCount();
    }

    private Favorite getFavoriteById(Long favoriteId) {
        return favoriteRepository.findById(favoriteId)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_FAVORITE_ID));
    }

    /**
     * 사용자의 관심목록을 전체 조회
     *
     * @param user 현재 접속 중인 사용자
     * @return 좋아요 id에 해당하는 Article 정보로 구성된 FavoriteResponse 를 반환 (favoriteId, ArticlesResponse)
     */
    @Transactional(readOnly = true)
    public List<FavoriteResponse> findAllFavorites(User user) {
        User targetUser = userService.findUserByEmail(user.getEmail());
        return FavoriteResponse.listOf(favoriteRepository.findAllByUser(targetUser));
    }
}
