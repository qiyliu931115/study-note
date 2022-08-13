package com.example.datastructure.base.sort;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class BubbleSort {

    public static void main(String[] args) {
        //int[] arr = new int[]{3,9,-1,10,20};
//        int[] arr = new int[]{1,2,3,4,5,6};
//        bubbleSort(arr);

        int[] arr2 = new int[80000];
        for (int i = 0; i < 80000 ;i++) {
            arr2[i] = (int)(Math.random() * 8000000); //生成【0-8000000】的数
        }

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = simpleDateFormat.format(date);

        System.out.println("排序前的时间是" + dateStr);

        bubbleSort(arr2);

        Date date2 = new Date();
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr2 = simpleDateFormat2.format(date2);
        System.out.println("排序后的时间是" + dateStr2);

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

     public static void bubbleSort(int[] arr) {
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

             //System.out.println("第" + (i+1) + "排序结果" + Arrays.toString(arr));
         }
         //System.out.println("排序结果" + Arrays.toString(arr));
     }
}
