package com.bookwhale.user.service;

import com.bookwhale.auth.domain.info.UserInfo;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.common.upload.FileUploader;
import com.bookwhale.user.domain.User;
import com.bookwhale.user.domain.UserRepository;
import com.bookwhale.user.dto.ProfileResponse;
import com.bookwhale.user.dto.UserResponse;
import com.bookwhale.user.dto.UserUpdateRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final FileUploader fileUploader;

    public void createUser(UserInfo userInfo) {
        User user = User.builder()
            .email(userInfo.getEmail())
            .nickname(userInfo.getName())
            .profileImage(userInfo.getPicture())
            .build();
        userRepository.save(user);
    }

    public boolean checkUserExists(UserInfo userInfo) {
        return userRepository.existsByEmail(userInfo.getEmail());
    }

    public UserResponse getUserInfo(User user){
        return UserResponse.of(findUserByEmail(user.getEmail()));
    }

    public void updateMyInfo(User user, UserUpdateRequest request) {
        User targetUser = findUserByEmail(user.getEmail());
        targetUser.updateUserName(request.toEntity().getNickname());
        userRepository.save(targetUser);
    }

    public User findUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public void withdrawalUser(User user){
        User targetUser = findUserByEmail(user.getEmail());
        userRepository.delete(targetUser);
    }

    public ProfileResponse uploadProfileImage(User user, MultipartFile image) {
        User targetUser = findUserByEmail(user.getEmail());
        deleteImage(targetUser);
        String imageUrl = getImageUrlAndUploadImage(targetUser, image);
        userRepository.save(targetUser);
        return ProfileResponse.of(imageUrl);
    }

    public String getImageUrlAndUploadImage(User user, MultipartFile image) {
        User targetUser = findUserByEmail(user.getEmail());
        String imageUrl = fileUploader.uploadFile(image);
        targetUser.uploadProfile(imageUrl);
        return imageUrl;
    }

    public void deleteProfileImage(User user) {
        User targetUser = findUserByEmail(user.getEmail());
        deleteImage(targetUser);
        userRepository.save(targetUser);
    }

    private void deleteImage(User user) {
        String image = user.getProfileImage();
        if (image != null) {
            fileUploader.deleteFile(image);
            user.deleteProfile();
        }
    }
    
    public Optional<User> findByUserId(Long userId) {
        return userRepository.findById(userId);
    }
}
