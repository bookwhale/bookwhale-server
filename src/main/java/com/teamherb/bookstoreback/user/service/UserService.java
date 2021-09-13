package com.teamherb.bookstoreback.user.service;

import com.teamherb.bookstoreback.Interest.domain.Interest;
import com.teamherb.bookstoreback.Interest.domain.InterestRepository;
import com.teamherb.bookstoreback.Interest.dto.InterestRequest;
import com.teamherb.bookstoreback.Interest.dto.InterestResponse;
import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.common.utils.upload.FileStoreUtil;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.post.domain.PostRepository;
import com.teamherb.bookstoreback.user.domain.Role;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.domain.UserRepository;
import com.teamherb.bookstoreback.user.dto.PasswordUpdateRequest;
import com.teamherb.bookstoreback.user.dto.ProfileResponse;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import com.teamherb.bookstoreback.user.dto.UserUpdateRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  private final FileStoreUtil fileStoreUtil;

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
        .phoneNumber(signUpRequest.getPhoneNumber())
        .role(Role.ROLE_USER)
        .build();

    userRepository.save(user);
  }

  public void updateMyInfo(User user, UserUpdateRequest userUpdateRequest) {
    user.update(userUpdateRequest);
    userRepository.save(user);
  }

  public ProfileResponse uploadProfileImage(User user, MultipartFile profileImage) {
    deleteProfile(user);
    String uploadFile = fileStoreUtil.storeFile(profileImage);
    user.uploadProfile(uploadFile);
    userRepository.save(user);
    return ProfileResponse.of(uploadFile);
  }

  public void deleteProfileImage(User user) {
    deleteProfile(user);
    userRepository.save(user);
  }

  private void deleteProfile(User user) {
    //TODO : S3 연동하면 S3 이미지를 삭제하는 로직을 추가해야합니다.
    user.deleteProfile();
  }

  public void updatePassword(User user, PasswordUpdateRequest req) {
    validateIsCorrectPassword(req.getOldPassword(), user.getPassword());
    user.updatePassword(passwordEncoder.encode(req.getNewPassword()));
    userRepository.save(user);
  }

  private void validateIsCorrectPassword(String password, String encodedPassword) {
    if (!passwordEncoder.matches(password, encodedPassword)) {
      throw new CustomException(ErrorCode.INVALID_USER_PASSWORD);
    }
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
