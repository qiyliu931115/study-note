package com.github.qyl;

import java.util.concurrent.locks.ReentrantLock;

public class testReentrantLock {


    public static void main(String[] args) {
        ReentrantLock reentrantLock = new ReentrantLock();

        reentrantLock.lock();
        reentrantLock.tryLock();
        reentrantLock.unlock();
    }
}
