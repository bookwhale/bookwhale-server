package com.bookwhale.user.service;

import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.common.upload.FileUploader;
import com.bookwhale.user.domain.User;
import com.bookwhale.user.domain.UserRepository;
import com.bookwhale.user.dto.PasswordUpdateRequest;
import com.bookwhale.user.dto.ProfileResponse;
import com.bookwhale.user.dto.SignUpRequest;
import com.bookwhale.user.dto.UserUpdateRequest;
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

    public void createUser(SignUpRequest request) {
        validateIsDuplicateIdentity(request);
//        User user = User.create(request.toEntity(), passwordEncoder.encode(request.getPassword()));
//        userRepository.save(user);
    }

    private void validateIsDuplicateIdentity(SignUpRequest request) {
        if (userRepository.existsByIdentity(request.getIdentity())) {
            throw new CustomException(ErrorCode.DUPLICATED_USER_IDENTITY);
        }
    }

    public void updateMyInfo(User user, UserUpdateRequest request) {
        user.update(request.toEntity());
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
}
