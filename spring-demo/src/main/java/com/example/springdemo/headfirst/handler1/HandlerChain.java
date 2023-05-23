package com.example.springdemo.headfirst.handler1;

import java.util.ArrayList;
import java.util.List;

public class HandlerChain {

    private List<IHandler> handlers = new ArrayList<>();

    public void  addHandler(IHandler iHandler) {
        this.handlers.add(iHandler);
    }

    public void handle(String args) {
        for (IHandler iHandler : handlers) {
            if (iHandler.handler(args)) {
                break;
            }
        }
    }

    /**
     * 如果某个处理链可以处理 不会继续往下传递请求
     * @param args
     */
    public static void main(String[] args) {
        HandlerChain handlerChain = new HandlerChain();
        handlerChain.addHandler(new HandlerA());
        handlerChain.addHandler(new HandlerB());
        handlerChain.addHandler(new HandlerC());
        handlerChain.handle("test");
    }
}
