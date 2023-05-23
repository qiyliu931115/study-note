package com.example.springdemo.headfirst.responsibilityChainLinked;

/**
 * 链式
 */
public abstract class Handler {

    Handler successor = null;

    public void setSuccessor(Handler successor) {
        this.successor = successor;
    }

    public final void handler (String args) {
        doHandler(args);
        if (successor!= null) {
            successor.handler(args);
        }
    }

    abstract void doHandler(String args);
}
