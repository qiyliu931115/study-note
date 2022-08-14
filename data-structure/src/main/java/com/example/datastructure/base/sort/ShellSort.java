package com.example.datastructure.base.sort;

import java.util.Arrays;

public class ShellSort {

    public static void main(String[] args) {
        int[] arr = {8,9,1,7,2,3,5,4,6,0};
        shellSort0(arr);

        System.out.println(Arrays.toString(arr));


    }

    public static void shellSort0(int[] arr) {

        for (int gap = arr.length/2; gap > 0 ; gap = gap / 2) {
            for (int i = gap; i< arr.length; i++) {
                for (int j = i  - gap; j>= 0; j = j - gap) {
                    //如果当前元素大于加上步长后的那个元素 说明交换
                    if (arr[j] > arr[j+gap]) {
                        int temp = arr[j];
                        arr[j] = arr[j+gap];
                        arr[j + gap] = temp;
                    }
                }
            }
        }


    }

    public static void shellSort(int[] arr) {

        //希尔排序的第一轮排序
        //因为是第一轮 将10个数据分成了5组
        for (int i = 5; i< arr.length; i++) {
            //遍历各组中所有的元素 （共有5组，每组有两个元素）步长5
            for (int j = i  - 5; j>= 0; j = j - 5) {
                //如果当前元素大于加上步长后的那个元素 说明交换
                if (arr[j] > arr[j+5]) {
                    int temp = arr[j];
                    arr[j] = arr[j+5];
                    arr[j + 5] = temp;
                }
            }
        }

        System.out.println("第一轮排序" + Arrays.toString(arr));


        //希尔排序的第二轮排序
        //因为是第2轮 将10个数据分成了5/2组
        for (int i = 2; i< arr.length; i++) {
            //遍历各组中所有的元素 （共有5组，每组有两个元素）步长5
            for (int j = i  - 2; j>= 0; j = j - 2) {
                //如果当前元素大于加上步长后的那个元素 说明交换
                if (arr[j] > arr[j+2]) {
                    int temp = arr[j];
                    arr[j] = arr[j+2];
                    arr[j + 2] = temp;
                }
            }
        }

        System.out.println("第二轮排序" + Arrays.toString(arr));


        //希尔排序的第三轮排序
        //因为是第3轮 将10个数据分成了5/2/2组
        for (int i = 1; i< arr.length; i++) {
            //遍历各组中所有的元素 （共有5组，每组有两个元素）步长5
            for (int j = i  - 1; j>= 0; j = j - 1) {
                //如果当前元素大于加上步长后的那个元素 说明交换
                if (arr[j] > arr[j+1]) {
                    int temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j + 1] = temp;
                }
            }
        }

        System.out.println("第三轮排序" + Arrays.toString(arr));
    }
}
