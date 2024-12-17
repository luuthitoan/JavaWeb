package com.example.shopapp.repositories;

import com.example.shopapp.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    //find order of user base on user id
    List<Order> findByUserId(Long userId);
    @Query("SELECT o FROM Order o WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR o.fullName LIKE %:keyword% OR o.address LIKE %:keyword% " +
            "OR o.note LIKE %:keyword%)")
    Page<Order> findByKeyword(String keyword, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE (:keyword IS NULL OR :keyword = '' OR o.fullName LIKE %:keyword% OR o.address LIKE %:keyword% OR o.note LIKE %:keyword%) AND ( o.user.id= :userId)")
    Page<Order> findByKeyWordUserId(String keyword,Long userId,Pageable pageable);
}
