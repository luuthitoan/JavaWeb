package com.example.shopapp.controller;

import com.example.shopapp.dto.ProductDTO;
import com.example.shopapp.dto.ProductImageDTO;
import com.example.shopapp.model.Product;
import com.example.shopapp.model.ProductImage;
import com.example.shopapp.responses.ActionResponse;
import com.example.shopapp.responses.ProductListResponse;
import com.example.shopapp.responses.ProductResponse;
import com.example.shopapp.services.ProductImageService;
import com.example.shopapp.services.ProductService;
import com.github.javafaker.Faker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductImageService productImageService;
    @GetMapping("")
    ResponseEntity<ProductListResponse> getAllProducts(
                                                       @RequestParam(defaultValue = "")  String keyword,
                                                       @RequestParam(defaultValue = "0",name = "category_id") Long categoryId,
                                                       @RequestParam(value = "page",defaultValue = "1") int page,
                                                       @RequestParam(value="limit",defaultValue = "8") int limit

    )
    {
        PageRequest pageRequest = PageRequest.of(page,limit, Sort.by("id").ascending());
        Page<ProductResponse> productPage = productService.getAllProducts(keyword,categoryId,pageRequest);
        int totalPages = productPage.getTotalPages();
        List<ProductResponse> products=productPage.getContent();
        ProductListResponse returnData = new ProductListResponse(products,totalPages);
        return  ResponseEntity.status(HttpStatus.OK).body(returnData);
    }

    //Get a product base on id
    @GetMapping("{id}")
    ResponseEntity<?> getProduct(@PathVariable Long id)
    {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ProductResponse.fromProduct(productService.getProductById(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //Create a product
    @PostMapping(value = "")
    public ResponseEntity<?> createProduct(@Valid  @RequestBody ProductDTO productDTO,
                                           BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessage);
            }
            Product newProduct = productService.createProduct(productDTO);
            return ResponseEntity.status(HttpStatus.OK).body(newProduct);
        } catch (Exception ex) {
            return  ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    //upload image of product
    @PostMapping(value = "uploads/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages( @PathVariable("id") Long productId,
            @ModelAttribute("files") List<MultipartFile> files) {
        try {
            List<ProductImage> productImages = new ArrayList<>();
            files = files == null ? new ArrayList<>() : files;
            if(files.size()>ProductImage.MAXIMUM_IMAGES_PER_PRODUCT)
            {
                return ResponseEntity.badRequest().body("You can only upload maximum 5 images");
            }
            for (MultipartFile file : files) {
                if (file.getSize() == 0) {
                    continue;
                }
                if (file.getSize() > 10 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File is too large");
                }
                String contentType = file.getContentType();
                if (!isImage(file)) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be an image");
                }
                Product existedProduct = productService.getProductById(productId);
                String fileName = storeFile(file);
                ProductImage productImage = productService.createProductImage(existedProduct.getId(), ProductImageDTO.builder()
                        .imageUrl(fileName)
                        .build());
                existedProduct.setThumbnail(productImage.getImageUrl());
                productImages.add(productImage);
            }
            return ResponseEntity.ok().body(productImages);
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //get image or product
    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName)
    {
        try {
            Path imagePath = Paths.get("uploads/"+imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());
            if(resource.exists())
            {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            }
            else
            {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpg").toUri()));
            }
        } catch (MalformedURLException e) {
            return  ResponseEntity.notFound().build();
        }
    }
    private boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
    private String storeFile(MultipartFile file) throws IOException
    {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFileName = UUID.randomUUID().toString()+"_"+filename;
        Path uploadDir = Paths.get("uploads");
        //Check exists folder exits and create new folder if not
        if(!Files.exists(uploadDir))
        {
            Files.createDirectories(uploadDir);
        }
        //Files destination
        Path destination = Paths.get(uploadDir.toString(),uniqueFileName);
        //Copy file to destination
        Files.copy(file.getInputStream(),destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }

    //Delete a product base on id
    @DeleteMapping("{id}")
    public  ResponseEntity<?> deleteProduct(@PathVariable Long id)
    {
        try {
            productService.deleteProduct(id);
            ActionResponse actionResponse = new ActionResponse();
            actionResponse.setMessage("Delete product successfully");
            return ResponseEntity.status(HttpStatus.OK).body(actionResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("product_images/{id}")
    public  ResponseEntity<?> deleteImage(@PathVariable Long id)
    {
        try {
            productImageService.deleteImage(id);
            ActionResponse actionResponse = new ActionResponse();
            actionResponse.setMessage("Delete product image successfully");
            return ResponseEntity.status(HttpStatus.OK).body(actionResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //Update a product base on id
    @PutMapping("{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO)
    {
        try {
            Product updateProduct = productService.updateProduct(id,productDTO);
            return ResponseEntity.ok().body(ProductResponse.fromProduct(updateProduct));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("generateFakeProducts")
    public ResponseEntity<String> generateFakeProducts()
    {
        Faker faker = new Faker();
        for(int i=0;i<1000;i++)
        {
            String productName = faker.commerce().productName();
            if(productService.existsByName(productName)) {
                continue;
            }
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price((float)faker.number().numberBetween(100000,2000000))
                    .thumbnail("")
                    .description(faker.lorem().sentence())
                    .categoryId((long)faker.number().numberBetween(3,7)).build();
            try {
                productService.createProduct(productDTO);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok().body("Fake data successfully!");
    }

    //get more product base on more id
    @GetMapping("by-ids")
    public ResponseEntity<?> getProductByIds(@RequestParam("ids") String ids)
    {
        try {
            List<Long> productsIds = Arrays.stream(ids.split(","))
                    .map(Long::parseLong).collect(Collectors.toList());
            List<Product> products = productService.findProductsByIds(productsIds);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
