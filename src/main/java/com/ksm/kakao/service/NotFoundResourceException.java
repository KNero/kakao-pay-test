package com.ksm.kakao.service;

public class NotFoundResourceException extends Exception {
    public NotFoundResourceException(String message) {
        super(message);
    }
}
