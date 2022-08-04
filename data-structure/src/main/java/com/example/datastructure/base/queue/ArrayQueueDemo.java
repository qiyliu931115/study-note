package com.example.datastructure.base.queue;

import java.util.Scanner;

//数组模拟队列
public class ArrayQueueDemo {

    public static void main (String[] arg) {
        ArrayQueue arrayQueue = new ArrayQueue(3);


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
            switch (key){
                case 's':
                    arrayQueue.showQueue();
                    break;
                case 'a':
                    System.out.println("输入一个数");
                    arrayQueue.add(scanner.nextInt());
                    break;
                case 'g':
                    try{
                        System.out.printf("取出的数据是%d",arrayQueue.get());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 'h':

                    try{
                        System.out.printf("队列头的数据是%d\na",arrayQueue.peek());
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

class ArrayQueue {

    private int maxSize; //最大容量

    private int front;//头

    private int rear;//尾

    private int[] arr;


    public ArrayQueue(int maxSize) {
        this.maxSize = maxSize;
        this.arr = new int[maxSize];
        this.front = -1;    //指向队列头部 front是指向队列头的前一个位置
        this.rear = -1;     //指向队列尾 就是指向队列尾的数据
    }

    public boolean isFull() {
        return rear == maxSize - 1;
    }

    public boolean isEmpty() {
        return front == rear;
    }

    public void add(int data) {
        if (isFull()) {
            System.out.println("队列已满!");
            return;
        }
        rear++;
        arr[rear] = data;
    }

    public int get () {
        if (isEmpty()) {
            throw new RuntimeException("队列没数据");
        }
        front++;
        return arr[front];
    }

    public void showQueue () {
        if (isEmpty()) {
            System.out.println("队列没数据");
            return;
        }
        for (int i =0;i<arr.length;i++) {
            System.out.printf("arr[%d]=%d\n", i, arr[i]);
        }
    }

    public int peek() {
        if (isEmpty()) {
            throw new RuntimeException("队列没数据");
        }
        return arr[front +1];
    }

}
