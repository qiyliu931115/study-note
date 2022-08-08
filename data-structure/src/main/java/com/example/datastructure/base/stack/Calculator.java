package com.example.datastructure.base.stack;

public class Calculator {

    public static void main(String[] args) {

        String expression = "7+2*6-4";

        ArrayStack2 numStack = new ArrayStack2(10);
        ArrayStack2 operStack = new ArrayStack2(10);

        int index = 0; //用于扫描
        int num1 = 0;
        int num2 = 0;
        int oper = 0;
        int res = 0;
        char ch = ' '; //每次扫描得到的char保存到ch
        //循环扫描expression
        while (true) {
            //依次扫描expression 每一个字符
            ch = expression.substring(index, index +1).charAt(0);
            //判断数字还是字符
            if (operStack.isOper(ch)) {
                //符号栈为空 直接入栈
                if (operStack.isEmpty()) {
                    operStack.push(ch);
                } else  {
                    //当前的ch优先级小于等于栈中的操作符
                    if (operStack.priority(ch) <= operStack.priority(operStack.peek())) {
                        //操作数栈弹出2个数
                        num1 = numStack.pop();
                        num2 = numStack.pop();
                        //操作符号栈弹出一个数
                        oper = operStack.pop();
                        //运算
                        res = numStack.cal(num1,num2,oper);
                        //把运算结果入数栈
                        numStack.push(res);
                        //ch入栈
                        operStack.push(ch);

                    } else {
                        //当前的ch优先级大于栈中的操作符 ch直接入栈
                        operStack.push(ch);
                    }
                }
            } else {
                //是数字
                numStack.push(ch - 48); //'1' => 1
            }
            //让index+1 并判断是否扫描到expression最后
            index++;
            if (index >= expression.length()) {
                break;
            }
        }

        //当表达式扫描完毕 顺序从 数栈和符号栈 中pop出相应的数和符号 并运行
        while (true) {
            //如果符号栈为空 则计算结束
            if (operStack.isEmpty()) {
                break;
            } else {
                num1 = numStack.pop();
                num2 = numStack.pop();
                oper = operStack.pop();
                res = operStack.cal(num1,num2,oper);
                numStack.push(res); //结果入数栈
            }
        }
        //将数栈最后的数 弹出 就是结果
        System.out.printf("表达式 %s = %d\n", expression , numStack.pop());

    }
}


class ArrayStack2 {

    private int maxSize; //栈深度

    private int[] stack;    // 数组模拟栈

    private int top = -1; //栈顶

    private int bottom = -1;   //栈底

    public ArrayStack2(int maxSize) {
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

    //返回运算符的优先级， 优先级使用数字 数字越大 优先级越高
    public int priority(int oper) {
        if (oper == '*' || oper == '/') {
            return 1;
        } else if (oper == '+' || oper == '-') {
            return 0;
        } else {
            return -1;
        }
    }

    //查看栈顶的数据
    public int peek() {
        return stack[top];
    }


    //判断是否运算符
    public boolean isOper (char val) {
        return val == '*' || val == '/' || val == '+' || val == '-';
    }

    //计算方法
    public int cal (int num1, int num2, int oper) {
        int res = 0; //计算结果
        switch (oper) {
            case '+':
                res = num1 + num2;
                break;
            case '-':
                res = num2 - num1;
                break;
            case '*':
                res = num1 * num2;
                break;
            case '/':
                res = num2 / num1;
                break;
        }
        return res;
    }

}
