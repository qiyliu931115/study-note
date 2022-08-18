package src.main.java.com.example.datastructure.base.search;

public class InsertValueSearch {

    public static void main(String[] args) {
        int[] arr = new int[100];
        for (int i = 1; i <= 100 ; i++) {
            arr[i-1] = i;
        }

        System.out.println(insertValueSearch(arr, 0, arr.length-1, 100));
    }

    public static int insertValueSearch(int[] arr, int left, int right, int value) {
        // value > arr[arr.length-1] 和 value < arr[0]  必须要 否则 mid 可能越界 因为value参与了mid的计算
        if (left > right || value > arr[arr.length-1] || value < arr[0]) {
            return -1;
        }
        int mid = left + (right - left) * (value - arr[left]) / (arr[right] - arr[left]);
        int midValue = arr[mid];
        if (midValue == value) {
            return mid;
        } else if (value > midValue) {
            return insertValueSearch(arr, mid+1,right,value);
        } else {
            return insertValueSearch(arr, left,mid-1,value);
        }
    }
}


