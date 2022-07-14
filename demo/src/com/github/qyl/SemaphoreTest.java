package com.github.qyl;

import java.util.concurrent.Semaphore;

public class SemaphoreTest {

    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(2);
        for (int i = 0; i <= 10; i ++) {
            MyRunnable runnable = new MyRunnable(semaphore);
            Thread thread = new Thread(runnable, "Thread-" + i);
            thread.start();
        }
    }

    private static class MyRunnable implements Runnable {
        // 成员属性 Semaphore对象
        private final Semaphore semaphore;
        public MyRunnable(Semaphore semaphore) {
            this.semaphore = semaphore;
        }
        public void run() {
            String threadName = Thread.currentThread().getName();
            // 获取许可
            boolean acquire = semaphore.tryAcquire();
            // 未获取到许可 结束
            if (!acquire) {
                System.out.println("线程【" + threadName + "】未获取到许可，结束");
                return;
            }
            // 获取到许可
            try {
                System.out.println("线程【" + threadName + "】获取到许可");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 释放许可
                semaphore.release();
                System.out.println("线程【" + threadName + "】释放许可");
            }
        }
    }
}
