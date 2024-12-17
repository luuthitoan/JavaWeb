package com.example.shopapp.responses;

import com.example.shopapp.model.Product;
import com.example.shopapp.model.ProductImage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class ProductResponse extends BaseResponse {
    private Long id;
    private String name;
    private Float price;
    private String thumbnail;
    private String description;
    @JsonProperty("category_id")
    private Long categoryId;
    @JsonProperty("product_images")
    private List<ProductImage> productImages = new ArrayList<>();


    public static ProductResponse fromProduct(Product product)
    {
        ProductResponse productResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .categoryId(product.getCategory().getId())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .productImages(product.getProductImages())
                .description(product.getDescription()).build();
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setUpdatedAt(product.getUpdatedAt());
        return productResponse;
    }
}
