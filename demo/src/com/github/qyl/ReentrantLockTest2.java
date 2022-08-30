package com.github.qyl;

public class ReentrantLockTest2 {

    private static Object object = new Object();

    public static void main(String[] args) {
        m1();
    }

    public static void m1() {
        new Thread(()->{
            synchronized (object){
                System.out.println(Thread.currentThread().getName() + "---外层调用");
                synchronized (object){
                    System.out.println(Thread.currentThread().getName() + "---中层调用");
                    synchronized (object){
                        System.out.println(Thread.currentThread().getName() + "---内层调用");
                    }
                }
            }
        }).start();

    }}
