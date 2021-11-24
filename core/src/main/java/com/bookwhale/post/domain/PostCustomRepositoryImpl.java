package com.bookwhale.post.domain;

import static com.bookwhale.post.domain.QPost.post;
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
public class PostCustomRepositoryImpl implements PostCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Post> findPostWithSellerById(Long id) {
        Post result = queryFactory.selectFrom(post)
            .leftJoin(post.seller, user).fetchJoin()
            .where(post.id.eq(id))
            .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public Page<Post> findAllOrderByCreatedDateDesc(String title, String author, String publisher,
        String sellingLocation, String postStatus, Pageable pageable) {
        QueryResults<Post> results = queryFactory
            .selectFrom(post)
            .where(
                titleLike(title),
                authorLike(author),
                publisherLike(publisher),
                sellingLocationEq(sellingLocation),
                postStatusEq(postStatus)
            )
            .orderBy(post.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();
        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public static BooleanExpression titleLike(String title) {
        return Optional.ofNullable(title)
            .map(keyword -> post.book.bookTitle.like("%" + keyword + "%"))
            .orElse(null);
    }

    public static BooleanExpression authorLike(String author) {
        return Optional.ofNullable(author)
            .map(keyword -> post.book.bookAuthor.like("%" + keyword + "%"))
            .orElse(null);
    }

    public static BooleanExpression publisherLike(String publisher) {
        return Optional.ofNullable(publisher)
            .map(keyword -> post.book.bookPublisher.like("%" + keyword + "%"))
            .orElse(null);
    }

    public static BooleanExpression sellingLocationEq(String sellingLocation) {
        return Optional.ofNullable(sellingLocation)
            .map(keyword -> post.sellingLocation.eq(Location.valueOf(keyword))).orElse(null);
    }

    public static BooleanExpression postStatusEq(String postStatus) {
        return Optional.ofNullable(postStatus)
            .map(keyword -> post.postStatus.eq(PostStatus.valueOf(keyword))).orElse(null);
    }


}