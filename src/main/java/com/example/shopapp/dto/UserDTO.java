package com.example.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class UserDTO {
    @JsonProperty("fullname")
    private String fullName;
    @JsonProperty("phone_number")
    @NotBlank(message = "User identifier is required")
    private String userIdentifier;
    @NotBlank(message = "Address is required")
    private String address;
    @NotBlank(message = "Password is required")
    private String password;
    @JsonProperty("retype_password")
    private String retypePassword;
    @JsonProperty("google_account_id")
    private int googleAccountId;
    @JsonProperty("role_id")
    @NotNull(message = "Role ID is required")
    private Long roleId;
}
