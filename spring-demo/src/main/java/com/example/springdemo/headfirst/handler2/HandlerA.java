package com.example.springdemo.headfirst.handler2;

public class HandlerA extends  Handler{
    @Override
    void doHandler(String args) {
        System.out.println("HandlerA " + args);
    }
}
