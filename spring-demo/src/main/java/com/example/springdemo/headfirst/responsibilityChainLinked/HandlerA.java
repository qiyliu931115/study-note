package com.example.springdemo.headfirst.responsibilityChainLinked;

public class HandlerA extends  Handler{
    @Override
    void doHandler(String args) {
        System.out.println("HandlerA " + args);
    }
}
