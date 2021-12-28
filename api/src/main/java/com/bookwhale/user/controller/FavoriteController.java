package com.bookwhale.user.controller;

import com.bookwhale.auth.domain.CurrentUser;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.user.domain.User;
import com.bookwhale.user.dto.FavoriteRequest;
import com.bookwhale.user.dto.FavoriteResponse;
import com.bookwhale.user.service.FavoriteService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 현재 접속중인 user가 보낸 관심목록 관련 요청을 처리
 */
@RestController
@RequestMapping("/api/user/me")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * 사용자가 접근한 판매글에서 관심목록 추가를 요청
     *
     * @param user    현재 접속 중인 사용자
     * @param request 관심목록 추가 처리 요청
     * @return 요청 처리 성공 시 HttpStatus.OK (200)을 반환, 실패 시 Exception이 던져지며 ErrorCode를 반환
     * @see ErrorCode
     */
    @PostMapping("/favorite")
    public ResponseEntity<Void> addFavorite(@CurrentUser User user,
        @Valid @RequestBody FavoriteRequest request) {
        favoriteService.addFavorite(user, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 사용자가 관심목록 추가한 전체 목록을 조회
     *
     * @param user 현재 접속 중인 사용자
     * @return 요청 처리 성공 시 사용자의 전체 관심목록을 반환한다.
     */
    @GetMapping("/favorites")
    public ResponseEntity<List<FavoriteResponse>> findMyFavorites(@CurrentUser User user) {
        return ResponseEntity.ok(favoriteService.findAllFavorites(user));
    }

    /**
     * 사용자의 관심목록 중 선택한 관심을 삭제한다.
     *
     * @param user       현재 접속 중인 사용자
     * @param favoriteId 관심 고유값 (ID)
     * @return 요청 처리 성공 시 HttpStatus.OK (200)을 반환, 실패 시 Exception이 던져지며 ErrorCode를 반환
     * @see ErrorCode
     */
    @DeleteMapping("favorite/{favoriteId}")
    public ResponseEntity<Void> deleteFavorite(@CurrentUser User user,
        @PathVariable Long favoriteId) {
        favoriteService.deleteFavorite(user, favoriteId);
        return ResponseEntity.ok().build();
    }
}
