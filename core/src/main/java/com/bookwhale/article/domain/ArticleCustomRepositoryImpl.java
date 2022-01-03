package com.bookwhale.article.domain;

import static com.bookwhale.article.domain.QArticle.article;
import static com.bookwhale.user.domain.QUser.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class ArticleCustomRepositoryImpl implements ArticleCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Article> findArticleWithSellerById(Long id) {
        Article result = queryFactory.selectFrom(article)
            .leftJoin(article.seller, user).fetchJoin()
            .where(article.id.eq(id))
            .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public List<Article> findAllBySearch(String search, Pageable page) {
        if (StringUtils.isBlank(search)) {
            return queryFactory
                .selectFrom(article)
                .orderBy(article.createdDate.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();
        }
        return queryFactory
            .selectFrom(article)
            .where(
                bookTitleLike(search).or(articleTitleLike(search)).or(authorLike(search)),
                article.articleStatus.eq(ArticleStatus.SALE)
            )
            .orderBy(article.createdDate.desc())
            .offset(page.getOffset())
            .limit(page.getPageSize())
            .fetch();
    }

    public static BooleanExpression bookTitleLike(String bookTitle) {
        return article.book.bookTitle.like("%" + bookTitle + "%");
    }

    public static BooleanExpression articleTitleLike(String articleTitle) {
        return article.title.like("%" + articleTitle + "%");
    }

    public static BooleanExpression authorLike(String author) {
        return article.book.bookAuthor.like("%" + author + "%");
    }
}
