package com.github.qyl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

public class ABADemo {

    static AtomicReference<Integer> atomicReference = new AtomicReference<>(100);
    static AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<>(100, 1);

    public static void main(String[] args) {

        System.out.println("ABA问题的产生----");

        new Thread(() -> {
            atomicReference.compareAndSet(100, 101);
            atomicReference.compareAndSet(101, 100);
        },"t1").start();

        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            atomicReference.compareAndSet(100, 2019);

            System.out.println(atomicReference.get());

        },"t2").start();

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("ABA问题的解决----");

        new Thread(() -> {
            System.out.println("t3的第1次版本号" + atomicStampedReference.getStamp());

            //t3暂停1秒
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            atomicStampedReference.compareAndSet(100, 101, atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1);
            System.out.println("t3的第2次版本号" + atomicStampedReference.getStamp());


            atomicStampedReference.compareAndSet(101, 100, atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1);
            System.out.println("t3的第3次版本号" + atomicStampedReference.getStamp());

        },"t3").start();


        new Thread(() -> {
            int stamp = atomicStampedReference.getStamp();
            System.out.println("t4的第一次版本号" + stamp);

            //t4暂停3秒
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            boolean b = atomicStampedReference.compareAndSet(100, 2019, stamp, stamp + 1);
            System.out.println("t4的现场修改成功否：" + b + "当前最新实际版本号" + atomicStampedReference.getStamp());


            System.out.println("t4的实际最新值：" + atomicStampedReference.getReference());

        },"t4").start();
    }
}
