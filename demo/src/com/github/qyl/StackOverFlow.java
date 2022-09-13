package com.github.qyl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StackOverFlow {

    public static void main(String[] args) {


        List<String> list = new ArrayList<>();
        while (true) {
            int i = new Random().nextInt(Integer.MAX_VALUE);
            list.add(String.valueOf(i).intern());
        }

    }

}
