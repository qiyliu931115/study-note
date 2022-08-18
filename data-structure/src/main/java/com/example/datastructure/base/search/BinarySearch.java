package src.main.java.com.example.datastructure.base.search;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BinarySearch {

    public static void main(String[] args) {
        int[] arr = {1,8,10,89,1000,1000};


        System.out.println(binarySearch(arr, 1000));


        System.out.println(binarySearch0(arr, 0, arr.length-1, 1000));

        System.out.println(binarySearch1(arr, 0, arr.length-1, 1000));
    }

    public static ArrayList binarySearch1(int[] arr, int left, int right, int value) {
        if (left > right) {
            return null;
        }
        int mid = left + (right - left) / 2;
        int midValue = arr[mid];
        if (midValue == value) {
            //向左和向右扫描， 所有等于value的下标 放入arrayList
            ArrayList<Integer> integers = new ArrayList<>();
            int temp = mid -1;
            while (true) {
                if (temp < 0 || arr[temp] != value) {
                    break;
                }
                //temp放入集合
                integers.add(temp);
                temp -= 1;
            }

            integers.add(mid);


            temp = mid  + 1;
            while (true) {
                if (temp > arr.length -1 || arr[temp] != value) {
                    break;
                }
                //temp放入集合
                integers.add(temp);
                temp += 1;
            }

            return integers;
        } else if (midValue > value) {
            return binarySearch1(arr, left, mid - 1, value);
        } else {
            return binarySearch1(arr, mid + 1, right, value);
        }

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
