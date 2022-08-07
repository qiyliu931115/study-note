package com.example.datastructure.leetcode.editor.cn;


import java.util.ArrayList;
import java.util.List;

public class 回文链表{
	public static void main(String[] args) {
		Solution solution = new 回文链表().new Solution();
		boolean palindrome = solution.isPalindrome(new ListNode(1, new ListNode(1, new ListNode(2, new ListNode(1, null)))));

		System.out.println(palindrome);
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
    public boolean isPalindrome(ListNode head) {
    	if (head == null || head.next == null) {
    		return false;
		}

		List<Integer> list = new ArrayList<>();

		while (head != null) {
			list.add(head.val);
			head = head.next;
		}

		int l = 0;
		int r = list.size() -1;
		while (l < r) {
			if (!list.get(l).equals(list.get(r))) {
				return false;
			}
			l++;
			r--;
		}
		return true;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}
