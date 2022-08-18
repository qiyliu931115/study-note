package src.main.java.com.example.datastructure.base.search;

public class BinarySearch {

    public static void main(String[] args) {
        int[] arr = {1,8,10,89,1000,1234};


        System.out.println(binarySearch(arr, 10));
    }


    public static int binarySearch(int[] arr, int value) {

        int left = 0, right = arr.length-1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] == value) {
                return mid;
            } else if (arr[mid] > value) {
                right = mid - 1;
            } else if (arr[mid] < value) {
                left = mid + 1;
            }
        }

        return -1;
    }
}
