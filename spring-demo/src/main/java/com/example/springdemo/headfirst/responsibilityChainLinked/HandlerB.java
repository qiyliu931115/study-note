package com.example.springdemo.headfirst.responsibilityChainLinked;

public class HandlerB extends  Handler{
    @Override
    void doHandler(String args) {
        System.out.println("HandlerB " + args);
    }
}
