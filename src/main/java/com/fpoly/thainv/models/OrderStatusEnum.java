package com.fpoly.thainv.models;

import java.util.Arrays;
import java.util.Optional;

public enum OrderStatusEnum {
	ACTIVE(1),
    CANCELLED(5),
    CONFIRMED(2);

    private final int value;

    OrderStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Optional<OrderStatusEnum> valueOf(int value) {
        return Arrays.stream(values())
                .filter(enumVal -> enumVal.value == value)
                .findFirst();
    }
}