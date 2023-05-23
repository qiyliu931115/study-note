package com.example.springdemo.headfirst.responsibilityChainList;

public class HandlerA implements IHandler {
    @Override
    public boolean handler(String args) {
        boolean handler = false;
        System.out.println("HandlerA handler " + args);
        return handler;
    }
}
