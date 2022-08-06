package com.example.datastructure.leetcode.editor.cn;


public class 反转链表{
	public static void main(String[] args) {
		Solution solution = new 反转链表().new Solution();

		solution.reverseList(new ListNode(1, new ListNode(2, new ListNode(3, null))));
		
	}
//leetcode submit region begin(Prohibit modification and deletion)
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution {
    public ListNode reverseList(ListNode head) {
		ListNode cur = head, pre = null;
		while (cur!= null) {
			ListNode temp = cur.next;
			cur.next = pre;
			pre = cur;
			cur = temp;

		}
		return pre;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}
