package com.github.qyl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class LockSupportDemo3 {

    static Object object = new Object();
    static Lock lock = new ReentrantLock();
    static Condition condition = lock.newCondition();

    public static void main(String[] args) {
        Thread threadA = new Thread(() -> {
            try {


                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(Thread.currentThread().getName() + "---come in");
                LockSupport.park();
                System.out.println(Thread.currentThread().getName() + "---被唤醒");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "A");

        threadA.start();




        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + "---通知");
                LockSupport.unpark(threadA);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "B").start();

    }
}
