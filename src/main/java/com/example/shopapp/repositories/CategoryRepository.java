package com.example.shopapp.repositories;

import com.example.shopapp.model.Category;
import com.example.shopapp.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//no need annotation repository because when extends super class JpaRepository Java Spring boot know that is repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

}
