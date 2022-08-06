package com.example.datastructure.leetcode.editor.cn;


public class 合并两个排序的链表{
	public static void main(String[] args) {
		Solution solution = new 合并两个排序的链表().new Solution();
//
//		solution.mergeTwoLists(new ListNode(1, new ListNode(2,new ListNode(4,null))),
//				new ListNode(1, new ListNode(3,new ListNode(4,null))));

		solution.mergeTwoLists(new ListNode(1, new ListNode(2, null)),
				(new ListNode(3, new ListNode(4, null))));
		
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
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
		ListNode merge  = new ListNode(-1);

		ListNode temp1 = l1, temp2 = l2;

		ListNode cur = merge;

		while (temp1 != null && temp2 != null) {

			if (temp1.val < temp2.val) {
				cur.next = temp1;
				temp1 = temp1.next;
			} else {
				cur.next = temp2;
				temp2 = temp2.next;
			}
			cur = cur.next;
		}

		if (temp1 == null) {
			cur.next = temp2;
		}
		if (temp2 == null) {
			cur.next = temp1;
		}
		return merge.next;
	}

}
//leetcode submit region end(Prohibit modification and deletion)

}
