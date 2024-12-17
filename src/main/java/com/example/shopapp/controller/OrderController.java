package com.example.shopapp.controller;

import com.example.shopapp.dto.OrderDTO;
import com.example.shopapp.responses.OrderListResponse;
import com.example.shopapp.responses.OrderResponse;
import com.example.shopapp.services.CartService;
import com.example.shopapp.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final CartService cartService;
    @PostMapping("")
    public ResponseEntity<?> createOrder(@RequestBody @Valid OrderDTO orderDTO, BindingResult result)
    {
        try
        {
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            OrderResponse orderResponse = orderService.createOrder(orderDTO);
            cartService.deleteProductOfUser(orderDTO.getUserID());
            return ResponseEntity.ok().body(orderResponse);
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //get information of order base on user id
    @GetMapping("/user/{user_id}")
    public ResponseEntity<?> getOrdersUser(@Valid @PathVariable("user_id") Long userId,  @RequestParam(defaultValue = "", required = false) String keyword,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int limit)
    {
        try {
            PageRequest pageRequest = PageRequest.of(
                    page, limit,
                    //Sort.by("createdAt").descending()
                    Sort.by("id").ascending()
            );
            Page<OrderResponse> orderPage = orderService
                    .getAllOrders(keyword,userId, pageRequest)
                    .map(OrderResponse::fromOrder);
            int totalPages = orderPage.getTotalPages();
            List<OrderResponse> orderResponses = orderPage.getContent();
            return ResponseEntity.ok(OrderListResponse
                    .builder()
                    .orders(orderResponses)
                    .totalPages(totalPages)
                    .build());
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getAnOrder(@Valid @PathVariable("id") Long id){
        try {
            return ResponseEntity.ok().body(orderService.getOrder(id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //update order
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@Valid @PathVariable long id,@Valid @RequestBody OrderDTO orderDTO)
    {
        try {
            return ResponseEntity.ok().body(orderService.updateOrder(id,orderDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Delete an order
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@Valid @PathVariable("id") Long id)
    {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok("Delete order successfully!" +id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-orders-by-keyword")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<OrderListResponse> getOrdersByKeyword(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                //Sort.by("createdAt").descending()
                Sort.by("id").ascending()
        );
        Page<OrderResponse> orderPage = orderService
                .getOrdersByKeyword(keyword, pageRequest)
                .map(OrderResponse::fromOrder);
        // Lấy tổng số trang
        int totalPages = orderPage.getTotalPages();
        List<OrderResponse> orderResponses = orderPage.getContent();
        return ResponseEntity.ok(OrderListResponse
                .builder()
                .orders(orderResponses)
                .totalPages(totalPages)
                .build());
    }
}
