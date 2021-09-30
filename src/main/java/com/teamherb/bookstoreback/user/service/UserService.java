package com.teamherb.bookstoreback.user.service;

import com.teamherb.bookstoreback.Interest.domain.Interest;
import com.teamherb.bookstoreback.Interest.domain.InterestRepository;
import com.teamherb.bookstoreback.Interest.dto.InterestRequest;
import com.teamherb.bookstoreback.Interest.dto.InterestResponse;
import com.teamherb.bookstoreback.common.Pagination;
import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.common.utils.upload.FileUploader;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.post.domain.PostRepository;
import com.teamherb.bookstoreback.post.dto.PostsResponse;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.domain.UserRepository;
import com.teamherb.bookstoreback.user.dto.PasswordUpdateRequest;
import com.teamherb.bookstoreback.user.dto.ProfileResponse;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import com.teamherb.bookstoreback.user.dto.UserUpdateRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

  private final FileUploader fileUploader;

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

  public void updateMyInfo(User user, UserUpdateRequest request) {
    user.update(request);
    userRepository.save(user);
  }

  public ProfileResponse uploadProfileImage(User user, MultipartFile image) {
    deleteImage(user);
    String imageUrl = getImageUrlAndUploadImage(user, image);
    userRepository.save(user);
    return ProfileResponse.of(imageUrl);
  }

  public String getImageUrlAndUploadImage(User user, MultipartFile image) {
    String imageUrl = fileUploader.uploadFile(image);
    user.uploadProfile(imageUrl);
    return imageUrl;
  }

  public void deleteProfileImage(User user) {
    deleteImage(user);
    userRepository.save(user);
  }

  private void deleteImage(User user) {
    String image = user.getProfileImage();
    if (image != null) {
      fileUploader.deleteFile(image);
      user.deleteProfile();
    }
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
    /*
    TODO: findAllByUser join 쿼리가 나가지 않음 ---> 해결이 필요함
          interestResponse -> PostsResponse 로 변경 필요 
    */
    List<Interest> interests = interestRepository.findAllByUser(user);
    return InterestResponse.listOf(interests);
  }

  public void addInterest(User user, InterestRequest request) {
    Post post = validatePostIdAndGetPost(request.getPostId());
    interestRepository.save(Interest.create(user, post));
  }

  public Post validatePostIdAndGetPost(Long postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_POST_ID));
  }

  public void deleteInterest(User user, Long interestId) {
    Interest interest = validateInterestIdAndGetInterest(interestId);
    interest.validateIsMyInterest(user);
    interestRepository.delete(interest);
  }

  private Interest validateInterestIdAndGetInterest(Long interestId) {
    return interestRepository.findById(interestId)
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INTEREST_ID));
  }

  @Transactional(readOnly = true)
  public List<PostsResponse> findMyPost(User user) {
    return PostsResponse.listOf(postRepository.findAllBySeller(user));
  }
}
