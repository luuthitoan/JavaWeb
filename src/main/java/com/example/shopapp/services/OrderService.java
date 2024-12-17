package com.example.shopapp.services;

import com.example.shopapp.dto.CartItemDTO;
import com.example.shopapp.dto.OrderDTO;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.model.*;
import com.example.shopapp.repositories.OrderDetailRepository;
import com.example.shopapp.repositories.OrderRepository;
import com.example.shopapp.repositories.ProductRepository;
import com.example.shopapp.repositories.UserRepository;
import com.example.shopapp.responses.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class OrderService{
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private  final OrderDetailRepository orderDetailRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public OrderResponse createOrder(OrderDTO orderDTO) throws Exception {
        //Check exist user id
        User user = userRepository.findById(orderDTO.getUserID()).orElseThrow(
                ()-> new DataNotFoundException("Can't not find use with id "+orderDTO.getUserID())
        );
        //convert order dto -> order use model mapper
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper->mapper.skip(Order::setId));
        // update all field except id of order
        Order order = new Order();
        modelMapper.map(orderDTO,order);
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setStatus(OrderStatus.PENDING);
        LocalDate shippingDate = orderDTO.getShippingDate()==null?LocalDate.now():orderDTO.getShippingDate();
        //shipping date > today
        if(shippingDate==null||shippingDate.isBefore(LocalDate.now()))
        {
            throw new DataNotFoundException("Date shipping must be at least today");
        }
        order.setTotalMoney(orderDTO.getTotalMoney());
        order.setActive(true);
        order.setShippingDate(shippingDate);
        orderRepository.save(order);
        //create list for order detail
        List<OrderDetail> orderDetails = new ArrayList<>();
        for(CartItemDTO cartItemDTO: orderDTO.getCartItems())
        {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            //get information of product in cart items
            Long productId = cartItemDTO.getProductId();
            int quantity = cartItemDTO.getQuantity();
            //find information of product
            Product product = productRepository.findById((productId))
                    .orElseThrow(()->new DataNotFoundException("Product not found with id : "+productId));
            //set information of order detail
            orderDetail.setProduct(product);
            orderDetail.setNumberOfProducts(quantity);
            orderDetail.setPrice(product.getPrice());
            orderDetail.setTotalMoney(product.getPrice()*quantity);
            orderDetails.add(orderDetail);
        }
        //save information of order detail to database
        orderDetailRepository.saveAll(orderDetails);
        return modelMapper.map(order,OrderResponse.class);
    }


    public OrderResponse getOrder(Long id) throws Exception {
        Order existedOrder = orderRepository.findById(id).orElseThrow(
                ()->new DataNotFoundException("Order not exist !")
        );

        return modelMapper.map(existedOrder,OrderResponse.class);
    }


    @Transactional
    public OrderResponse updateOrder(Long id, OrderDTO orderDTO) throws Exception {
        //Check exist order;
        Order existedOrder = orderRepository.findById(id).orElseThrow(
                ()->new DataNotFoundException("Order not exist !")
        );
        //Check exits user
        User existstedUser = userRepository.findById(orderDTO.getUserID()).orElseThrow(
                ()->new DataNotFoundException("User is note exist!")
        );
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper->mapper.skip(Order::setId));
        // update all field except id of order
        modelMapper.map(orderDTO,existedOrder);
        orderRepository.save(existedOrder);
        return modelMapper.map(existedOrder,OrderResponse.class);
    }


    @Transactional
    public void deleteOrder(Long id) throws Exception {
        Order existedOrder = orderRepository.findById(id).orElseThrow(
                ()->new DataNotFoundException("Order not exist !")
        );
        existedOrder.setActive(false);
    }


    public Page<Order> getAllOrders(String keyWord, Long userId, Pageable pageable) {
      return orderRepository.findByKeyWordUserId(keyWord,userId,pageable);
    }


    public Page<Order> getOrdersByKeyword(String keyword, Pageable pageable) {
        return orderRepository.findByKeyword(keyword, pageable);
    }
}
