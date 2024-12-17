package com.example.shopapp.services;

import com.example.shopapp.repositories.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductImageService {
    private final ProductImageRepository productImageRepository;

    public void deleteImage(Long id) {
        productImageRepository.deleteById(id);
    }
}
