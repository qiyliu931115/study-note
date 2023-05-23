package com.example.springdemo.headfirst.handler2;

public class HandlerB extends  Handler{
    @Override
    void doHandler(String args) {
        System.out.println("HandlerB " + args);
    }
}
