package com.example.shopapp.controller;

import com.example.shopapp.component.LocalizationUtil;
import com.example.shopapp.dto.UpdateUserDTO;
import com.example.shopapp.model.User;
import com.example.shopapp.responses.UserResponse;
import com.example.shopapp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final LocalizationUtil localizationUtil;

    @PostMapping("/details")
    public ResponseEntity<?> getUserDetails(@RequestHeader("Authorization") String token)
    {
        try {
            String extractedToken = token.substring(7);
            User user = userService.getUserDetailFromToken(extractedToken);
            return  ResponseEntity.ok().body(UserResponse.fromUser(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/details/{userId}")
    public ResponseEntity<UserResponse> updateUserDetails(
            @PathVariable Long userId,
            @RequestBody UpdateUserDTO updatedUserDTO,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {

            String extractedToken = authorizationHeader.substring(7);
            User user = userService.getUserDetailFromToken(extractedToken);
            if (user.getId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            User updatedUser = userService.updateUser(userId, updatedUserDTO);
            return ResponseEntity.ok(UserResponse.fromUser(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
