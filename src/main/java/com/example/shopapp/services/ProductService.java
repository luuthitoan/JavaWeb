package com.example.shopapp.services;

import com.example.shopapp.dto.ProductDTO;
import com.example.shopapp.dto.ProductImageDTO;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.exceptions.InvalidParamException;
import com.example.shopapp.model.Category;
import com.example.shopapp.model.Product;
import com.example.shopapp.model.ProductImage;
import com.example.shopapp.repositories.CategoryRepository;
import com.example.shopapp.repositories.ProductImageRepository;
import com.example.shopapp.repositories.ProductRepository;
import com.example.shopapp.responses.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    public Product createProduct(ProductDTO productDTO) throws Exception {
       Category category= categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(()->new DataNotFoundException("Can't find category id"));
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .thumbnail(productDTO.getThumbnail())
                .category(category).build();
        return productRepository.save(newProduct);
    }


    public Product getProductById(long id){
        return productRepository.findById(id).orElseThrow(()->new RuntimeException("Product is not exist!"));
    }


    public Page<ProductResponse> getAllProducts(String keyword,Long categoryId,PageRequest pageRequest) {
        return productRepository.findByKeywordAndCategoryId(keyword, categoryId, pageRequest).map(ProductResponse::fromProduct);
    }


    public Product updateProduct(long id, ProductDTO productDTO) throws DataNotFoundException {
        Product existedProduct = getProductById(id);
        if(existedProduct!=null)
        {
            Category category= categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(()->new DataNotFoundException("Can't find category id"));
            existedProduct.setName(productDTO.getName());
            existedProduct.setPrice(productDTO.getPrice());
            existedProduct.setDescription(productDTO.getDescription());
            existedProduct.setCategory(category);
            productRepository.save(existedProduct);
            return existedProduct;
        }
        return null;
    }


    public void deleteProduct(long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        optionalProduct.ifPresent(productRepository::delete);
    }


    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws Exception {
        //check exists product
        Product existedProduct = getProductById(productId);
        if(existedProduct!=null)
        {
            ProductImage newProductImage = ProductImage.builder()
                    .product(existedProduct)
                    .imageUrl(productImageDTO.getImageUrl()).build();
            //Not allowed insert over 5 images of product.
          int size = productImageRepository.findByProductId(productId).size();
          if(size>=ProductImage.MAXIMUM_IMAGES_PER_PRODUCT)
          {
              throw new InvalidParamException("Number of images must be <= "+ ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
          }
          return productImageRepository.save(newProductImage);
        }
        return null;
    }


    public List<Product> findProductsByIds(List<Long> productIds) {
        return productRepository.findProductByIds(productIds);
    }


    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }
}
