package com.example.fashionshop.validation;

import com.example.fashionshop.model.Order;
import com.example.fashionshop.model.commons.Stock;
import com.example.fashionshop.model.commons.enums.OrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class OrderValidator {

    public static void validateOrder(Order order, HttpStatus status, String message) {
        if (!(order.getCount() > ValidationConstants.ORDER_PRODUCT_COUNT_MIN_VALUE &&
                order.getCount() <= ValidationConstants.ORDER_PRODUCT_COUNT_MAX_VALUE
                ) || order.getDate() == 0) {
            throw new ResponseStatusException(status,message);
        }
    }

    public static void validateOrderChangeStatus(Order order, OrderStatus newStatus, HttpStatus httpStatus, String message) {
        Stock stock = order.getProduct().getStock();
        if (newStatus == OrderStatus.UNPAID) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Illegal argument passed for order to change status");
        }

        if (!stock.getIsAvailable() ||
                stock.getCount() - order.getCount() < 0) {
            throw new ResponseStatusException(httpStatus, message);
        }
    }
}
