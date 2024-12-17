package com.example.shopapp.repositories;

import com.example.shopapp.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsByName(String name);
    Page<Product> findByNameContainingAndCategory_id(String name, Long categoryId, Pageable pageable); //paging elements
    @Query("SELECT p FROM Product p WHERE (:keyword IS NULL OR :keyword ='' OR p.name LIKE %:keyword%) AND (:categoryId IS NULL OR :categoryId =0 OR p.category.id = :categoryId)")
    Page<Product> findByKeywordAndCategoryId(@Param("keyword") String name, @Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p where p.id IN :productIds")
    List<Product> findProductByIds(@Param("productIds") List<Long> productIDs);
}
