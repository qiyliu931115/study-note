package com.github.qyl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolDemo {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);//一池5个处理线程

        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();//一池1个处理线程

        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();//一池N个处理线程


        //模拟十个用户办理业务
        try{
            for (int i = 1;i <=10;i++) {
                newCachedThreadPool.execute(()->{
                    System.out.println(Thread.currentThread().getName() + "办理业务");
                });

                TimeUnit.MILLISECONDS.sleep(200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            executorService.shutdown();
        }
    }
}
