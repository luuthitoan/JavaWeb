package com.example.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {
    @JsonProperty("fullname")
    private String fullName;
    @JsonProperty("user_identifier")
    private String userIdentifier;
    private String address;
    private String password;
    @JsonProperty("google_account_id")
    private int googleAccountId;
}
