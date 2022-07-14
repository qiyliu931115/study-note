package com.github.qyl;

import java.util.concurrent.locks.LockSupport;

public class SequentialPrinting {



    private static String STRING = "A1B2C3D4";

    private static volatile int  index = 0;

    private static volatile boolean flag = true;

    private static Object object = new Object();

    private static class Thread1 implements Runnable {

        @Override
        public void run() {
            synchronized (this) {

                while (flag) {
                    System.out.println(Thread.currentThread().getName() + "->" + STRING.charAt(index));
                    flag = false;
                    index = index++;
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.notifyAll();
                }

                this.notifyAll();

            }
        }
    }

    private static class Thread2 implements Runnable {

        @Override
        public void run() {
            synchronized (this) {

                while (!flag) {
                    System.out.println(Thread.currentThread().getName() + "->" + STRING.charAt(index));
                    flag = true;
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.notifyAll();
                }

                System.out.println(Thread.currentThread().getName() + "->" + STRING.charAt(index));
                this.notifyAll();
            }
        }
    }


    public static void main(String[] args) {

        Thread1 thread1 = new Thread1();
        Thread2 thread2 = new Thread2();

        Thread t1 = new Thread(thread1, "线程1");
        t1.start();

        Thread t2 = new Thread(thread2, "线程2");
        t2.start();
    }





//    @Override
//    public void run() {
//        for (; index < string.length(); index ++) {
//            System.out.println(Thread.currentThread().getName() + "->" + string.charAt(index));
//            LockSupport.park();
//            LockSupport.park();
//        }
//    }
}
