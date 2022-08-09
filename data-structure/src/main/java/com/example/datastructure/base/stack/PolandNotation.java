package com.example.datastructure.base.stack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * 逆波兰表达式
 */
public class PolandNotation {

    public static void main(String[] args) {

        // (3+4)*5-6 =》3 4 + 5 * 6 +
        //为了方便 逆波兰表达式 数字 符号 空格 隔开
        String  suffixExpression = "3 4 + 5 * 6 -";
        //思路
        // 1 先将 suffix expression 放入ArrayList中
        // 2 将 ArrayList传递给一个方法 遍历 ArrayList 配合栈完成计算
        List<String> listString = getListString(suffixExpression);
        System.out.println("rpnList=" + listString);

        int calculate = calculate(listString);
        System.out.println(calculate);
    }

    //将一个逆Poland expression 一次将数据 和 运算符 放入 ArrayList

    public static List<String> getListString(String suffixExpression) {

        String[] split = suffixExpression.split(" ");
        List<String> list = new ArrayList<String>();
        for (String element : split) {
            list.add(element);
        }
        return list;
    }

    //完成对 逆 Poland expression 的运算
    public static int calculate(List<String> ls) {
        //创建栈 只需要一个栈即可
        Stack<String> stack = new Stack<String>();
        //遍历 如果是数 入栈
        for (String item:ls) {
            if (item.matches("\\d+")) {//匹配的是多位数
                stack.push(item);
            } else {
                //如果不是数字 是运算符 弹出两个数 进行运算 然后入栈
                //次顶在前 栈顶在后
                int num2 = Integer.parseInt(stack.pop());
                int num1 = Integer.parseInt(stack.pop());
                int res = 0;
                if (item.equals("+")) {
                    res = num1 + num2;
                } else if (item.equals("-")) {
                    res = num1 - num2;
                } else if (item.equals("*")) {
                    res = num1 * num2;
                } else if (item.equals("/")) {
                    res = num1 / num2;
                } else {
                    throw new RuntimeException("运算符有误");
                }
                //把结果入栈
                stack.push(res + "");
            }
        }
        //最后留在stack中的数据就是运算结果
        return Integer.parseInt(stack.pop());
    }
}
