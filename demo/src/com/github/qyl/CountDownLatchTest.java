package com.github.qyl;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchTest {

    public static void main(String[] args) throws InterruptedException {
        // 让2个线程去等待3个三个工作线程执行完成
        CountDownLatch c = new CountDownLatch(3);

        // 2 个等待线程
        WaitThread waitThread1 = new WaitThread("wait-thread-1", c);
        WaitThread waitThread2 = new WaitThread("wait-thread-2", c);

        // 3个工作线程
        Worker worker1 = new Worker("worker-thread-1", c);
        Worker worker2 = new Worker("worker-thread-2", c);
        Worker worker3 = new Worker("worker-thread-3", c);

        // 启动所有线程
        waitThread1.start();
        waitThread2.start();
        Thread.sleep(1000);
        worker1.start();
        worker2.start();
        worker3.start();
    }
}

/**
 * 等待线程
 */
class WaitThread extends Thread {

    private String name;
    private CountDownLatch c;

    public WaitThread(String name, CountDownLatch c) {
        this.name = name;
        this.c = c;
    }

    @Override
    public void run() {
        try {
            // 等待
            System.out.println(this.name + " wait...");
            c.await();
            System.out.println(this.name + " continue running...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/**
 * 工作线程
 */
class Worker extends Thread {

    private String name;
    private CountDownLatch c;

    public Worker(String name, CountDownLatch c) {
        this.name = name;
        this.c = c;
    }

    @Override
    public void run() {
        System.out.println(this.name + " is running...");
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(this.name + " is end.");
        c.countDown();
    }
}
