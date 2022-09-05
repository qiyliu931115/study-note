package com.github.qyl;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 多线程之间按顺序调用，实现A-B-C三个线程启动
 *
 * A打印5次 B打印10次 C打印15次
 *
 * 循环十轮
 */
public class ConditionTest {

    public static void main(String[] args) {

        ShareResource shareResource = new ShareResource();

        new Thread(()->{
            for (int i =0;i<10;i++){
                shareResource.print5();
            }
        },"AA").start();
        new Thread(()->{
            for (int i =0;i<10;i++){
                shareResource.print10();
            }
        },"BB").start();
        new Thread(()->{
            for (int i =0;i<10;i++){
                shareResource.print15();
            }
        },"CC").start();
    }
}


class ShareResource {

    private volatile int num = 1;

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private Condition condition2 = lock.newCondition();
    private Condition condition3 = lock.newCondition();

    public void print5(){
        lock.lock();
        try {
            while (num != 1) {

                condition.await();
            }
            for (int i = 1; i <= 5;i ++) {
                System.out.println(Thread.currentThread().getName() + ":"+ i);
            }
            num = 2;
            condition2.signal();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            lock.unlock();
        }

    }

    public void print10(){
        lock.lock();
        try {
            while (num != 2) {

                condition2.await();
            }
            for (int i = 1; i <= 10;i ++) {
                System.out.println(Thread.currentThread().getName() + ":"+ i);
            }
            num = 3;
            condition3.signal();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            lock.unlock();
        }

    }

    public void print15(){
        lock.lock();
        try {
            while (num != 3) {

                condition3.await();
            }
            for (int i = 1; i <= 15;i ++) {
                System.out.println(Thread.currentThread().getName() + ":"+ i);
            }
            num = 1;
            condition.signal();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            lock.unlock();
        }

    }


}