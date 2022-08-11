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

        map[3][1] = 1;
        map[3][2] = 1;

        for (int i = 0;i < 8; i++) {
            for (int j = 0;j < 7; j++) {
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }



    }

    //使用递归回溯给小球找路
    //1 表示地图
    //2 i,j从哪个位置开始找
    //3 如果小球能到map[6][5]位置说明通路找到
    //4 约定 当map[i][j]为0表示该点没有走过 当为1时 表示墙 当2表示通路可以走 3 表示已经该位置已经走过 但是走不通
    //5 走迷宫时 需要确定一个策略 下-》右边-》上面-》左，如果该点走不通 再回溯
    /**
     *
     * @param map 表示地图
     * @param i     从哪个位置开始找
     * @param j
     * @return      如果找到返回true 否则返回false
     */
    public static boolean setWay(int[][] map, int i, int j) {
        if (map[6][5] == 2) { //通路已经找到
            return true;
        } else {
            if (map[i][j] == 0) {//如果当前这个点没走过
                //按策略 下-》右边-》上面-》左
                map[i][j] = 2; //假定该点可以走通的
            }
        }
    }

}
