package com.bookwhale.article.domain;

import static com.bookwhale.article.domain.QArticle.article;
import static com.bookwhale.user.domain.QUser.user;

import com.bookwhale.common.domain.Location;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    public Page<Article> findAllOrderByCreatedDateDesc(String title, String author, String publisher,
        String sellingLocation, String articleStatus, Pageable pageable) {
        QueryResults<Article> results = queryFactory
            .selectFrom(article)
            .where(
                titleLike(title),
                authorLike(author),
                publisherLike(publisher),
                sellingLocationEq(sellingLocation),
                articleStatusEq(articleStatus)
            )
            .orderBy(article.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();
        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public static BooleanExpression titleLike(String title) {
        return title != null ? article.book.bookTitle.like("%" + title + "%") : null;
    }

    public static BooleanExpression authorLike(String author) {
        return author != null ? article.book.bookAuthor.like("%" + author + "%") : null;
    }

    public static BooleanExpression publisherLike(String publisher) {
        return publisher != null ? article.book.bookPublisher.like("%" + publisher + "%") : null;
    }

    public static BooleanExpression sellingLocationEq(String sellingLocation) {
        return Optional.ofNullable(sellingLocation)
            .map(keyword -> article.sellingLocation.eq(Location.valueOf(keyword))).orElse(null);
    }

    public static BooleanExpression articleStatusEq(String articleStatus) {
        return Optional.ofNullable(articleStatus)
            .map(keyword -> article.articleStatus.eq(ArticleStatus.valueOf(keyword))).orElse(null);
    }
}