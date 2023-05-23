package com.example.springdemo.headfirst.responsibilityChainList;

public class HandlerC implements IHandler {
    @Override
    public boolean handler(String args) {
        boolean handler = true;
        System.out.println("HandlerC handler " + args);
        return handler;
    }

}