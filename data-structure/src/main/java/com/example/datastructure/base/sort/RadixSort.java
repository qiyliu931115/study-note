package com.example.datastructure.base.sort;

import java.util.Arrays;

/**、
 * 基数排序
 */
public class RadixSort {

    public static void main(String[] args) {
        int[] arr = {53,-3,542,-748,14,214};

        radixSort1(arr);

        System.out.println(Arrays.toString(arr));

    }

    /**
     * 有负数时处理思路：先判断有无负数 有则找到最小值 数组所有数据都减去该值 也就是数组最小值会变为0 接下来按正整数排序 最后数组所有数据加上原最小数 变为原有数据值
     * @param arr
     */

    public static void radixSort1(int[] arr) {
        //得到数组中最大的数的位数
        int max = arr[0];

        int min  = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max  = arr[i];
            }

            if (arr[i] < min) {
                min  = arr[i];
            }
        }

        for (int i = 0; i < arr.length; i++) {
            arr[i]  = arr[i] - min;
        }


        int maxLength = (max + "").length();


        //定义一个二维数组 表示10个桶 每个桶就是一个一维数组
        //1 二维数组包含10个以为数组
        //2 为了防止放入数的时候 数据溢出 则每个一维数组（桶） 大小定为arr.length
        // 3 明确 基数排序就是用空间算时间
        int[][] bucket = new int[10][arr.length];

        //为了记录每个桶的实际存放多少个数据（每轮不一样） 定义一个一维数组 记录各个桶每次放入的数据个数
        // bucketElementCount[0] 记录的就是bucket[0]的放入的数据的个数
        int[] bucketElementCount = new int[10];

        for ( int i = 0, n = 1; i < maxLength; i ++, n*=10) {
            //针对每个元素的对应位进行排序 第一次是个位  第二次是十位 第三次是百位++
            for (int j = 0;j<arr.length;j++) {
                //取出每个元素对应位的值
                int digitOfrWElement = arr[j] / n % 10;
                //放入对应的桶中
                bucket[digitOfrWElement][bucketElementCount[digitOfrWElement]] = arr[j];

                bucketElementCount[digitOfrWElement]++;

            }

            //按照这个桶的顺序 （一维数组的下标一次取出数据 放入原来的数组）
            int index = 0;//

            //遍历每一个桶 并将桶中的数据 放入到原数组
            for (int k = 0; k < bucketElementCount.length;k++) {
                //如果桶中有数据 才放入原数组
                if (bucketElementCount[k] != 0 ) {

                    //循环该桶 即第k个一维数组 放入
                    for (int l = 0; l < bucketElementCount[k]; l++) {
                        //取出数据放入arr
                        arr[index] = bucket[k][l];
                        index++;
                    }

                }
                //每轮处理后 将每个bucketElementCount[k] = 0 ！！！
                bucketElementCount[k] =0;
            }
        }


        for (int i = 0; i < arr.length; i++) {
            arr[i]  = arr[i] + min;
        }
    }

    public static void radixSort(int[] arr) {

        //第一轮 对元素的个位进行排序处理

        //定义一个二维数组 表示10个桶 每个桶就是一个一维数组
        //1 二维数组包含10个以为数组
        //2 为了防止放入数的时候 数据溢出 则每个一维数组（桶） 大小定为arr.length
        // 3 明确 基数排序就是用空间算时间
        int[][] bucket = new int[10][arr.length];

        //为了记录每个桶的实际存放多少个数据（每轮不一样） 定义一个一维数组 记录各个桶每次放入的数据个数
        // bucketElementCount[0] 记录的就是bucket[0]的放入的数据的个数
        int[] bucketElementCount = new int[10];

        for (int j = 0;j<arr.length;j++) {
            //取出个位的值
            int digitOfrWElement = arr[j] / 1 % 10;
            //放入对应的桶中
            bucket[digitOfrWElement][bucketElementCount[digitOfrWElement]] = arr[j];

            bucketElementCount[digitOfrWElement]++;

        }

        //按照这个桶的顺序 （一维数组的下标一次取出数据 放入原来的数组）
        int index = 0;//

        //遍历每一个桶 并将桶中的数据 放入到原数组
        for (int k = 0; k < bucketElementCount.length;k++) {
            //如果桶中有数据 才放入原数组
            if (bucketElementCount[k] != 0 ) {

                //循环该桶 即第k个一维数组 放入
                for (int l = 0; l < bucketElementCount[k]; l++) {
                    //取出数据放入arr
                    arr[index] = bucket[k][l];
                    index++;
                }

            }
            //第一轮处理后 将每个bucketElementCount[k] = 0 ！！！
            bucketElementCount[k] =0;
        }

        System.out.println("第一轮 对个位的排序处理：" + Arrays.toString(arr));


        //第二轮 对十位的排序处理


        for (int j = 0;j<arr.length;j++) {
            //取出个位的值
            int digitOfrWElement = arr[j] / 10 % 10; //748 /10 = 74 % 10 = 4
            //放入对应的桶中
            bucket[digitOfrWElement][bucketElementCount[digitOfrWElement]] = arr[j];

            bucketElementCount[digitOfrWElement]++;

        }

        //按照这个桶的顺序 （一维数组的下标一次取出数据 放入原来的数组）
        index = 0;//

        //遍历每一个桶 并将桶中的数据 放入到原数组
        for (int k = 0; k < bucketElementCount.length;k++) {
            //如果桶中有数据 才放入原数组
            if (bucketElementCount[k] != 0 ) {

                //循环该桶 即第k个一维数组 放入
                for (int l = 0; l < bucketElementCount[k]; l++) {
                    //取出数据放入arr
                    arr[index] = bucket[k][l];
                    index++;
                }

            }
            //每轮处理后 将每个bucketElementCount[k] = 0 ！！！
            bucketElementCount[k] =0;
        }

        System.out.println("第二轮 对十位的排序处理：" + Arrays.toString(arr));




        //第三轮 对百位位的排序处理


        for (int j = 0;j<arr.length;j++) {
            //取出个位的值
            int digitOfrWElement = arr[j] / 100 % 10; //748 /10 = 74 % 10 = 4
            //放入对应的桶中
            bucket[digitOfrWElement][bucketElementCount[digitOfrWElement]] = arr[j];

            bucketElementCount[digitOfrWElement]++;

        }

        //按照这个桶的顺序 （一维数组的下标一次取出数据 放入原来的数组）
        index = 0;//

        //遍历每一个桶 并将桶中的数据 放入到原数组
        for (int k = 0; k < bucketElementCount.length;k++) {
            //如果桶中有数据 才放入原数组
            if (bucketElementCount[k] != 0 ) {

                //循环该桶 即第k个一维数组 放入
                for (int l = 0; l < bucketElementCount[k]; l++) {
                    //取出数据放入arr
                    arr[index] = bucket[k][l];
                    index++;
                }

            }
            //每轮处理后 将每个bucketElementCount[k] = 0 ！！！
            bucketElementCount[k] =0;
        }

        System.out.println("第三轮 对百位的排序处理：" + Arrays.toString(arr));

    }

}
