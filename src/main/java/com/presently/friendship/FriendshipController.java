package com.presently.friendship;

import com.presently.user.User;
import com.presently.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor

public class FriendshipController {

    private final FriendshipService friendshipService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<Friendship>> getFriends() {
        String username = SecurityContextHolder.getContext()
            .getAuthentication().getName();
        
        return userService.findByUsername(username).map(user ->
            ResponseEntity.ok(friendshipService.getFriends(user)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/request/{id}")
    public ResponseEntity<Friendship> sendRequest(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext()
            .getAuthentication().getName();


        return userService.findByUsername(username).map(requester ->
            userService.findById(id).map(receiver ->
                    ResponseEntity.ok(friendshipService.sendFriendRequest(requester, receiver)))
                .orElse(ResponseEntity.notFound().build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/accept/{id}")
    public ResponseEntity<Friendship> acceptRequest(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        return userService.findByUsername(username)
                .map(user -> ResponseEntity.ok(
                        friendshipService.acceptFriendRequest(id, user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFriendship(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext()
            .getAuthentication().getName();

        var user = userService.findByUsername(username);
        if (user.isEmpty()) return ResponseEntity.notFound().build();

        var friendship = friendshipService.findById(id);
        if (friendship.isEmpty()) return ResponseEntity.notFound().build();

        Friendship f = friendship.get();
        if (!f.getRequester().getId().equals(user.get().getId()) &&
            !f.getReceiver().getId().equals(user.get().getId())) {
            return ResponseEntity.status(403).build();
        }

        friendshipService.deleteFriendship(id);
        return ResponseEntity.noContent().build();
    }
}
