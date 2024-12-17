package com.example.shopapp.controller;

import com.example.shopapp.component.LocalizationUtil;
import com.example.shopapp.dto.CategoryDTO;
import com.example.shopapp.model.Category;
import com.example.shopapp.responses.ActionResponse;
import com.example.shopapp.responses.CategoryResponse;
import com.example.shopapp.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final LocalizationUtil localizationUtils;
    @PostMapping("")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult result) {
        CategoryResponse categoryResponse = new CategoryResponse();
        if(result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            categoryResponse.setMessage(localizationUtils.getLocalizedMessage("Insert category failed !"));
            categoryResponse.setErrors(errorMessages);
            return ResponseEntity.badRequest().body(categoryResponse);
        }
        Category category = categoryService.createCategory(categoryDTO);
        categoryResponse.setCategory(category);
        categoryResponse.setMessage("Insert category successfully!");
        return ResponseEntity.ok(categoryResponse);
    }

    @GetMapping("")
    public ResponseEntity<List<Category>> getAllCategories(
    ) {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActionResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO
    ) {
        ActionResponse updateCategoryResponse = new ActionResponse();
        categoryService.updateCategory(id, categoryDTO);
        updateCategoryResponse.setMessage("Update category successfully!");
        return ResponseEntity.ok(updateCategoryResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDetailCategory(
            @PathVariable Long id
    ) {
        Category existedCategory = categoryService.getCategoryById(id);
        return ResponseEntity.ok(existedCategory);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        ActionResponse deleteCategoryResponse = new ActionResponse();
        deleteCategoryResponse.setMessage("Delete category successfully!");
        return ResponseEntity.ok(deleteCategoryResponse);
    }
}

