package com.github.qyl;

public class MyThread1  extends Thread {
    private String name;

    public MyThread1(String name) {
        this.name = name;
    }
    @Override
    public void run(){

        System.out.println(name+"运行");


    }

    public static void main(String[] args) throws InterruptedException {
        MyThread1 myThreadA = new MyThread1("A");
        MyThread1 myThreadB = new MyThread1("B");
        MyThread1 myThreadC = new MyThread1("C");
        myThreadA.start();
        /**join的意思是使得放弃当前线程的执行，并返回对应的线程，例如下面代码的意思就是：
         程序在main线程中调用t1线程的join方法，则main线程放弃cpu控制权，并返回t1线程继续执行直到线程t1执行完毕
         所以结果是t1线程执行完后，才到主线程执行，相当于在main线程中同步t1线程，t1执行完了，main线程才有执行的机会
         */
        myThreadA.join();
        myThreadB.start();
        myThreadB.join();
        myThreadC.start();
    }
}