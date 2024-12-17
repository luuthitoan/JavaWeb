package com.example.shopapp.controller;
import com.example.shopapp.dto.CartItemDTO;
import com.example.shopapp.model.Cart;
import com.example.shopapp.responses.ActionResponse;
import com.example.shopapp.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/carts")
@RequiredArgsConstructor
public class CartController {
    private  final CartService cartService;
    @PostMapping("")
    ResponseEntity<?> addProductToCart(@RequestBody CartItemDTO cartItemDTO)
    {
        try {
            cartService.addProduct(cartItemDTO);
            ActionResponse actionResponse = new ActionResponse();
            actionResponse.setMessage("Add product to cart successfully!");
            return ResponseEntity.ok().body(actionResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("{userId}")
    ResponseEntity<?> getCartByUserId(@PathVariable("userId") Long userId)
    {
        try {
            List<Cart> carts = cartService.getCartByUserId(userId);
            return ResponseEntity.ok(carts);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("{userId}/{productId}")
    ResponseEntity<?> deleteProductInCart(@PathVariable("userId") Long userId, @PathVariable("productId") Long productId)
    {
        try {
            cartService.deleteProductInCart(userId,productId);
            ActionResponse actionResponse = new ActionResponse();
            actionResponse.setMessage("Delete product in cart successfully");
            return ResponseEntity.status(HttpStatus.OK).body(actionResponse);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
