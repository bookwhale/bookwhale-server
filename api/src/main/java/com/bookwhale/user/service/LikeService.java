package com.bookwhale.user.service;

import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.like.domain.Like;
import com.bookwhale.like.domain.likeRepository;
import com.bookwhale.post.domain.Post;
import com.bookwhale.user.domain.User;
import com.bookwhale.user.dto.LikeRequest;
import com.bookwhale.user.dto.LikeResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 접속 중인 사용자(user)가 게시글(post)에 좋아요를 남긴 요청을 처리
 */
@Service
public class LikeService {

    private final likeRepository likeRepository;
    private final UserService userService;


    public LikeService(likeRepository likeRepository, UserService userService) {
        this.likeRepository = likeRepository;
        this.userService = userService;
    }

    /**
     * 현재 접속중인 사용자가 대상 post에 좋아요 추가
     * @param user 현재 접속 중인 사용자
     * @param request 좋아요 처리 요청
     */
    public void addLike(User user, LikeRequest request) {
        Post post = userService.validatePostIdAndGetPost(request.getPostId());
        validateIsDuplicatedLike(user, post);
        likeRepository.save(Like.create(user, post));
    }

    public void validateIsDuplicatedLike(User user, Post post) {
        if (likeRepository.existsByUserAndPost(user, post)) {
            throw new CustomException(ErrorCode.DUPLICATED_LIKE);
        }
    }

    /**
     * 사용자가 선택한 좋아요를 취소 처리
     * @param user 현재 접속 중인 사용자
     * @param likeId 좋아요 id
     */
    public void deleteLike(User user, Long likeId) {
        Like like = getLikeById(likeId);
        like.validateIsMyLike(user);
        likeRepository.delete(like);
    }

    private Like getLikeById(Long likeId) {
        return likeRepository.findById(likeId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_LIKE_ID));
    }

    /**
     * 사용자의 좋아요 목록을 전체 조회
     * @param user 현재 접속 중인 사용자
     * @return 좋아요 id에 해당하는 Post 정보로 구성된 LikeResponse 를 반환 (likeId, PostsResponse)
     */
    @Transactional(readOnly = true)
    public List<LikeResponse> findAllLikes(User user) {
        return LikeResponse.listOf(likeRepository.findAllByUser(user));
    }
}
