package com.github.qyl;

import java.util.concurrent.TimeUnit;

public class StringTest {

    public static void main(String[] args) throws InterruptedException {
        String s = new String("1");
        s.intern();
        String s2 = "1";
        System.out.println(s == s2); //false

        String s3 = new String("1") + new String("2");
        s3.intern();
        String s4 = "12";
        System.out.println(s3 == s4); //true


        String s5 = new String("1");
        String intern = s5.intern();
        String s6 = "1";
        System.out.println(intern == s6);

        TimeUnit.SECONDS.sleep(600);
    }
}
