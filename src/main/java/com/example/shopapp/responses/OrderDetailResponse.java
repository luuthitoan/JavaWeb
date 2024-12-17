package com.example.shopapp.responses;

import com.example.shopapp.model.Order;
import com.example.shopapp.model.OrderDetail;
import com.example.shopapp.model.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {
    private Long id;
    @JsonProperty("order_id")
    private Long order_id;
    @JsonProperty("product_id")
    private Long product_id;
    @JsonProperty("price")
    private Float price;
    @JsonProperty("number_of_products")
    private Integer numberOfProducts;
    @JsonProperty("total_money")
    private Float totalMoney;
    public static OrderDetailResponse fromOrderDetail(OrderDetail orderDetail)
    {
       return OrderDetailResponse.builder()
                .id(orderDetail.getId())
                .order_id(orderDetail.getOrder().getId())
                .product_id(orderDetail.getProduct().getId())
                .numberOfProducts(orderDetail.getNumberOfProducts())
                .price(orderDetail.getPrice())
                .totalMoney(orderDetail.getTotalMoney()).build();
    }
}
