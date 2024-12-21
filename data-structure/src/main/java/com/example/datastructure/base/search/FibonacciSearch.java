package com.example.datastructure.base.search;

import java.util.Arrays;

public class FibonacciSearch {

    static int maxSize = 20;

    public static void main(String[] args) {
        int[] arr = {1,8,10,89,1000,1234};
        System.out.println(fibonacciSearch(arr,1234));
    }

    /**
     * 得到斐波那契数列  mid = low + F(k-1)-1   F就是fib()方法返回值
     * @return
     */
    public static int[] fib () {

        int [] f = new int[maxSize];
        f[0] = 1;
        f[1] = 1;

        for (int i = 2; i < maxSize;i++) {
            f[i] = f[i -1] + f[i -2];
        }
        return f;
    }


    /**
     *
     * @param a 数组
     * @param key   要查找的关键码
     * @return
     */
    public static int fibonacciSearch(int [] a, int key) {
        int low =0;
        int high = a.length-1;
        int k = 0;//表示斐波那契数列分割数值下标
        int mid = 0;//存放mid值
        int[] f = fib();
        //获取斐波那契分割数值的下标
        while (high > f[k] - 1) {
            k++;
        }
        //因为f[k]值 可能大于a的长度 因此我们需要使用arrays类 构造一个新的数组 并指向a[]
        //不足的部分使用0填充
        int[] temp = Arrays.copyOf(a,f[k]);
        //实际上 需要使用a数组最后的数填充temp

        // temp = {1,8,10,89,1000,1234,0,0} => {1,8,10,89,1000,1234,1234,1234}
        for (int i = high +1;i<temp.length;i++) {
            temp[i] = a[high];
        }

        //使用while循环处理，找到我们的数key
        while (low <= high) { //只要这个条件满足就可以一直找
            mid = low + f[k-1] -1;

            if (key < temp[mid]) { //说明应该继续往左边查找
                high = mid-1;
                //为什么是k--
                //全部元素 = 前面的元素 + 后面的元素
                // f[k] = f[k-1] + f[k-2]
                // 因为前面有f[k-1]个元素，所以可以继续拆分 f[k-1] = f[k-2] + f[k-3]
                //即 在f[k-1]的前面继续查找 k--
                //即下次循环 mid= f[k-1-1]-1
                k--;
            } else if (key > temp[mid]) {//往右边查找
                low = mid + 1;
                //全部元素 = 前面的元素 + 后面的元素
                // f[k] = f[k-1] + f[k-2]
                // 后面我们还有f[k-2]个元素 所以可以继续拆分 f[k-2] = f[k-3] + f[k-4]
                //即 在f[k-2]的前面查找k -=2
                //即下次循环的mid= f[k-1-2]-1
                k = k-2;
            } else {
                //需要确定返回哪个下标
                if (mid <= high) {
                    return mid;
                } else {
                    return high;
                }
            }
        }

        return -1;
    }
}
