package com.example.datastructure.base.recursion;

public class RecursionTest {
    public static void main(String[] args) {
        System.out.println(mul(3));
    }


    public static int mul(int num) {
        if (num == 1) {
            return num;
        }
        return mul(num - 1) * num;

    }
}
