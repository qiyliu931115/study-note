package com.example.springdemo.headfirst.responsibilityChainLinked;

public class HandlerD extends  Handler{
    @Override
    void doHandler(String args) {
        System.out.println("HandlerD " + args);
    }
}
