package com.example.shopapp.services;

import com.example.shopapp.dto.CategoryDTO;
import com.example.shopapp.model.Category;
import com.example.shopapp.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService{
    private final CategoryRepository categoryRepository;

    public Category createCategory(CategoryDTO category) {
        Category newCategory = Category.builder().name(category.getName()).build();
        return categoryRepository.save(newCategory);
    }


    public Category getCategoryById(long id) {
        return categoryRepository.findById(id).orElseThrow(()->new RuntimeException("Category not found"));
    }


    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }


    public Category updateCategory(long id, CategoryDTO category) {
        Category existingCategory = getCategoryById(id);
        existingCategory.setName(category.getName());
        categoryRepository.save(existingCategory);
        return existingCategory;
    }


    public void deleteCategory(long id) {

        categoryRepository.deleteById(id);
    }
}
