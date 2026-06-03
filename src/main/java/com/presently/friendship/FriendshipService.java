package com.presently.friendship;

import com.presently.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class FriendshipService {

    private final FriendshipRepository friendshipRepository;

    public Friendship sendFriendRequest(User requester, User receiver) {
        Optional<Friendship> existing = friendshipRepository
                .findByReceiverAndRequester(receiver, requester);
        Optional<Friendship> reverse = friendshipRepository
                .findByReceiverAndRequester(requester, receiver);
        
        if (existing.isPresent() || reverse.isPresent()) {
            throw new RuntimeException("Friend request already exists");
        }

        Friendship friendship = new Friendship();
        friendship.setRequester(requester);
        friendship.setReceiver(receiver);
        friendship.setStatus(FriendshipStatus.PENDING);

        return friendshipRepository.save(friendship);
    }

    public Friendship acceptFriendRequest(Long friendshipId, User receiver) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));
        
        if (!friendship.getReceiver().getId().equals(receiver.getId())) {
            throw new RuntimeException("No authorization to accept this request");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        return friendshipRepository.save(friendship);
    }

    public List<Friendship> getFriends(User user) {
        List<Friendship> asRequester = friendshipRepository
                .findByRequesterAndStatus(user, FriendshipStatus.ACCEPTED);
        List<Friendship> asReceiver = friendshipRepository
                .findByReceiverAndStatus(user, FriendshipStatus.ACCEPTED);

        List<Friendship> allFriends = new ArrayList<>(asRequester);
        allFriends.addAll(asReceiver);
        return allFriends;
    }

    public void deleteFriendship(Long friendshipId) {
        friendshipRepository.deleteById(friendshipId);
    }
    
    public Optional<Friendship> findById(Long id) {
        return friendshipRepository.findById(id);
    }
}
