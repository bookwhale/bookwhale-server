package com.teamherb.bookstoreback.post.domain;

import static com.teamherb.bookstoreback.post.domain.QPost.post;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamherb.bookstoreback.post.dto.FullPostRequest;
import com.teamherb.bookstoreback.post.dto.FullPostResponse;
import com.teamherb.bookstoreback.post.dto.QFullPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<FullPostResponse> findAllByFullPostReqOrderByCreatedDateDesc(
        FullPostRequest req, Pageable pageable) {
        QueryResults<FullPostResponse> results = queryFactory
            .select(new QFullPostResponse(
                post.id,
                post.book.bookThumbnail,
                post.title,
                post.price,
                post.book.bookTitle,
                post.postStatus,
                post.createdDate
            ))
            .from(post)
            .where(
                titleLike(req.getTitle()),
                authorLike(req.getAuthor()),
                publisherLike(req.getPublisher())
            )
            .orderBy(post.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();
        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public static BooleanExpression titleLike(String title) {
        return title != null ? post.book.bookTitle.like("%" + title + "%") : null;
    }

    public static BooleanExpression authorLike(String author) {
        return author != null ? post.book.bookAuthor.like("%" + author + "%") : null;
    }

    public static BooleanExpression publisherLike(String publisher) {
        return publisher != null ? post.book.bookPublisher.like("%" + publisher + "%") : null;
    }
}
