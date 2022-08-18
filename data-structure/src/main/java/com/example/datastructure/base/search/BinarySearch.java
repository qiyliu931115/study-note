package src.main.java.com.example.datastructure.base.search;

public class BinarySearch {

    public static void main(String[] args) {
        int[] arr = {1,8,10,89,1000,1234};


        System.out.println(binarySearch(arr, 231311));


        System.out.println(binarySearch0(arr, 0, arr.length-1, 231311));
    }

    public static int binarySearch0(int[] arr, int left, int right, int value) {
        if (left > right) {
            return -1;
        }
        int mid = left + (right - left) / 2;
        int midValue = arr[mid];
        if (midValue == value) {
            return mid;
        } else if (midValue > value) {
            return binarySearch0(arr, left, mid - 1, value);
        } else {
            return binarySearch0(arr, mid + 1, right, value);
        }

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
