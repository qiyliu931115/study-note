package src.main.java.com.example.datastructure.base.search;

public class FibonacciSearch {

    static int maxSize = 20;

    public static void main(String[] args) {
        int[] arr = {1,8,10,89,1000,1234};
    }

    /**
     * 得到斐波那契数列  mid = low + F(k-1)-1   F就是fib()方法返回值
     * @return
     */
    public static int[] fib () {

        int [] f = new int[maxSize];
        f[0] = 1;
        f[1] = 2;

        for (int i = 2; i < maxSize;i++) {
            f[i] = f[i -1] + f[i -2];
        }
        return f;
    }



    public static int fibonacciSearch(int [] a, int key) {
        int low =0, high = a.length-1;
        int k = 0;//表示斐波那契数列分割数值下标
    }
}
