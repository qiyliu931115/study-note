package com.github.qyl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockTest {

    private static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public static void main(String[] args) {
        Integer test = 2;
        ExecutorService threadPool = Executors.newFixedThreadPool(10);


        for (int i = 0; i < 3; i++) {
            threadPool.execute(() -> {
                try {
                    readWriteLock.writeLock().lockInterruptibly();
                    System.out.println("写入数据" + test);
                    // 假装耗时操作
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    readWriteLock.writeLock().unlock();
                }
            });
        }
        for (int i = 0; i < 20; i++) {
            threadPool.execute(() -> {
                try {
                    readWriteLock.readLock().lockInterruptibly();
                    System.out.println("读取数据" + test);
                    // 假装耗时操作
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    readWriteLock.readLock().unlock();
                }
            });
        }
    }
}
