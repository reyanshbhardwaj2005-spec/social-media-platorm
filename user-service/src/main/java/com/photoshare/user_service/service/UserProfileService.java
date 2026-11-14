package com.photoshare.user_service.service;

import com.photoshare.user_service.client.MediaServiceClient;
import com.photoshare.user_service.dto.request.ProfileRequest;
import com.photoshare.user_service.dto.response.MediaUploadResponse;
import com.photoshare.user_service.dto.response.ProfileResponse;
import com.photoshare.user_service.dto.response.ProfileSummaryResponse;
import com.photoshare.user_service.entity.UserProfile;
import com.photoshare.user_service.repository.UserProfileRepository;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class UserProfileService {

    private final UserProfileRepository profileRepository;
    private final MediaServiceClient mediaServiceClient;

    public UserProfileService(UserProfileRepository profileRepository, MediaServiceClient mediaServiceClient) {
        this.profileRepository = profileRepository;
        this.mediaServiceClient = mediaServiceClient;
    }

    public ProfileResponse createProfile(UUID userId, String username, ProfileRequest request,
                                         MultipartFile profileImage, String accessToken) throws IOException {

        if (profileRepository.existsByUserId(userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Profile already exists for this user.");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }

        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setUsername(username.trim());
        applyRequest(profile, request, true);

        if (profileImage != null && !profileImage.isEmpty()) {
            attachProfilePicture(profile, profileImage, accessToken);
        }

        UserProfile savedProfile = profileRepository.save(profile);
        return toProfileResponse(savedProfile, userId);
    }


    public @Nullable ProfileResponse getMyProfile(UUID uuid) {
        UserProfile profile = profileRepository.findByUserId(uuid);
        return toProfileResponse(profile, uuid);
    }

    public ProfileResponse updateMyProfile(UUID userId, ProfileRequest request,
                                           MultipartFile profileImage, String accessToken) throws IOException {
        UserProfile profile = getRequiredProfile(userId);
        applyRequest(profile, request, false);

        // If no new image is supplied, retain the existing profile picture.
        if (profileImage != null && !profileImage.isEmpty()) {
            attachProfilePicture(profile, profileImage, accessToken);
        }

        return toProfileResponse(profileRepository.save(profile), userId);
    }

    public @Nullable ProfileResponse getProfile(UUID userId, UUID uuid) {
        UserProfile profile = profileRepository.findByUserId(userId);
        return toProfileResponse(profile, uuid);
    }

    public List<ProfileSummaryResponse> searchProfiles(String query, UUID currentUserId) {

        String normalizedQuery = query == null ? "" : query.trim();

        if (normalizedQuery.isBlank()) {
            throw new IllegalArgumentException("Search query must not be empty.");
        }

        return profileRepository.searchProfiles(normalizedQuery, currentUserId, PageRequest.of(0, 20))
                .getContent()
                .stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    public @Nullable ProfileResponse updatePrivacy(UUID uuid, @NotNull(message = "privateAccount is required") Boolean privateAccount) {
        UserProfile profile = new UserProfile();
        profile.setPrivateAccount(privateAccount);
        return toProfileResponse(profile, uuid);
    }

    private void attachProfilePicture(UserProfile profile, MultipartFile image,
                                      String accessToken) throws IOException {
        MediaUploadResponse uploaded = mediaServiceClient.uploadProfilePicture(
                image.getBytes(), image.getOriginalFilename(), image.getContentType(), accessToken
        );
        profile.setProfilePictureMediaId(uploaded.getMediaId());
        profile.setProfilePictureUrl(uploaded.getImageUrl());
    }

    private UserProfile getRequiredProfile(UUID userId) {
        UserProfile profile = profileRepository.findByUserId(userId);
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found.");
        }
        return profile;
    }

    private void applyRequest(UserProfile profile, ProfileRequest request, boolean creating) {

        profile.setFullName(request.getFullName().trim());
        profile.setBio(blankToNull(request.getBio()));
//        profile.setProfilePictureUrl(blankToNull(request.getProfilePictureUrl()));
        profile.setWebsite(blankToNull(request.getWebsite()));

        if (request.getPrivateAccount() != null) {
            profile.setPrivateAccount(request.getPrivateAccount());
        } else if (creating) {
            profile.setPrivateAccount(false);
        }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private ProfileResponse toProfileResponse(UserProfile profile, UUID viewerUserId) {

        boolean isOwner = profile.getUserId().equals(viewerUserId);
        boolean restricted = profile.isPrivateAccount() && !isOwner;

        ProfileResponse response = new ProfileResponse();

        response.setProfileId(profile.getId());
        response.setUserId(profile.getUserId());
        response.setUsername(profile.getUsername());
        response.setFullName(profile.getFullName());
        response.setProfilePictureUrl(profile.getProfilePictureUrl());
        response.setProfilePictureMediaId(profile.getProfilePictureMediaId());
        response.setPrivateAccount(profile.isPrivateAccount());
        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());

        // Private profiles expose only basic information to non-owners
        // until follow relationships are implemented.
        response.setBio(restricted ? null : profile.getBio());
        response.setWebsite(restricted ? null : profile.getWebsite());

        return response;
    }


    private ProfileSummaryResponse toSummaryResponse(UserProfile profile) {

        ProfileSummaryResponse response = new ProfileSummaryResponse();

        response.setUserId(profile.getUserId());
        response.setUsername(profile.getUsername());
        response.setFullName(profile.getFullName());
        response.setProfilePictureUrl(profile.getProfilePictureUrl());
        response.setPrivateAccount(profile.isPrivateAccount());

        return response;
    }
}
