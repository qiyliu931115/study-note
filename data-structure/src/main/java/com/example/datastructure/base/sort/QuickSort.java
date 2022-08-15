package src.main.java.com.example.datastructure.base.sort;

import java.util.Arrays;

public class QuickSort {

    public static void main(String[] args) {
        int[] arr = {-9,78,0,23,-567,70};
        quickSort(arr, 0, arr.length-1);
        System.out.println(Arrays.toString(arr));
    }

    public static void quickSort(int[] arr, int left, int right) {
        int l = left;
        int r = right;
        //中轴
        int mid = arr [(left + right) /2];
        //while循环的目的 是让比mid小的放到左边  比 mid大的放到右边
        while (l < r) {
            //在mid左边一直找 ，找到一个大于等于mid的值
            while (arr[l] < mid) {
                l += 1;
            }
            //在mid右边边一直找 ，找到一个小于mid的值
            while (arr[r] > mid) {
                r -= 1;
            }
            //如果l >= r 说明 mid 左边都是小于等于mid的数 右边全是大于等于mid的值
            if (l >= r) {
                break; //break结束整个循环
            }
            //交互左右的值
            int temp = arr[l];
            arr[l] = arr[r];
            arr[r] = temp;

            //如果交换完后，发现arr[l] == mid, 那么r前移一格
            if (arr[l] == mid) {
                r-= 1;
            }
            //如果交换完后，发现arr[r] == mid, 那么l后移一格
            if (arr[r] == mid) {
                l+= 1;
            }

        }
        //如果 l==r 必须l++,r-- 否则出现栈溢出
        if (l == r) {
            l++;
            r--;
        }

        //向左递归
        if ( left < r) {
            quickSort(arr,left,r);
        }

        //向左递归
        if ( right > l) {
            quickSort(arr,l,right);
        }
    }
}
