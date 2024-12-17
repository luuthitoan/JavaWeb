package com.example.shopapp.services;

import com.example.shopapp.dto.OrderDetailDTO;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.model.Order;
import com.example.shopapp.model.OrderDetail;
import com.example.shopapp.model.Product;
import com.example.shopapp.repositories.OrderDetailRepository;
import com.example.shopapp.repositories.OrderRepository;
import com.example.shopapp.repositories.ProductRepository;
import com.example.shopapp.responses.OrderDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService{
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderDetailResponse createOrderDetail(OrderDetailDTO orderDetailDTO) throws Exception {
        //check order id exist
        Order existedOrder = orderRepository.findById(orderDetailDTO.getOrderId()).orElseThrow(
                ()->new DataNotFoundException("Id of order not valid")
        );
        //check product exist
        Product existedProduct = productRepository.findById(orderDetailDTO.getProductId()).orElseThrow(
                ()->new DataNotFoundException("Id of product is not valid")
        );
        //create order detail
        OrderDetail orderDetail = OrderDetail.builder()
                .order(existedOrder)
                .product(existedProduct)
                .price(orderDetailDTO.getPrice())
                .numberOfProducts(orderDetailDTO.getNumberOfProduct())
                .totalMoney(orderDetailDTO.getTotalMoney())
                 .build();
        orderDetailRepository.save(orderDetail);
        return OrderDetailResponse.fromOrderDetail(orderDetail);
    }


    public OrderDetail getOrderDetail(Long id) throws DataNotFoundException {
        return orderDetailRepository.findById(id).orElseThrow(
                ()->new DataNotFoundException("Can't find order detail with id + "+ id)
        );
    }


    public OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
        //Check existed order detail
        OrderDetail existedOrderDetail = orderDetailRepository.findById(id).orElseThrow(
                ()->new DataNotFoundException("Id of order detail not valid")
        );
        //check exist order
        Order existedOrder = orderRepository.findById(orderDetailDTO.getOrderId()).orElseThrow(
                ()->new DataNotFoundException("Id of order is not valid")
        );
        //check exist product
        Product existedProduct = productRepository.findById(orderDetailDTO.getProductId()).orElseThrow(
                ()->new DataNotFoundException("Id of product is not valid")
        );
       existedOrderDetail.setPrice(orderDetailDTO.getPrice());
       existedOrderDetail.setOrder(existedOrder);
       existedOrderDetail.setProduct(existedProduct);
       existedOrderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
       existedOrderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProduct());
        return orderDetailRepository.save(existedOrderDetail);
    }


    public void deleteOrderDetail(Long id) {
        orderDetailRepository.deleteById(id);
    }


    public List<OrderDetail> getOrderDetails(Long orderId) throws DataNotFoundException {
        Order existedOrder = orderRepository.findById(orderId).orElseThrow(
                ()->new DataNotFoundException("Id of order not valid")
        );
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
        return orderDetails;
    }
}
