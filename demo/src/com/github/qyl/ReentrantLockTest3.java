package com.github.qyl;

public class ReentrantLockTest3 {


    public static void main(String[] args) {
        new ReentrantLockTest3().m1();
    }

    public synchronized void m1() {
        System.out.println("外---");
        m2();
    }

    public synchronized void m2() {
        System.out.println("中---");
        m3();
    }

    public synchronized void m3() {
        System.out.println("内---");
    }
}
