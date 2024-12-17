package com.example.shopapp.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductListResponse {
    private List<ProductResponse> products;
    private int totalPages;
}
