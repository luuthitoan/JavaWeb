package com.example.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    @JsonProperty("user_id")
    @Min(value = 1,message = "User ID must be > 0")
    private Long userID;
    @JsonProperty("fullname")
    private String fullName;
    private String email;
    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    private String address;
    private String note;
    @JsonProperty("order_date")
    private Date orderDate;
    @JsonProperty("total_money")
    @Min(value = 0,message = "Total money must be >= 0")
    private Long totalMoney;
    @JsonProperty("shipping_method")
    private String shippingMethod;
    @JsonProperty("shipping_address")
    private String shippingAddress;
    @JsonProperty("payment_method")
    private String paymentMethod;
    @JsonProperty("tracking_number")
    private String trackingNumber;
    @JsonProperty("status")
    private String status;
    @JsonProperty("shipping_date")
    private LocalDate shippingDate;
    @JsonProperty("cart_items")
    private List<CartItemDTO> cartItems;
}
