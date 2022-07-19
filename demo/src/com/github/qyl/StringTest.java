package com.github.qyl;

public class StringTest {

    public static void main(String[] args) {
        String s = new String("1");
        s.intern();
        String s2 = "1";
        System.out.println(s == s2); //false

        String s3 = new String("1") + new String("2");
        s3.intern();
        String s4 = "12";
        System.out.println(s3 == s4); //true


        String s5 = new String("1");
        String s6 = "1";
        System.out.println(s5 == s6);
    }
}
