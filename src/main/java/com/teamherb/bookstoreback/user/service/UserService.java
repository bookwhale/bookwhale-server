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
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.domain.UserRepository;
import com.teamherb.bookstoreback.user.dto.PasswordUpdateRequest;
import com.teamherb.bookstoreback.user.dto.ProfileResponse;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import com.teamherb.bookstoreback.user.dto.UserUpdateRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

  public void createUser(SignUpRequest request) {
    validateIsDuplicateIdentity(request);
    User user = User.create(request, passwordEncoder.encode(request.getPassword()));
    userRepository.save(user);
  }

  private void validateIsDuplicateIdentity(SignUpRequest request) {
    if (userRepository.existsByIdentity(request.getIdentity())) {
      throw new CustomException(ErrorCode.DUPLICATED_USER_IDENTITY);
    }
  }

  public void updateMyInfo(User loginUser, UserUpdateRequest userUpdateRequest) {
    loginUser.update(userUpdateRequest);
    userRepository.save(loginUser);
  }

  public ProfileResponse uploadProfileImage(User loginUser, MultipartFile profileImage) {
    deleteProfile(loginUser);
    String uploadImage = fileStoreUtil.storeFile(profileImage);
    loginUser.uploadProfile(uploadImage);
    userRepository.save(loginUser);
    return ProfileResponse.of(uploadImage);
  }

  public void deleteProfileImage(User loginUser) {
    deleteProfile(loginUser);
    userRepository.save(loginUser);
  }

  private void deleteProfile(User user) {
    //TODO : S3 연동하면 S3 이미지를 삭제하는 로직을 추가해야합니다.
    user.deleteProfile();
  }

  public void updatePassword(User loginUser, PasswordUpdateRequest req) {
    validateIsCorrectPassword(req.getOldPassword(), loginUser.getPassword());
    loginUser.updatePassword(passwordEncoder.encode(req.getNewPassword()));
    userRepository.save(loginUser);
  }

  private void validateIsCorrectPassword(String password, String encodedPassword) {
    if (!passwordEncoder.matches(password, encodedPassword)) {
      throw new CustomException(ErrorCode.INVALID_USER_PASSWORD);
    }
  }

  @Transactional(readOnly = true)
  public List<InterestResponse> findInterests(User user) {
    // TODO: findAllByUser join 쿼리가 나가지 않음 ---> 해결이 필요함
    List<Interest> interests = interestRepository.findAllByUser(user);
    return InterestResponse.listOf(interests);
  }

  public void addInterest(User loginUser, InterestRequest request) {
    Post post = validatePostIdAndGetPost(request.getPostId());
    interestRepository.save(Interest.create(loginUser, post));
  }

  public Post validatePostIdAndGetPost(Long postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_POST_ID));
  }

  public void deleteInterest(User loginUser, Long interestId) {
    Interest interest = validateInterestIdAndGetInterest(interestId);
    interest.validateIsMyInterest(loginUser);
    interestRepository.delete(interest);
  }

  private Interest validateInterestIdAndGetInterest(Long interestId) {
    return interestRepository.findById(interestId)
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INTEREST_ID));
  }
}
