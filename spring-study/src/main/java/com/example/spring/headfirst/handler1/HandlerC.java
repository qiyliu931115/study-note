package com.example.spring.headfirst.handler1;

public class HandlerC implements IHandler {
    @Override
    public boolean handler(String args) {
        boolean handler = false;
        System.out.println("HandlerC handler " + args);
        return handler;
    }

}