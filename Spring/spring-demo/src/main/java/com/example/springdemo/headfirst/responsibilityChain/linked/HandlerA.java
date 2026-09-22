package com.example.springdemo.headfirst.responsibilityChain.linked;

public class HandlerA extends  Handler{
    @Override
    void doHandler(String args) {
        System.out.println("HandlerA " + args);
    }
}
