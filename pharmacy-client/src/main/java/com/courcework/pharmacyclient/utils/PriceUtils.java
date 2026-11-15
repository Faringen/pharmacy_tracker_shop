package com.courcework.pharmacyclient.utils;

import com.courcework.pharmacyclient.dto.PriceDTO;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class PriceUtils {

    private PriceUtils(){

    }

    public static Optional<BigDecimal> findMinPrice(List<PriceDTO> prices){
        return prices.stream()
                .map(PriceDTO::price)
                .min(Comparator.naturalOrder());
    }

    public static boolean isMinPrice(PriceDTO item, List<PriceDTO> price){
        return findMinPrice(price)
                .map(min -> item.price().compareTo(min)==0)
                .orElse(false);
    }
}
