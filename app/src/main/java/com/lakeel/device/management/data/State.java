package com.lakeel.device.management.data;

public enum State {

    UNKNOWN(0), WAIT(1), SUCCESS(2), FAILED(3);

    private int value;

    State(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
