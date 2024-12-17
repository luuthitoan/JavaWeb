package com.example.shopapp.services;

import com.example.shopapp.dto.CartItemDTO;
import com.example.shopapp.model.Cart;
import com.example.shopapp.model.Product;
import com.example.shopapp.model.User;
import com.example.shopapp.repositories.CartRepository;
import com.example.shopapp.repositories.ProductRepository;
import com.example.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService{
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    public void addProduct(CartItemDTO cartItemDTO) throws Exception {
        Optional< Cart> cart = cartRepository.findByUserIdAndProductId(cartItemDTO.getUserId(),cartItemDTO.getProductId());
        if(cart.isPresent())
        {
            Integer oldQuantity = cart.get().getQuantity();
            cart.get().setQuantity(oldQuantity+cartItemDTO.getQuantity());
            cartRepository.save(cart.get());
        }
        else
        {
            Optional<Product> product = productRepository.findById(cartItemDTO.getProductId());
            Optional<User> user = userRepository.findById(cartItemDTO.getUserId());
            if(!user.isPresent()||!product.isPresent())
            {
                throw new Exception("Product or user is not existed !");
            }
            Cart newCart = Cart.builder()
                    .product(product.get())
                    .user(user.get())
                    .quantity(cartItemDTO.getQuantity()
                    ).build();
            cartRepository.save(newCart);
        }
    }

    public List<Cart> getCartByUserId(Long userId) throws Exception {
        return cartRepository.findByUserId(userId);
    }


    public void deleteProductInCart(Long userId, Long productId) throws Exception {
        Optional<Cart> existedCart = cartRepository.findByUserIdAndProductId(userId,productId);
        cartRepository.delete(existedCart.get());
    }


    public void deleteProductOfUser(Long userId) throws Exception {
       List<Cart> carts = getCartByUserId(userId);
        for (Cart cart: carts
             ) {
            this.cartRepository.delete(cart);
        }
    }
}
