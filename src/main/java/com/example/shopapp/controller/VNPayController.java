package com.example.shopapp.controller;

import com.example.shopapp.dto.OrderDTO;
import com.example.shopapp.responses.ActionResponse;
import com.example.shopapp.responses.OrderResponse;
import com.example.shopapp.services.CartService;
import com.example.shopapp.services.OrderService;
import com.example.shopapp.services.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/vnpay")
@RequiredArgsConstructor
public class VNPayController {
    private final VNPayService vnPayService;
    private final OrderService orderService;
    private final CartService cartService;
    @PostMapping("/submitOrder")
    public ResponseEntity<?> submitOrder(@RequestBody OrderDTO orderDTO,
                                      HttpServletRequest request, HttpServletResponse response){
        try {
            //get total amount of product in cart base user id
            OrderResponse orderResponse = orderService.createOrder(orderDTO);
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            //create order and get order id
            String vnpayUrl = vnPayService.createOrder(orderResponse.getTotalMoney(), orderResponse.getId(), baseUrl);
            ActionResponse actionResponse = new ActionResponse();
            actionResponse.setMessage(vnpayUrl);
            return ResponseEntity.ok(actionResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/vnpay-payment")
    public void GetMapping(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //parameter have user id, if payment status is success then insert new order to
        int paymentStatus =vnPayService.orderReturn(request);
        String orderInfo = request.getParameter("vnp_OrderInfo");
        if(paymentStatus==1)
        {
            //delete product in cart
            OrderResponse order = orderService.getOrder(Long.valueOf(orderInfo));
            Long userId = order.getUserId();
            cartService.deleteProductOfUser(userId);
            response.sendRedirect("http://localhost:4200");
        }
        else
        {
            orderService.deleteOrder(Long.valueOf(orderInfo));
            response.sendRedirect("http://localhost:4200");
        }
//        String paymentTime = request.getParameter("vnp_PayDate");
//        String transactionId = request.getParameter("vnp_TransactionNo");
//        String totalPrice = request.getParameter("vnp_Amount");
////        model.addAttribute("orderId", orderInfo);
////        model.addAttribute("totalPrice", totalPrice);
////        model.addAttribute("paymentTime", paymentTime);
////        model.addAttribute("transactionId", transactionId);
//        return paymentStatus == 1 ? "ordersuccess" : "orderfail";
    }
}
