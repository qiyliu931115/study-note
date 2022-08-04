package com.example.datastructure.base.queue;

import java.util.Scanner;

public class CircleArrayQueueDemo {

    public static void main(String[] args) {
        CircleArrayQueue arrayQueue = new CircleArrayQueue(4); //有效数据是3


        Scanner scanner = new Scanner(System.in);
        char key = ' ';

        boolean loop = true;

        while (loop) {
            System.out.println("s:显示队列");
            System.out.println("e:退出程序");
            System.out.println("a:添加数据到队列");
            System.out.println("g:获取数据");
            System.out.println("h:查看队列头的数据");
            key = scanner.next().charAt(0);
            switch (key) {
                case 's':
                    arrayQueue.showQueue();
                    break;
                case 'a':
                    System.out.println("输入一个数");
                    arrayQueue.add(scanner.nextInt());
                    break;
                case 'g':
                    try {
                        System.out.printf("取出的数据是%d", arrayQueue.get());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 'h':

                    try {
                        System.out.printf("队列头的数据是%d\na", arrayQueue.peek());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    break;
                case 'e':
                    scanner.close();
                    loop = false;
                    break;
                default:
                    break;

            }
        }
        System.out.println("exits");
    }

}

class CircleArrayQueue {

    private int maxSize;

    private int front; //指向第一个数据下标 初始0

    private int rear;   //指向最后一个数据的后一位的下标 初始0

    private int[] data;

    public CircleArrayQueue(int arrMaxSize) {
        maxSize = arrMaxSize;
        data = new int[maxSize];
    }

    public boolean isFull() {
        return (rear + 1) % maxSize == front;
    }

    public boolean isEmpty() {
        return rear == front; //初始都是0
    }

    public void add(int n) {
        if (isFull()) {
            System.out.println("队列已满!");
            return;
        }

        data[rear] = n;
        rear = (rear + 1) % maxSize;
    }

    public int get() {
        if (isEmpty()) {
            throw new RuntimeException("队列没数据");
        }

        int temp = data[front];
        front = (front + 1) % maxSize; //如果是加1 一直加会数组越界
        return temp;
    }

    public void showQueue() {
        if (isEmpty()) {
            System.out.println("队列没数据");
            return;
        }
        //从front开始遍历
        for (int i = front; i < front + size(); i++) {
            System.out.printf("dada[%d]=%d\n", i % maxSize, data[i % maxSize]); // i 有可能超过数组长度
        }
    }

    //求出当前队列有效数据个数
    private int size() {
        // rear = 1 front =0 maxSize =3   1+3-0 = 4     4 % 3 = 1
        // rear = 2 front =0 maxSize =3  2+3-0 = 5      5 % 3 = 2
        return (rear + maxSize - front) % maxSize;
    }

    public int peek() {
        if (isEmpty()) {
            throw new RuntimeException("队列没数据");
        }
        return data[front];
    }

}