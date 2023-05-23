package com.example.springdemo.headfirst.responsibilityChainLinked;

public class HandlerC extends  Handler{
    @Override
    void doHandler(String args) {
        System.out.println("HandlerC " + args);
    }
}
