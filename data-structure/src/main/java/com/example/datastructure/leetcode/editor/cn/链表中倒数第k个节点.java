package com.example.datastructure.leetcode.editor.cn;


public class 链表中倒数第k个节点{
	public static void main(String[] args) {
		Solution solution = new 链表中倒数第k个节点().new Solution();

		ListNode kthFromEnd = solution.getKthFromEnd(new ListNode(1, new ListNode(2, new ListNode(3, null))), 3);
		System.out.println(kthFromEnd);

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
	public ListNode getKthFromEnd(ListNode head, int k) {

		if (head.next == null && k == 1) {
			return head;
		}

		int length = getLength(head);
		ListNode temp = head;

		if (k <1 || k > length) {
			return null;
		}
		for (int i = 0;i <length - k ;i++) {

			temp = temp.next;

		}
		return temp;

	}

	private int getLength(ListNode listNode) {
		if (listNode == null) {
			return 0;
		}
		if (listNode.next == null) {
			return 1;
		}
		int length = 0;
		ListNode temp = listNode;
		while(temp != null) {
			length++;
			temp = temp.next;
		}

		return length;
	}
}
//leetcode submit region end(Prohibit modification and deletion)

}
