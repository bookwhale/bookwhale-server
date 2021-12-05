package com.bookwhale.like.domain;

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
@Table(name = "article_like")
public class Like extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    private Like(User user, Article article) {
        this.user = user;
        this.article = article;
    }

    public static Like create(User loginUser, Article article) {
        return new Like(loginUser, article);
    }

    public void validateIsMyLike(User loginUser) {
        if (!isMyLike(loginUser)) {
            throw new CustomException(ErrorCode.USER_ACCESS_DENIED);
        }
    }

    public boolean isMyLike(User loginUser) {
        return this.getUser().getId().equals(loginUser.getId());
    }
}
