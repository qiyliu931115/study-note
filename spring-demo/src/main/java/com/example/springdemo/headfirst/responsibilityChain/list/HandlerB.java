package com.example.springdemo.headfirst.responsibilityChain.list;

public class HandlerB implements IHandler {
    @Override
    public boolean handler(String args) {
        boolean handler = false;
        System.out.println("HandlerB handler " + args);
        return handler;
    }

}