package com.example.datastructure.base.sort;

import java.util.Arrays;

public class InsertSort {

    public static void main(String[] args) {
        int[]  arr = {101,34,119,1};
        insertSort0(arr);
        System.out.println(Arrays.toString(arr));
    }


    //插入排序
    public static void insertSort0(int[]  arr) {

        for (int i = 1;i<arr.length; i++) {
            //定义待插入
            int insertValue = arr[i];
            int insertIndex = i -1; //即arr[1]的前面这个歌数的下标

            //给insertValue找到插入的位置

            // 1    insertIndex >=0 保证给 insertIndex找插入位置 不越界
            // 2    insertValue < arr[insertIndex] 待插入的数据 还没有找到插入位置
            // 3    就需要将arr[insertIndex]往后移动
            while (insertIndex >=0 && insertValue < arr[insertIndex]) {
                arr[insertIndex + 1] = arr[insertIndex];
                insertIndex--;
            }
            //当退出while循环时 说明插入位置找到 insertIndex+1

            arr[insertIndex + 1] = insertValue;
        }



    }

    //插入排序
    public static void insertSort(int[]  arr) {
        //第一轮 {101,34,119,1} -> {34,101,119,1}

        //定义待插入
        int insertValue = arr[1];
        int insertIndex = 1 -1; //即arr[1]的前面这个歌数的下标

        //给insertValue找到插入的位置

        // 1    insertIndex >=0 保证给 insertIndex找插入位置 不越界
        // 2    insertValue < arr[insertIndex] 待插入的数据 还没有找到插入位置
        // 3    就需要将arr[insertIndex]往后移动
        while (insertIndex >=0 && insertValue < arr[insertIndex]) {
            arr[insertIndex + 1] = arr[insertIndex];
            insertIndex--;
        }
        //当退出while循环时 说明插入位置找到 insertIndex+1

        arr[insertIndex + 1] = insertValue;

        System.out.println("第一轮插入");
        System.out.println(Arrays.toString(arr));

        //第二轮
        insertValue = arr[2];
        insertIndex = 2 - 1; //即arr[1]的前面这个歌数的下标
        while (insertIndex >=0 && insertValue < arr[insertIndex]) {
            arr[insertIndex + 1] = arr[insertIndex];
            insertIndex--;
        }

        arr[insertIndex + 1] = insertValue;

        System.out.println("第二轮插入");
        System.out.println(Arrays.toString(arr));

        //第三轮
        insertValue = arr[3];
        insertIndex = 3 - 1; //即arr[1]的前面这个歌数的下标
        while (insertIndex >=0 && insertValue < arr[insertIndex]) {
            arr[insertIndex + 1] = arr[insertIndex];
            insertIndex--;
        }

        arr[insertIndex + 1] = insertValue;

        System.out.println("第三轮插入");
        System.out.println(Arrays.toString(arr));
    }

}
