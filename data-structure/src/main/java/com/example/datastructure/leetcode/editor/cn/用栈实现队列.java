package com.example.datastructure.leetcode.editor.cn;


import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

public class 用栈实现队列{
	public static void main(String[] args) {
		Solution solution = new 用栈实现队列().new Solution();
		
	}

    class Solution {

    }


    //leetcode submit region begin(Prohibit modification and deletion)
class MyQueue {

        private int front;//用了 front 变量来存储队首元素

	    private Stack<Integer> in, out;

    public MyQueue() {
        in = new Stack<>();
        out = new Stack();
    }
    
    public void push(int x) {
        if (in.empty()) {
            front = x;
        }
        in.push(x);
    }
    
    public int pop() {
        if (out.isEmpty()) { //out是空的
            while (!in.isEmpty()) { //如果 in不等于空
                out.push(in.pop()); // in=3,2,1  out=1,2,3
            }
        }
        return out.pop(); //把1推出去
    }
    
    public int peek() {
        if (!out.isEmpty()) {
            return out.peek();
        }
        return front;
    }
    
    public boolean empty() {
        return in.isEmpty() && out.isEmpty();
    }
}

/**
 * Your MyQueue object will be instantiated and called as such:
 * MyQueue obj = new MyQueue();
 * obj.push(x);
 * int param_2 = obj.pop();
 * int param_3 = obj.peek();
 * boolean param_4 = obj.empty();
 */
//leetcode submit region end(Prohibit modification and deletion)

}
