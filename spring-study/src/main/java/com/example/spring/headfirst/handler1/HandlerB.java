package com.example.spring.headfirst.handler1;

public class HandlerB implements IHandler {
    @Override
    public boolean handler(String args) {
        boolean handler = true;
        System.out.println("HandlerB handler " + args);
        return handler;
    }

}