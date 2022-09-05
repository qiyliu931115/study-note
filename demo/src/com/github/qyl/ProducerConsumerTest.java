package com.github.qyl;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerTest {

    public static void main(String[] args) {

        Share share = new Share();

        new Thread(()->{
            for (int i = 0; i< 5;i++) {
                share.incr();
            }
        }, "A").start();

        new Thread(()->{
            for (int i = 0; i< 5;i++) {
                share.decr();
            }
        }, "B").start();

        new Thread(()->{
            for (int i = 0; i< 5;i++) {
                share.incr();
            }
        }, "C").start();

        new Thread(()->{
            for (int i = 0; i< 5;i++) {
                share.decr();
            }
        }, "D").start();
    }
}

class Share{

    public volatile int count = 0;

    private Lock lock = new ReentrantLock();

    private Condition condition = lock.newCondition();

    public void incr() {
        lock.lock();

        try {
            while (count != 0) {
                condition.await();

            }
            count++;
            System.out.println(Thread.currentThread().getName() + ":" + count);
            condition.signalAll();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            lock.unlock();
        }
    }

    public void decr() {
        lock.lock();

        try {
            while (count == 0) {
                condition.await();

            }
            count--;
            System.out.println(Thread.currentThread().getName() + ":" + count);
            condition.signalAll();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            lock.unlock();
        }

    }

}
