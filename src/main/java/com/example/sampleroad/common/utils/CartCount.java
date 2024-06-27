package com.example.sampleroad.common.utils;

import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.cart.CartRepository;
import com.querydsl.core.Tuple;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@NoArgsConstructor
@Component
public class CartCount {
    public long countFalse;
    public long countTrue;

    public CartCount(long countFalse, long countTrue) {
        this.countFalse = countFalse;
        this.countTrue = countTrue;
    }

    public CartCount getCartCount(List<Tuple> tuples) {

        long countFalse = tuples.stream()
                .mapToLong(tuple -> tuple.get(0, Long.class))
                .sum();

        long countTrue = tuples.stream()
                .mapToLong(tuple -> tuple.get(1, Long.class))
                .sum();

        if (countTrue > 1L) {
            countTrue = 1L;
        }

        return new CartCount(countFalse, countTrue);
    }
}
