package com.example.datastructure.base.sort;

import java.util.Arrays;

public class MergeSort {

    public static void main(String[] args) {
        int arr[] = {8,4,5,7,1,3,6,2};

        mergeSort(arr, 0, arr.length-1, new int[arr.length]);

        System.out.println("归并排序后=" + Arrays.toString(arr));
    }


    //分加合的方法
    public static void mergeSort (int [] arr, int left, int right, int[] temp) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            //左边递归分组
            mergeSort(arr, left, mid ,temp);
            //右边递归分组
            mergeSort(arr, mid + 1, right ,temp);

            //合并
            merge(arr, left, mid, right,temp );
        }
    }


    /**
     * 合并的方法
     *
     * @param arr 排序的原始数组
     * @param left  左边有序序列的初始索引
     * @param mid   中间索引
     * @param right 最右边的索引
     * @param temp  中转的数组
     */
    public static void merge (int [] arr, int left, int mid, int right, int[] temp) {

        int i = left; //左边的第一个索引

        int j =mid + 1;//右边的第一个索引

        int t = 0; //指向temp的当前索引

        //先把左右两边的数据拷贝到temp
        //直到左右两边有序序列，有一个处理完毕
        while (i <= mid && j <= right) {//两个序列都没到最后 继续循环
            if (arr[i] <= arr[j]) {
                //如果左边的有序序列的当前下标元素 小于等于右边的有序序列的当前下标里的元素
                //就把左边的放入temp
                temp[t] = arr[i];
                t++;
                i++;
            } else {
                //如果右边的有序序列的当前下标元素 小于左边的有序序列的当前下标里的元素
                //就把右边边的放入temp
                temp[t] = arr[j];
                t++;
                j++;
            }
        }

        //把另一个序列剩余的数据 全部拷贝到temp

        while (i <= mid) {//左边的有序序列还有剩余
            temp[t] = arr[i];
            t++;
            i++;
        }

        while (j <= right) {//右边边的有序序列还有剩余
            temp[t] = arr[j];
            j++;
            i++;
        }


        //把temp拷贝到arr
        t = 0;
        int tempLeft = left;

        //第一次合并 templeft是0， right是1 //第二次合并 templeft是2， right是3
        //最后一次合并时 templeft是0 right是7

        while (tempLeft <= right) {
            arr[tempLeft] = temp[t];
            t++;
            tempLeft++;
        }




    }
}
