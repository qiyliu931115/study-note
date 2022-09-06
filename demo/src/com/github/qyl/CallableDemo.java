package com.github.qyl;

import java.util.Timer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

class MyThread implements Runnable {

    @Override
    public void run() {

    }
}

class Callable implements java.util.concurrent.Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        TimeUnit.SECONDS.sleep(2);
        System.out.println(Thread.currentThread().getName() + "11111");
        return 11111;
    }
}

public class CallableDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable());
        FutureTask<Integer> futureTask2 = new FutureTask<Integer>(new Callable());

        Thread thread = new Thread(futureTask, "AA");
        thread.start();
        Thread thread2 = new Thread(futureTask2, "BB");
        thread2.start();
        System.out.println(Thread.currentThread().getName());
        int result = 100;

        while (!futureTask.isDone()) {
            //自旋判断task是否完毕
        }

        int result2 = futureTask.get();//futureTask.get()是阻塞的

        System.out.println(result + result2);
    }
}
