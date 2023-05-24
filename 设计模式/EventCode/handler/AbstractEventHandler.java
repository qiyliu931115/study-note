package com.yf.ShoppingCart.web.event.handler;

import com.yf.ShoppingCart.web.services.tk.TkService;
import com.yf.ShoppingCart.web.util.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

@Slf4j
public class AbstractEventHandler<T extends ApplicationEvent> {

    @Autowired
    public TkService tkService;

    @EventListener
    @Async("asyncEventExecutor")
    public void handleEvent(T event) {
        log.debug("handleMyEvent:{}", event);
    }
}
