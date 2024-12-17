package com.example.shopapp.responses;

import com.example.shopapp.model.Role;
import com.example.shopapp.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("fullname")
    private String fullName;

    @JsonProperty("user_identifier")
    private String userIdentifier;

    @JsonProperty("address")
    private String address;

    @JsonProperty("is_active")
    private boolean active;

    @JsonProperty("role")
    private Role role;
    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .userIdentifier(user.getUserIdentifier())
                .address(user.getAddress())
                .role(user.getRole())
                .build();
    }
}
