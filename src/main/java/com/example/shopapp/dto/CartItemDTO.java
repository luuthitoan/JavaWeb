package com.example.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("quantity")
    private Integer quantity;
}
