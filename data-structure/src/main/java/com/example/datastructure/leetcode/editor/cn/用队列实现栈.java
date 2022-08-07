package com.example.datastructure.leetcode.editor.cn;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class 用队列实现栈{
	public static void main(String[] args) {
        MyStack solution = new 用队列实现栈().new MyStack();
		
	}


//leetcode submit region begin(Prohibit modification and deletion)
class MyStack {

	private List<Integer> list;

    public MyStack() {
        list = new LinkedList<>();
    }

    public void push(int x) {
        list.add(x);
    }

    public int pop() {
        Integer integer = list.get(list.size() - 1);

        list = list.subList(0, list.size() - 1);
        return integer;
    }

    public int top() {
        return list.get(list.size() - 1);
    }

    public boolean empty() {
        return list.isEmpty();
    }
}

/**
 * Your MyStack object will be instantiated and called as such:
 * MyStack obj = new MyStack();
 * obj.push(x);
 * int param_2 = obj.pop();
 * int param_3 = obj.top();
 * boolean param_4 = obj.empty();
 */
//leetcode submit region end(Prohibit modification and deletion)

}
