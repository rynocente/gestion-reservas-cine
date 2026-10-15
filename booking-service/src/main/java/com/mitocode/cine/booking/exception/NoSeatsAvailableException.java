package com.mitocode.cine.booking.exception;

public class NoSeatsAvailableException extends RuntimeException {
    public NoSeatsAvailableException(String msg) {
        super(msg);
    }
}
