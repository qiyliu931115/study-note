package com.example.datastructure.base.recursion;

public class Queen8 {

    //max表示共有多少个皇后
    int max = 8;
    //定义数组array 保存皇后放置位置的结果，比如 arr={0.4.7.5.2.6.1.3}
    int[] array = new int[max];
    static int count = 0;
    static int judgeCount = 0;

    public static void main(String[] args) {
        Queen8 queen8 = new Queen8();
        queen8.check(0);
        System.out.printf("一共有%d解法", count);
        System.out.println();
        System.out.printf("一共判断冲突%d次", judgeCount);
    }

    //编写一个方法 放置第N个皇后
    //特别注意 check 每一次递归 进入到check中都有一套for 循环 for (int i = 0; i < max; i++) 因此会有回溯
    private  void check (int n) {
        if (n == max) { //n = 8
            print();
            return;
        }
        //依次放入皇后，并判断是否冲突
        for (int i = 0; i < max; i++) {
            //先把 这个皇后n  放到该行的第一列
            array[n] = i;
            //判断当放置第N个皇后到i列 是否冲突
            if (judge(n)) { //不冲突
                //接着放N+1个皇后，即开始递归
                check(n +1);
            }
            //如果冲突 就继续指向 array[n] = i,即将第N个皇后放置在本行 后移动一个位置


        }
    }

    //查看当我们放置第N个皇后是否和前面已经摆放的皇后冲突 judge(法官; 审判员; 裁判员; 评判员; 鉴定人; 鉴赏家;
    //v.	判断; 断定; 认为; 估计，猜测(大小、数量等); 裁判; 评判; 担任裁判; 评价; 审判)
    //Math.abs absolute 绝对值
    /**
     *
     * @param n 表示第N个皇后
     * @return
     */
    private boolean judge (int n) {
        judgeCount++;
        for (int i = 0; i < n ;i++) {
            //同一列 斜线
            //  array[i] == array[n] 表示判断 第N个皇后是否和前面的N-1个皇后在同一列  行是横着的，列是竖着的
            //  Math.abs(n-i) == Math.abs(array[n] - array[i]) 表示判断 第N个皇后是否和第i皇后在同一斜线
            // n=1 放在第二列  array[1] = 1
            // Math.abs(1-0) = 1
            // Math.abs(array[n] - array[i]) =  Math.abs(1 - 0) = 1
            // 行间距等于列间距 表示在同一个斜线上
            // 下标相减的值 = 下标中数据的值相减 说明在同一个斜线上
            if (array[i] == array[n] || Math.abs(n-i) == Math.abs(array[n] - array[i])) {
                return false;
            }
        }
        return true;
    }

    //写一个方法 将Queen摆放的位置打印出来
    private void  print () {
        count++;
        for (int i = 0; i < array.length;i++) {
            System.out.print(array[i] + " ");
        }
        System.out.println();
    }
}


