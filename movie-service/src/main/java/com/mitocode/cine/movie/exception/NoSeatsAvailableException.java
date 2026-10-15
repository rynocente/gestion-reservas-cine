package com.mitocode.cine.movie.exception;

public class NoSeatsAvailableException extends RuntimeException {
    public NoSeatsAvailableException(String msg) {
        super(msg);
    }
}
