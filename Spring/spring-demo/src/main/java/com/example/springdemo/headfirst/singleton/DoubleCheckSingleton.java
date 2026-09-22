package com.example.springdemo.headfirst.singleton;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 双重检查锁单例模式中，使用volatile关键字来修饰单例对象的变量，是为了保证多线程环境下的可见性和有序性。
 *
 * 在双重检查锁单例模式中，第一次检查单例对象是否为null时，如果多个线程同时进入了这个判断语句，
 *
 * 那么它们都会认为单例对象为null，然后会进入同步代码块中，创建单例对象。
 *
 * 但是，如果没有使用volatile关键字来修饰单例对象的变量，那么在某个线程创建单例对象之后，其他线程可能无法立即看到这个变化，仍然会认为单例对象为null，
 *
 * 然后继续创建单例对象，这样就会创建多个实例，违背了单例模式的初衷。
 *
 * 使用volatile关键字来修饰单例对象的变量，可以保证在多线程环境下，单例对象的变化对其他线程是可见的，从而避免了创建多个实例的问题。
 *
 * 同时，volatile关键字还可以保证单例对象的创建是有序的，避免了指令重排的问题。
 */
public class DoubleCheckSingleton {

    private static volatile ThreadPoolTaskExecutor threadPoolTaskExecutor = null;

    public DoubleCheckSingleton () {
        System.out.println(Thread.currentThread().getName() + ":DoubleCheckSingleton");
    }

    public static ThreadPoolTaskExecutor getInstance() {
        if (threadPoolTaskExecutor == null) {
//            双重检查锁单例模式中，使用锁来保证线程安全，锁的对象是Singleton.class，而不是this对象，是因为this对象在多线程环境下是不安全的。
//
//            在单例模式中，单例对象是在类加载的时候就被创建出来的，因此锁的对象应该是类对象，而不是实例对象。
//
//            使用Singleton.class作为锁对象，可以保证在多线程环境下，只有一个线程可以进入同步代码块，创建单例对象。
//
//            而使用this对象作为锁对象，会出现多个实例的情况，因为在多线程环境下，每个线程都有自己的实例对象，锁的是自己的实例对象，无法保证只有一个线程可以进入同步代码块。
//
//            另外，使用Singleton.class作为锁对象，可以保证在类加载的时候就被初始化，避免了在多线程环境下出现多个实例的情况。
//
//            而使用this对象作为锁对象，需要在实例化之后才能使用，无法保证在实例化之前不会出现多个实例的情况。
            synchronized (DoubleCheckSingleton.class) {
                if (threadPoolTaskExecutor == null) {
                    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
                    threadPoolTaskExecutor.setCorePoolSize(5);
                    threadPoolTaskExecutor.setMaxPoolSize(5);
                    threadPoolTaskExecutor.setThreadNamePrefix("AsyncEventExecutor-");
                    threadPoolTaskExecutor.initialize();
                    return threadPoolTaskExecutor;
                }
            }
        }
        return threadPoolTaskExecutor;
    }

    public static void main(String[] args) {
        ThreadPoolTaskExecutor instance = getInstance();
        instance.execute(new Runnable() {
            @Override
            public void run() {
                // 执行任务
            }
        });
    }
}
