package com.twitter.ms.repository;

import java.util.List;

import com.twitter.ms.model.User;
import com.twitter.ms.repository.projection.SameFollower;
import com.twitter.ms.repository.projection.UserProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowerUserRepository extends JpaRepository<User, Long> {
    @Query("SELECT CASE WHEN count(user) > 0 THEN true ELSE false END " +
            "FROM User user " +
            "LEFT JOIN user.followers follower " +
            "WHERE user.id = :authUserId AND follower.id = :userId")
    boolean isUserFollowByOtherUser(@Param("authUserId") Long authUserId, @Param("userId") Long userId);

    @Query("SELECT user " +
            "FROM User user " +
            "LEFT JOIN user.followers followers " +
            "WHERE followers.id = :userId")
    Page<UserProjection> getFollowingById(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT user " +
            "FROM User user " +
            "LEFT JOIN user.following following " +
            "WHERE following.id = :userId")
    Page<UserProjection> getFollowersById(@Param("userId") Long userId, Pageable pageable);
    @Query("SELECT CASE WHEN count(user) > 0 THEN true ELSE false END " +
            "FROM User user " +
            "INNER JOIN user.followers follower " +
            "WHERE follower.id = :userId AND user.id = :authUserId ")
    boolean isFollower(@Param("authUserId") Long authUserId, @Param("userId") Long userId);

    @Modifying
    @Query(value = "INSERT INTO user_follower_requests (follower_id, user_id) " +
            "SELECT * FROM (SELECT :authUserId, :userId) AS tmp " +
            "WHERE NOT EXISTS ( " +
            "SELECT follower_id FROM user_follower_requests WHERE follower_id = :authUserId " +
            "AND user_id = :userId " +
            ") LIMIT 1", nativeQuery = true)
    void addFollowerRequest(@Param("authUserId") Long authUserId, @Param("userId") Long userId);

    @Query(value = "SELECT users.id as id, users.full_name as fullName, users.username as username, users.about as about,\n" +
                   "        users.private_profile as privateProfile, users.avatar as avatar\n" +
                   "FROM users\n" +
                   "WHERE users.id IN (\n" +
                   "    SELECT user_subscriptions.subscriber_id\n" +
                   "    FROM user_subscriptions\n" +
                   "    WHERE user_subscriptions.user_id = :userId\n" +
                   ")\n" +
                   "INTERSECT\n" +
                   "SELECT users.id as id, users.full_name as fullName, users.username as username, users.about as about,\n" +
                   "        users.private_profile as privateProfile, users.avatar as avatar\n" +
                   "FROM users\n" +
                   "WHERE users.id IN (\n" +
                   "    SELECT user_subscriptions.subscriber_id\n" +
                   "    FROM user_subscriptions\n" +
                   "    WHERE user_subscriptions.user_id = :authUserId\n" +
                   ")",
            nativeQuery = true)
    <T> List<T> getSameFollowers(@Param("userId") Long userId, @Param("authUserId") Long authUserId, Class<T> responseType);
}
