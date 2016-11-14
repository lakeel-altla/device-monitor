package com.lakeel.device.management.data;

public enum Type {
    UNKNOWN(0), FOUND(1), LOST(2), DISTANCE(3), ;

    private int value;

    Type(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
