package com.example.datastructure.base.stack;


import java.util.Scanner;

public class ArrayStackDemo {

    public static void main(String[] args) {
        ArrayStack arrayStack = new ArrayStack(4);
        String key = "";
        boolean loop = true;
        Scanner scanner = new Scanner(System.in);

        while (loop) {
            System.out.println("show 显示栈");
            System.out.println("exit 退出程序");
            System.out.println("push 添加数据到栈(入栈)");
            System.out.println("pop 从栈取出数据(出栈)");
            System.out.println("输入你的选择");
            key = scanner.next();
            switch (key) {
                case "show":
                    arrayStack.traverse();
                    break;
                case "exit":
                    scanner.close();
                    loop = false;
                    System.out.println("程序退出");
                    break;
                case "push":
                    arrayStack.push(scanner.nextInt());
                    break;
                case "pop":
                    try {
                        int pop = arrayStack.pop();
                        System.out.printf("出栈的数据是 %d\n", pop);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                default:
                    break;
            }
        }
    }

}

class ArrayStack {

    private int maxSize; //栈深度

    private int[] stack;    // 数组模拟栈

    private int top = -1; //栈顶

    private int bottom = -1;   //栈底

    public ArrayStack(int maxSize) {
        this.maxSize = maxSize;
        stack = new int[maxSize];
    }

    public boolean isFull() {
        return this.top == maxSize - 1;
    }

    public boolean isEmpty() {
        return this.top == - 1;
    }


    public void push(int val) {
        if (isFull()) {
            System.out.println("栈满");
            return;
        }
        top++;
        stack[top] = val;
    }

    public int pop() {
        if (isEmpty()) {
            throw new RuntimeException("栈空 没有数据");
        }
        int i = stack[top];
        top--;
        return i;
    }

    //遍历时 需要栈顶开始显示数据
    public void traverse() {
        if (isEmpty()) {
            System.out.println("栈空 没有数据");
            return;
        }
        //从栈顶遍历
        for (int i = top;i > -1;i --) {
            System.out.printf("stack[%d]=[%d]\n", i, stack[i]);
        }
    }
}
