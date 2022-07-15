package com.github.qyl;

import java.lang.ref.SoftReference;
import java.util.concurrent.TimeUnit;

public class softReference {

    public static void main(String[] args) throws InterruptedException {
        SoftReference<Byte[]> softReference = new SoftReference(new Byte[1024 *1024*10]);

        System.out.println(softReference.get());

        System.gc();

        TimeUnit.SECONDS.sleep(2);

        System.out.println(softReference.get());


        Byte[] bytes =  new Byte[1024 *1024*10];

        System.out.println(softReference.get());

    }
}
