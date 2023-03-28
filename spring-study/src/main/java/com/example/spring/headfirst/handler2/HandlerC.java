package com.example.spring.headfirst.handler2;

public class HandlerC extends  Handler{
    @Override
    void doHandler(String args) {
        System.out.println("HandlerC " + args);
    }
}
