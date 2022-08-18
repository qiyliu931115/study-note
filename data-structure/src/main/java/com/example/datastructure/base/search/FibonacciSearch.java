package src.main.java.com.example.datastructure.base.search;

public class FibonacciSearch {

    static int maxSize = 20;

    public static void main(String[] args) {
        int[] arr = {1,8,10,89,1000,1234};
    }

    public static int[] fib () {

        int [] f = new int[maxSize];
        f[0] = 1;
        f[1] = 2;

        for (int i = 2; i < maxSize;i++) {
            f[i] = f[i -1] + f[i -2];
        }
        return f;
    }
}
