package com.github.qyl;

import java.util.concurrent.*;

public class CustomThreadPoolDemo {
    public static void main(String[] args) {

        Runtime.getRuntime().availableProcessors();

        ExecutorService executorService = new ThreadPoolExecutor(
                2, 5,1,TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(3),Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardOldestPolicy());

        //模拟十个用户办理业务
        try{
            for (int i = 1;i <=9;i++) {
                int finalI = i;
                executorService.execute(()->{
                    System.out.println(Thread.currentThread().getName() + "办理业务:" + finalI);
                });
                //TimeUnit.MILLISECONDS.sleep(200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            executorService.shutdown();
        }
    }
}
