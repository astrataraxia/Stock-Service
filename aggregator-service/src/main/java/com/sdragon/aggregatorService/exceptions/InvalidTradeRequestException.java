package com.sdragon.aggregatorService.exceptions;

public class InvalidTradeRequestException extends RuntimeException{

    public InvalidTradeRequestException(String message) {
        super(message);
    }
}
