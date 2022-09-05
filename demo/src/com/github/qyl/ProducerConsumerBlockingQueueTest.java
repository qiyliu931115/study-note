package com.github.qyl;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ProducerConsumerBlockingQueueTest {
    public static void main(String[] args) {
        MyResource myResource = new MyResource(new ArrayBlockingQueue<>(10));
        new Thread(()->{
            System.out.println(Thread.currentThread().getName() + "生产线程启动");
            try {
                myResource.producer();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"Prod").start();


        new Thread(()->{
            System.out.println(Thread.currentThread().getName() + "消费线程启动");
            try {
                myResource.consumer();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"Cons").start();

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myResource.stop();
    }
}


class MyResource {

    private volatile boolean FLAG = true;

    private AtomicInteger atomicInteger = new AtomicInteger();

    BlockingQueue<String> blockingQueue = null;

    public MyResource(BlockingQueue<String> blockingQueue) {
        System.out.println(blockingQueue.getClass().getName());
        this.blockingQueue = blockingQueue;
    }

    public void stop() {
        this.FLAG = false;
    }

    public void producer() throws InterruptedException {
        String data = null;
        boolean returnValue;
        while (FLAG) {
            data = atomicInteger.incrementAndGet() + "";
            returnValue = blockingQueue.offer(data, 2L, TimeUnit.SECONDS);
            if (returnValue) {
                System.out.println(Thread.currentThread().getName() + "插入队列" + data + "成功");
            } else {
                System.out.println(Thread.currentThread().getName() + "插入队列" + data + "失败");
            }
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println(Thread.currentThread().getName() + "叫停，flag等于false 生产动作结束");
    }

    public void consumer() throws InterruptedException {
        String result = null;
        while (FLAG) {
            result = blockingQueue.poll( 2L, TimeUnit.SECONDS);
            if (result == null ||result.equals("")) {
                FLAG = false;
                System.out.println(Thread.currentThread().getName() + "超过2秒没有消费数据，消费退出");
                return;
            } else {
                System.out.println(Thread.currentThread().getName() + "消费队列" + result + "成功");
            }
        }
    }
}