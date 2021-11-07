package com.bookwhale.user.service;

import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.like.domain.Like;
import com.bookwhale.like.domain.likeRepository;
import com.bookwhale.post.domain.Post;
import com.bookwhale.user.domain.User;
import com.bookwhale.user.dto.LikeRequest;
import com.bookwhale.user.dto.LikeResponse;
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
public class LikeService {

  private final likeRepository likeRepository;
  private final UserService userService;

  /**
   * 현재 접속중인 사용자가 대상 판매글(post)에 관심 추가
   *
   * @param user    현재 접속 중인 사용자
   * @param request 관심목록 추가 처리 요청
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
   * 사용자의 관심목록 중 선택한 관심을 취소 처리
   *
   * @param user   현재 접속 중인 사용자
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
   * 사용자의 관심목록을 전체 조회
   *
   * @param user 현재 접속 중인 사용자
   * @return 좋아요 id에 해당하는 Post 정보로 구성된 LikeResponse 를 반환 (likeId, PostsResponse)
   */
  @Transactional(readOnly = true)
  public List<LikeResponse> findAllLikes(User user) {
    return LikeResponse.listOf(likeRepository.findAllByUser(user));
  }
}
