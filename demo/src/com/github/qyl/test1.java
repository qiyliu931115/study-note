package com.github.qyl;

public class test1 extends parent {

    public static void main(String[] args) {

    }

    @Override
    public synchronized void method1() {
        System.out.println("sss");
        int i= 0;
        i++;
    }
}
