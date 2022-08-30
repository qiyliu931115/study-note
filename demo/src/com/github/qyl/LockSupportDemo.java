package com.github.qyl;

public class LockSupportDemo {

    static Object object = new Object();

    public static void main(String[] args) {
        new Thread(()-> {
            synchronized (object){
                System.out.println(Thread.currentThread().getName() + "---come in");

                try {
                    //wait会释放锁，线程A阻塞等待，线程B获取锁，然后notify 唤醒A
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "---被唤醒");
            }

        },"A").start();

        new Thread(()-> {
            synchronized (object){
                object.notify();
                System.out.println(Thread.currentThread().getName() + "---通知");

                try {
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        },"B").start();
    }
}
