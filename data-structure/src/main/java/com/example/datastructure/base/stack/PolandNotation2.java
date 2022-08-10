package com.example.datastructure.base.stack;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 逆波兰表达式 后缀表达式
 */
public class PolandNotation2 {

    public static void main(String[] args) {

        //完成中缀转后缀表达式

        //1+((2+3)*4)-5 转成 1 2 3 + 4 *  + 5 -

        //1+((2+3)*4)-5  转成list  1, +, (, (, 2, +, 3, ), *, 4, ), -, 5

        //将中缀表达式这个list 转成 后缀表达式对应的list 即 1, +, (, (, 2, +, 3, ), *, 4, ), -, 5 转成 1, 2, 3, +, 4, *, +, 5, -

        String expression = "1+((2+3)*4)-5";
        List<String> infixExpression = toInfixExpression(expression);
        System.out.println("中缀表达式list:" + infixExpression); //[1, +, (, (, 2, +, 3, ), *, 4, ), -, 5]
        //
        List<String> parseSuffixExpressionList = parseSuffixExpressionList(infixExpression);
        System.out.println("后缀表达式list:" + parseSuffixExpressionList); //[1, 2, 3, +, 4, *, +, 5, -]

        System.out.printf("expression=%d",calculate(parseSuffixExpressionList));


//        // (30+4)*5-6 =》30 4 + 5 * 6 +
//        // 4 * 5 - 8 + 60  + 8 / 2 => 4 5 * 8 - 60 + 8 2 / +
//        //为了方便 逆波兰表达式 数字 符号 空格 隔开
//        String  suffixExpression = "4 5 * 8 - 60 + 8 2 / +";
//        //思路
//        // 1 先将 suffix expression 放入ArrayList中
//        // 2 将 ArrayList传递给一个方法 遍历 ArrayList 配合栈完成计算
//        List<String> listString = getListString(suffixExpression);
//        System.out.println("rpnList=" + listString);
//
//        int calculate = calculate(listString);
//        System.out.println(calculate);
    }

    //将中缀表达式list 转成后缀表达式list
    public static List<String> parseSuffixExpressionList(List<String > ls) {

        //定义两个栈
        Stack<String> s1 = new Stack<>();//符号栈
        // 因为s2 整个转换过程 没有pop操作 后面还需要逆序输出 直接使用List<String> s2;
        // Stack<String> s2 = new Stack<>();//中间结果栈

        List<String> s2 = new ArrayList<>();//中间结果栈

        //遍历ls

        for (String item :ls) {
            //如果是一个数 加入S2
            if (item.matches("\\d+")) {
                s2.add(item);

            } else if (item.equals("(")) {//如果是左括号 直接入s1栈
                s1.push(item);
            } else if (item.equals(")")) {//如果是右括号 则依次弹出s1栈顶的运算符，并压入s2 直到遇到左括号为止，此时将这一对括号丢弃

                while (!s1.peek().equals("(")) {
                    s2.add(s1.pop());
                }

                s1.pop(); // 将 左括号 弹出s1栈， 消除小括号

            } else {
                //当item元素的优先级 小于等于 s1栈顶运算符
                //将s1栈顶的运算符弹出并加入到s2

                //比较优先级高低的方法
                while (s1.size() != 0 && Operation.getValue(s1.peek()) >= Operation.getValue(item)) {
                    s2.add(s1.pop());
                }
                // 还需要将item压入s1栈
                s1.push(item);
            }

        }
        //将s1剩余的运算符一次弹出并加入s2
        while (s1.size() != 0) {
            s2.add(s1.pop());
        }
        return s2;//因为是存放到一个list 因此按顺序输出 就是对应的后缀表达式的list
    }

    //将中缀表达式转成对应的list
    public static List<String> toInfixExpression(String s) {
        //定义一个list 存放中缀表达式 内容
        List<String> ls = new ArrayList<>();
        int i = 0;
        String str;//对多位数的拼接
        char c;//每遍历一个字符 放入c
        do {
            //如果C是一个非数字 直接加到ls
            if ((c = s.charAt(i)) < 48 || (c = s.charAt(i)) > 57) {
                ls.add("" + c);
                i++;

            } else {
                //如果是数字 需要考虑多位数的问题
                str = "";//先将str设置为""
                while (i < s.length() && (c = s.charAt(i)) >= 48 && (c = s.charAt(i)) <= 57) {
                    str = str + c;//拼接
                    i++;
                }
                ls.add(str);
            }
        }while (i < s.length());
        return ls;//返回
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

//编写一个类 operation 可以返回一个运算符对应的优先级
class Operation {

    private static int ADD = 1;
    private static int SUB = 1;
    private static int MUL = 2;
    private static int DIV = 2;

    //写一个方法 返回对应的优先级数字
    public static int  getValue (String operation) {
        int result = 0;
        switch (operation) {
            case "+":
                result =  ADD;
                break;
            case "-":
                result =  SUB;
                break;
            case "*":
                result =  MUL;
                break;
            case "/":
                result =  DIV;
                break;
            default:
                System.out.println("不存在的运算符");
        }
        return result;
    }
}
