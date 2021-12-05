package com.bookwhale.favorite.domain;

import com.bookwhale.article.domain.Article;
import com.bookwhale.common.domain.BaseEntity;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.user.domain.User;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "article_favorite")
public class Favorite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    private Favorite(User user, Article article) {
        this.user = user;
        this.article = article;
    }

    public static Favorite create(User loginUser, Article article) {
        return new Favorite(loginUser, article);
    }

    public void validateIsMyFavorite(User loginUser) {
        if (!isMyFavorite(loginUser)) {
            throw new CustomException(ErrorCode.USER_ACCESS_DENIED);
        }
    }

    public boolean isMyFavorite(User loginUser) {
        return this.getUser().getId().equals(loginUser.getId());
    }
}
