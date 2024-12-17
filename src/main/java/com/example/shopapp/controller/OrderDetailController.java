package com.example.shopapp.controller;

import com.example.shopapp.dto.OrderDetailDTO;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.model.OrderDetail;
import com.example.shopapp.responses.OrderDetailResponse;
import com.example.shopapp.services.OrderDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor
public class OrderDetailController {

    private final OrderDetailService orderDetailService;
    //Create one order detail
    @PostMapping("")
    public ResponseEntity<?> createNewOrder(@Valid @RequestBody OrderDetailDTO orderDetailDTO)
    {
        try {
            OrderDetailResponse orderDetailResponse = orderDetailService.createOrderDetail(orderDetailDTO);
            return ResponseEntity.ok().body(orderDetailResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //Get order detail
    @GetMapping("{id}")
    public ResponseEntity<?> getOrderDetail(@Valid @PathVariable("id") Long id)
    {
        try {
            return ResponseEntity.ok().body(OrderDetailResponse.fromOrderDetail(orderDetailService.getOrderDetail(id)));
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //get order detail base on order id
    @GetMapping("/orders/{id}")
    public ResponseEntity<?> gerOrderDetailBaseOnOrderId(@Valid @PathVariable("id") Long orderId)
    {
        try {
            List<OrderDetail> orderDetails = orderDetailService.getOrderDetails(orderId);
            List<OrderDetailResponse> orderDetailResponses = orderDetails
                    .stream()
                    .map(OrderDetailResponse::fromOrderDetail)
                    .toList();
            return ResponseEntity.ok().body(orderDetailResponses);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //Update an order detail
    @PutMapping("{id}")
    public  ResponseEntity<?> updateOrderDetail(@Valid @PathVariable("id") Long id, @RequestBody OrderDetailDTO orderDetailDTO)
    {
        try {
            OrderDetail orderDetail = orderDetailService.updateOrderDetail(id,orderDetailDTO);
            return  ResponseEntity.ok().body(orderDetail);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Delete an order detail
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteOrderDetail(@Valid @PathVariable("id") Long id)
    {
        orderDetailService.deleteOrderDetail(id);
        return  ResponseEntity.ok("Remove order detail successfully "+ id);
    }
}
