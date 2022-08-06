package com.example.datastructure.leetcode.editor.cn;


import java.util.Stack;

public class 从尾到头打印链表{
	public static void main(String[] args) {
		Solution solution = new 从尾到头打印链表().new Solution();

		System.out.println(solution.reversePrint(new ListNode(1,new ListNode(2, new ListNode(3 , null)))));
		
	}
//leetcode submit region begin(Prohibit modification and deletion)
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) { val = x; }
 * }
 */
class Solution {
    public int[] reversePrint(ListNode head) {
    	if (head == null) {
    		return new int[]{};
		}
    	if (head.next == null) {
    		return new int[]{head.val};
		}

		Stack<ListNode> stack = new Stack<>();
    	while (head != null) {
    		stack.push(head);
    		head = head.next;
		}

    	int [] arr = new int[stack.size()];
		int size = stack.size();
		for (int i = 0; i < size;i++) {
    		arr[i] = stack.pop().val;
		}

    	return arr;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}
