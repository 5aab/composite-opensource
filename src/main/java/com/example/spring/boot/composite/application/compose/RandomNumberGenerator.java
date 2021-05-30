package com.example.spring.boot.composite.application.compose;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomNumberGenerator extends Random {

    public int nextNonNegative() {
        return next(Integer.SIZE - 1);
    }
}
