package com.example.datastructure.base.recursion;

public class MiGong {
    public static void main(String[] args) {


        //先创建二维数组 模拟迷宫
        //地图
        int[][] map = new int[8][7];
        //使用1表示墙
        //先把上下全部置为1
        for (int i = 0 ; i < 7 ;i++) {
            map[0][i] = 1;
            map[7][i] = 1;
        }

        //先把左右全部置为1
        for (int i = 0 ; i < 8 ;i++) {
            map[i][0] = 1;
            map[i][6] = 1;
        }

        for (int i = 0;i < 8; i++) {
            for (int j = 0;j < 7; j++) {
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
    }

}
