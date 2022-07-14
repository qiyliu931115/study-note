package com.github.qyl;

import sun.misc.Unsafe;

public class unsafeTest {

    public static void main(String[] args) {
        Unsafe unsafe = Unsafe.getUnsafe();
        long address = unsafe.getAddress(1);
        System.out.println(address);
    }
}
