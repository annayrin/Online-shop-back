package com.example.fashionshop.service.impl;

import com.example.fashionshop.model.Order;
import com.example.fashionshop.model.Product;
import com.example.fashionshop.model.commons.enums.OrderStatus;
import com.example.fashionshop.model.dto.requestDto.OrderUpdateReqDto;
import com.example.fashionshop.repository.OrderRepository;
import com.example.fashionshop.service.OrderService;
import com.example.fashionshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    /***
     *
     * @param order the product that would be added in DB
     * @return new product which has added
     */
    @Override
    public Order create(Order order) {
        order.setOrderStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }

    /***
     *
     * @return all data from DB, if there is not any data will return empty List.
     */
    @Override
    public List<Order> getAll() {
        List<Order> all = orderRepository.findAll();
        Collections.sort(all, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return (int) (o2.getDate()- o1.getDate());
            }
        });
        return all;
    }

    /***
     *
     * @param id with the help of it will find the object from DB.
     * @return returns founded object or throws @ResponseStatusException(BAD_REQUEST).
     */
    @Override
    public List<Order> getAllById(String id) {
        return orderRepository
                .getAllByUserId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Orders with user_id:" + id + "  not found in database")
                );
    }

    @Override
    public Order getOrdersByUserId(long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "order with id:" + orderId + "  not found in database")
        );
    }

//    @Transactional
//    @Override
//    public Order update(String id, OrderUpdateReqDto reqDto) {
//        Order fromDb = orderRepository
//                .findById(id)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.BAD_REQUEST,
//                        "product with id:" + id + "  not found in database")
//                );
//        fromDb.setCount(reqDto.getCount());
//        fromDb.setOrderStatus(reqDto.getOrderStatus());
//
//        return fromDb;
//        return null;
//    }

    /***
     *
     * @param userId property is used to determine if the user
     *               has authorisation to get orders by provided status from DB
     * @param orderStatus is the provided status
     * @return all the orders that satisfied the request's conditions
     */
    @Override
    public List<Order> getOrderByStatus(String userId, OrderStatus orderStatus) {
        return getAll().stream()
                .filter(item->item.getOrderStatus()==orderStatus)
                .collect(Collectors.toList());
    }

    /***
     *
     * @param orderId finds the necessary order from DB by provided orderId
     * @param orderStatus change the status of the found order
     */
    @Override
    @Transactional
    public void changeStatus(Long orderId, OrderStatus orderStatus) {
        Order fromDb = orderRepository.findById(orderId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Order with id:" + orderId + "  not found in database"));
        Product product = productService.getById(fromDb.getProduct().getId());
        product.updateStock(fromDb.getOrderStatus(), orderStatus, fromDb.getCount());
        fromDb.setOrderStatus(orderStatus);
    }

    /***
     *
     * @param id find the order with provided id and deletes it
     */
    @Override
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }
}


