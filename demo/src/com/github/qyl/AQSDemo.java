package com.github.qyl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class AQSDemo {

    public static void main(String[] args) {

        ReentrantLock reentrantLock = new ReentrantLock();

        new Thread(()->{
            reentrantLock.lock();
            try {
                TimeUnit.MINUTES.sleep(20);
                System.out.println("A thread come in");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                reentrantLock.unlock();
            }
        },"A").start();

        new Thread(()->{
            reentrantLock.lock();
            try {
                System.out.println("B thread come in");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                reentrantLock.unlock();
            }
        },"B").start();

        new Thread(()->{
            reentrantLock.lock();
            try {
                System.out.println("C thread come in");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                reentrantLock.unlock();
            }
        },"C").start();
    }
}
