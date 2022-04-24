package com.example.fashionshop.controller;

import com.example.fashionshop.model.Order;
import com.example.fashionshop.model.commons.enums.OrderStatus;
import com.example.fashionshop.model.dto.responseDto.ResponseDto;
import com.example.fashionshop.service.OrderService;
import com.example.fashionshop.validation.OrderValidator;
import com.example.fashionshop.validation.ProductValidator;
import com.example.fashionshop.validation.UserValidator;
import com.example.fashionshop.validation.ValidationConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /***
     *
     * @param order is made from the provided information by front-end which includes
     *            •product
     *            •user
     *            •additional order details
     * @param userId property is used to determine if the user has authorisation to make changes in database
     * @return responseDto to inform front-end that process has been done successfully/ failed
     */
    @PostMapping
    ResponseEntity<ResponseDto> create(@RequestBody Order order,
                                       @RequestHeader String userId) {
        UserValidator.checkUserAuthorized(order.getUser().getId(), HttpStatus.UNAUTHORIZED, ValidationConstants.UNAUTHORIZED_ERROR);
        ProductValidator.validateCreateProduct(order.getProduct(), HttpStatus.BAD_REQUEST, ValidationConstants.ORDER_ERROR_PRODUCT);
        OrderValidator.validateOrder(order, HttpStatus.BAD_REQUEST, "validation for order failed, plz check order description");
        Order created = orderService.create(order);
        ResponseDto responseDto = new ResponseDto("Order created.");
        responseDto.addInfo("OrderId", String.valueOf(created.getId()));
        return ResponseEntity.ok(responseDto);
    }

    /***
     *
     * @return all orders
     */
    @GetMapping("get-all")
    ResponseEntity<List<Order>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    /***
     *
     * @param userId is used to get all the orders made by current user
     * @return returns to front-end all the orders by current user,if process has been done authorized/ unauthorized
     */
    @GetMapping("/user-order")
    ResponseEntity<List<Order>> getOrdersByUserId(@RequestHeader("user_id") String userId) {

        UserValidator.checkUserAuthorized(userId, HttpStatus.UNAUTHORIZED, "user is UNAUTHORIZED, plz SignUp at first");
        return ResponseEntity.ok(orderService.getAllById(userId));
    }

    /***
     *
     * @param userId is used to get all the orders made by current user
     * @param orderStatus is used to get orders with this mentioned status
     * @return returns an array of orders that matched provided user id and order status
     */
    @GetMapping("/order-status")
    ResponseEntity<List<Order>> getOrderByStatus(@RequestHeader("user_id") String userId,
                                                 @RequestHeader("status") OrderStatus orderStatus){
        UserValidator.checkUserAuthorized(userId, HttpStatus.UNAUTHORIZED, "user is UNAUTHORIZED, plz SignUp at first");
        return ResponseEntity.ok(orderService.getOrderByStatus(userId, orderStatus));
    }

    /***
     *
     * @param userId property is used to determine if the user has authorisation to make changes in database
     * @param orderId is to get the necessary order which status will be changed
     * @param orderStatus is the new status for the current order
     * @return responseDto to inform front-end that process has been done successfully/ failed
     *
     */
    @PutMapping("/change-status/{order_id}/{status}")
    ResponseEntity<ResponseDto> changeStatus(@RequestHeader("user_id") String userId,
                                             @PathVariable("order_id") Long orderId,
                                             @PathVariable("status") OrderStatus orderStatus){
        UserValidator.checkUserAuthorized(userId, HttpStatus.UNAUTHORIZED, "user is UNAUTHORIZED, plz SignUp at first");
        OrderValidator.validateOrderChangeStatus(orderService.getOrdersByUserId(orderId),orderStatus, HttpStatus.BAD_REQUEST, "products in stock is not available or the count is not enough!");
        orderService.changeStatus(orderId, orderStatus);
        ResponseDto responseDto = new ResponseDto("OrderStatus changed.");
        responseDto.addInfo("OrderStatus", String.valueOf(orderId));
        return ResponseEntity.ok(responseDto);
    }

    /***
     *
     * @param id is used to find the corresponding order that will be deleted
     * @param userId property is used to determine if the user has authorisation to make changes in database
     * @return responseDto to inform front-end that process has been done successfully/ failed
     */
    @DeleteMapping("/{order_id}")
    ResponseEntity<ResponseDto> delete(@PathVariable("order_id") Long id,
                                       @RequestHeader String userId) {
        UserValidator.checkUserAuthorized(userId, HttpStatus.UNAUTHORIZED, "user is UNAUTHORIZED, plz SignUp at first");
        orderService.delete(id);
        ResponseDto responseDto = new ResponseDto("Order deleted.");
        responseDto.addInfo("OrderId", String.valueOf(id));
        return ResponseEntity.ok(responseDto);
    }
}