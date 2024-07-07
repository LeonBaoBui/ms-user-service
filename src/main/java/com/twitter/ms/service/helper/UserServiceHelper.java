package com.twitter.ms.service.helper;

import com.twitter.ms.model.User;
import com.twitter.ms.repository.BlockUserRepository;
import com.twitter.ms.repository.FollowerUserRepository;
import com.twitter.ms.repository.MuteUserRepository;
import com.twitter.ms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import main.java.com.leon.baobui.exception.ApiRequestException;
import main.java.com.leon.baobui.util.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static main.java.com.leon.baobui.constants.ErrorMessage.USER_PROFILE_BLOCKED;

@Component
@RequiredArgsConstructor
public class UserServiceHelper {
    private final FollowerUserRepository followerUserRepository;
    private final MuteUserRepository muteUserRepository;
    private final UserRepository userRepository;
    private final BlockUserRepository blockUserRepository;

    public boolean isUserFollowByOtherUser(Long userId) {
        Long authUserId = AuthUtil.getAuthenticatedUserId();
        return followerUserRepository.isUserFollowByOtherUser(authUserId, userId);
    }

    public boolean isUserHavePrivateProfile(Long userId) {
        Long authUserId = AuthUtil.getAuthenticatedUserId();
        return !userRepository.isUserHavePrivateProfile(userId, authUserId);
    }

    public boolean isUserBlockedByMyProfile(Long userId) {
        Long authUserId = AuthUtil.getAuthenticatedUserId();
        return blockUserRepository.isUserBlocked(authUserId, userId);
    }

    public boolean isUserMutedByMyProfile(Long userId) {
        Long authUserId = AuthUtil.getAuthenticatedUserId();
        return muteUserRepository.isUserMuted(authUserId, userId);
    }

    public boolean isMyProfileBlockedByUser(Long userId) {
        Long authUserId = AuthUtil.getAuthenticatedUserId();
        return blockUserRepository.isUserBlocked(userId, authUserId);
    }

    public boolean isMyProfileWaitingForApprove(Long userId) {
        Long authUserId = AuthUtil.getAuthenticatedUserId();
        return userRepository.isMyProfileWaitingForApprove(userId, authUserId);
    }

    public void checkIsUserBlocked(User user, User authUser) {
        if (blockUserRepository.isUserBlocked(user.getId(), authUser.getId())) {
            throw new ApiRequestException(USER_PROFILE_BLOCKED, HttpStatus.BAD_REQUEST);
        }
    }

}
