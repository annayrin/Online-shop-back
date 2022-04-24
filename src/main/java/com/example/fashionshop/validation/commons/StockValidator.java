package com.example.fashionshop.validation.commons;

import com.example.fashionshop.model.commons.Stock;

public final class StockValidator {
    public static boolean validateStock(Stock stock) {
        if (stock.getCount() < 0) return false;
        return true;
    }
}
