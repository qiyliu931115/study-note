package src.main.java.com.example.datastructure.base.search;

public class SeqSearch {

    public static void main(String[] args) {
        int arr[] = {1,9,11,-1,34,89};//无序数组

    }
    public static int seqSearch(int[] arr, int value) {
        //线性查找是逐一对比
        for (int i = 0;i<arr.length;i++) {
            if (arr[i] == value) {
                return i;
            }
        }
        return -1;
    }
}
