package com.example.shopapp.responses;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActionResponse {
    @JsonProperty("message")
    private String message;
}