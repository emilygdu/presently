package com.presently.friendship;

import com.presently.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByRecieverAndStatus(User reciever, FriendshipStatus status);
    List<Friendship> findByRequesterAndStatus(User requester, FriendshipStatus status);
    Optional<Friendship> findByRecieverAndRequester(User reciever, User requester);
}