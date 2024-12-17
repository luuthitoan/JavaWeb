package com.example.shopapp.responses;

import com.example.shopapp.model.Order;
import com.example.shopapp.model.OrderDetail;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse extends BaseResponse{
    private Long id;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("fullname")
    private String fullName;
    @JsonProperty("phone_number")
    private String phoneNumber;
    private String address;
    private String email;
    private String note;
    @JsonProperty("order_date")
    private LocalDate orderDate;
    private String status;
    @JsonProperty("total_money")
    private Long totalMoney;
    @JsonProperty("shipping_method")
    private String shippingMethod;
    @JsonProperty("shipping_date")
    private LocalDate shippingDate;
    @JsonProperty("shipping_address")
    private String shippingAddress;
    @JsonProperty("payment_method")
    private String paymentMethod;
    @JsonProperty( "active")
    private Boolean active;
    @JsonProperty("order_details")
    private List<OrderDetail> orderDetails;
    public static OrderResponse fromOrder(Order order) {
        OrderResponse orderResponse =  OrderResponse
                .builder()
                .id(order.getId())
                .userId(order.getId())
                .fullName(order.getFullName())
                .phoneNumber(order.getPhoneNumber())
                .email(order.getEmail())
                .address(order.getAddress())
                .note(order.getNote())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalMoney(order.getTotalMoney())
                .shippingMethod(order.getShippingMethod())
                .shippingAddress(order.getShippingAddress())
                .shippingDate(order.getShippingDate())
                .paymentMethod(order.getPaymentMethod())
                .orderDetails(order.getOrderDetails())
                .build();
        return orderResponse;
    }
}
