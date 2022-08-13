package com.example.datastructure.base.sort;

import java.util.Arrays;

public class BubbleSort {

    public static void main(String[] args) {
        int[] arr = new int[]{3,9,-1,10,-2};

        //表示是否进行过交换
        boolean flag = false;
        //冒泡排序的时间复杂度是O（n^2）
        for (int i = 0; i < arr.length -1 ;i++) {

            for (int j = 0; j < arr.length -1 - i ;j++) {
                //如果前面的数比后面的数打，则交换
                if (arr[j] > arr[j+1]) {
                    flag = true;
                    int temp = arr[j+1];
                    arr[j+1] = arr[j];
                    arr[j] = temp;

                }
            }
            if (!flag) {
                //一趟排序中 一次交换没发生
                break;
            }  else {
                flag =false; //将flag重置
            }

            System.out.println("第" + (i+1) + "排序结果" + Arrays.toString(arr));
        }
        System.out.println("排序结果" + Arrays.toString(arr));

//        for (int j = 0; j < arr.length -1 ;j++) {
//            //如果前面的数比后面的数打，则交换
//            if (arr[j] > arr[j+1]) {
//                int temp = arr[j+1];
//                arr[j+1] = arr[j];
//                arr[j] = temp;
//            }
//        }
//        System.out.println("第1次排序" + Arrays.toString(arr));

//        for (int j = 0; j < arr.length -2 ;j++) {
//            //如果前面的数比后面的数打，则交换
//            if (arr[j] > arr[j+1]) {
//                int temp = arr[j+1];
//                arr[j+1] = arr[j];
//                arr[j] = temp;
//            }
//        }
//        System.out.println("第2次排序" + Arrays.toString(arr));
//
//        for (int j = 0; j < arr.length -3 ;j++) {
//            //如果前面的数比后面的数打，则交换
//            if (arr[j] > arr[j+1]) {
//                int temp = arr[j+1];
//                arr[j+1] = arr[j];
//                arr[j] = temp;
//            }
//        }
//        System.out.println("第3次排序" + Arrays.toString(arr));
//
//        for (int j = 0; j < arr.length -4 ;j++) {
//            //如果前面的数比后面的数打，则交换
//            if (arr[j] > arr[j+1]) {
//                int temp = arr[j+1];
//                arr[j+1] = arr[j];
//                arr[j] = temp;
//            }
//        }
//        System.out.println("第4次排序" + Arrays.toString(arr));



     }
}
