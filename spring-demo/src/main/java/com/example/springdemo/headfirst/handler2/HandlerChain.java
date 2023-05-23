package com.example.springdemo.headfirst.handler2;

public class HandlerChain {

    private Handler head = null;
    private Handler tail = null;

    public void addHandler (Handler handler) {
        handler.setSuccessor(null);

        if (head == null) {
            head = handler;
            tail = handler;
        }

        tail.setSuccessor(handler);
        tail = handler;
    }

    public void handle (String args) {
        if (head != null) {
            head.handler(args);
        }
    }

    public static void main(String[] args) {
        HandlerChain handlerChain = new HandlerChain();
        handlerChain.addHandler(new HandlerA());
        handlerChain.addHandler(new HandlerB());
        handlerChain.addHandler(new HandlerC());
        handlerChain.addHandler(new HandlerD());
        handlerChain.handle("args");
    }
}
