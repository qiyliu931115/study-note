package com.example.datastructure.base.sort;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

//选择排序还是要比冒泡要短的
public class SelectSort {


    public static void main(String[] args) {
//        int[] arr = {101,34,119,1};
//        selectSort0(arr);


        int[] arr2 = new int[80000];
        for (int i = 0; i < 80000 ;i++) {
            arr2[i] = (int)(Math.random() * 8000000); //生成【0-8000000】的数
        }

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = simpleDateFormat.format(date);

        System.out.println("排序前的时间是" + dateStr);

        selectSort0(arr2);

        Date date2 = new Date();
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr2 = simpleDateFormat2.format(date2);
        System.out.println("排序后的时间是" + dateStr2);
    }


    public static void selectSort0(int[] arr) {
        //原始数组 101,34,119,1

        for (int i =0 ; i< arr.length-1;i++) {
            int minIndex = i;//最小值下标 一开始假定0
            int min = arr[i];//最小值  一开始假定0
            for (int j =i + 1; j< arr.length;j++) {
                //如果想从大到小排序 min > arr[j]
                if (min > arr[j]) {//假定的最小值 并不是最小的
                    min = arr[j]; //重置min
                    minIndex = j; //重置minIndex
                }
            }

            //将最小值，放在arr[0]
            if (minIndex !=i) {
                arr[minIndex] = arr[i];
                arr[i] = min;
            }

            //System.out.println("第" + i + "轮后");
            //System.out.println(Arrays.toString(arr));
        }
    }

    public static void selectSort(int[] arr) {



        //原始数组 101,34,119,1
        //第一轮 1,34,119,101

        int minIndex = 0;//最小值下标 一开始假定0
        int min = arr[0];//最小值  一开始假定0
        for (int j =0 + 1; j< arr.length;j++) {
            if (min > arr[j]) {//假定的最小值 并不是最小的
                min = arr[j]; //重置min
                minIndex = j; //重置minIndex
            }
        }

        //将最小值，放在arr[0]
        if (minIndex !=0) {
            arr[minIndex] = arr[0];
            arr[0] = min;
        }



        System.out.println("第一轮后");
        System.out.println(Arrays.toString(arr));


        //第2轮

        minIndex = 1;//最小值下标 一开始假定0
        min = arr[1];//最小值  一开始假定0
        for (int j =1 + 1; j< arr.length;j++) {
            if (min > arr[j]) {//假定的最小值 并不是最小的
                min = arr[j]; //重置min
                minIndex = j; //重置minIndex
            }
        }

        //将最小值，放在arr[1]
        if (minIndex !=1) {
            arr[minIndex] = arr[1];
            arr[1] = min;
        }

        System.out.println("第二轮后");
        System.out.println(Arrays.toString(arr));


        //第2轮

        minIndex = 2;//最小值下标 一开始假定0
        min = arr[2];//最小值  一开始假定0
        for (int j =2 + 1; j< arr.length;j++) {
            if (min > arr[j]) {//假定的最小值 并不是最小的
                min = arr[j]; //重置min
                minIndex = j; //重置minIndex
            }
        }

        //将最小值，放在arr[2]
        if (minIndex !=2) {
            arr[minIndex] = arr[2];
            arr[2] = min;
        }

        System.out.println("第三轮后");
        System.out.println(Arrays.toString(arr));
    }


    public static void selectSort2(int[] arr) {
        //原始数组 101,34,119,1
        //第一轮 1,34,119,101

        int minIndex = 0;//最小值下标 一开始假定0
        int min = arr[0];//最小值  一开始假定0

        for (int i = 0; i< arr.length -1;i++)  {
            for (int j =minIndex + 1; j< arr.length;j++) {
                if (min > arr[j]) {//假定的最小值 并不是最小的
                    min = arr[j]; //重置min
                    minIndex = j; //重置minIndex
                }
            }

            //将最小值，放在arr[0]
            arr[minIndex] = arr[i];
            arr[i] = min;

            minIndex = i+1;
            min = arr[i+1];
        }



        System.out.println("第一轮后");
        System.out.println(Arrays.toString(arr));
    }
}
