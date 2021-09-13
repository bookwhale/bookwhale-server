package com.teamherb.bookstoreback.user.service;

import com.teamherb.bookstoreback.Interest.domain.Interest;
import com.teamherb.bookstoreback.Interest.domain.InterestRepository;
import com.teamherb.bookstoreback.Interest.dto.InterestRequest;
import com.teamherb.bookstoreback.Interest.dto.InterestResponse;
import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.post.domain.PostRepository;
import com.teamherb.bookstoreback.user.domain.Role;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.domain.UserRepository;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import com.teamherb.bookstoreback.user.dto.UserUpdateRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  private final PostRepository postRepository;

  private final InterestRepository interestRepository;

  public void createUser(SignUpRequest signUpRequest) {
    if (userRepository.existsByIdentity(signUpRequest.getIdentity())) {
      throw new CustomException(ErrorCode.DUPLICATED_USER_IDENTITY);
    }

    User user = User.builder()
        .identity(signUpRequest.getIdentity())
        .password(passwordEncoder.encode(signUpRequest.getPassword()))
        .email(signUpRequest.getEmail())
        .name(signUpRequest.getName())
        .role(Role.ROLE_USER)
        .build();

    userRepository.save(user);
  }

  public void updateMyInfo(User user, UserUpdateRequest userUpdateRequest) {
    user.update(userUpdateRequest);
    userRepository.save(user);
  }

  @Transactional(readOnly = true)
  public List<InterestResponse> findInterests(User user) {
    List<Interest> interests = interestRepository.findAllByUserOrderByCreatedDate(user);
    return InterestResponse.listOf(interests);

  }

  public void addInterest(User loginUser, InterestRequest request) {
    Post post = postRepository.findById(request.getPostId())
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_POST_ID));
    Interest.create(loginUser, post);
  }

  public void deleteInterest(User loginUser, Long interestId) {
    Interest interest = interestRepository.findById(interestId)
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INTEREST_ID));

    if (!interest.getUser().getId().equals(loginUser.getId())) {
      throw new CustomException(ErrorCode.USER_ACCESS_DENIED);
    }

    interestRepository.delete(interest);
  }
}
