package com.presently.friendship;

import com.presently.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class FriendshipService {

    public final FriendshipRepository friendshipRepository;

    public Friendship sendFriendRequest(User requester, User receiver) {
        Optional<Friendship> existing = friendshipRepository
                .findByRecieverAndRequester(receiver, requester);
        
        if (existing.isPresent()) {
            throw new RuntimeException("Friend request already exists");
        }

        Friendship friendship = new Friendship();
        friendship.setRequester(requester);
        friendship.setReciever(receiver);
        friendship.setStatus(FriendshipStatus.PENDING);

        return friendshipRepository.save(friendship);
    }

    public Friendship acceptFriendRequest(Long friendshipId, User reciever) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));
        
        if (!friendship.getReciever().getId().equals(reciever.getId())) {
            throw new RuntimeException("No authorization to accept this request");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        return friendshipRepository.save(friendship);
    }

    public List<Friendship> getFriends(User user) {
        List<Friendship> asRequester = friendshipRepository
                .findByRequesterAndStatus(user, FriendshipStatus.ACCEPTED);
        List<Friendship> asReciever = friendshipRepository
                .findByRecieverAndStatus(user, FriendshipStatus.ACCEPTED);

        asRequester.addAll(asReciever);
        return asRequester;
    }

    public void deleteFriendship(Long friendshipId) {
        friendshipRepository.deleteById(friendshipId);
    }    
}
