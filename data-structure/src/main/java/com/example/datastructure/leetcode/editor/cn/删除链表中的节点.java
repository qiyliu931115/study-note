package com.example.datastructure.leetcode.editor.cn;


public class 删除链表中的节点{
	public static void main(String[] args) {
		Solution solution = new 删除链表中的节点().new Solution();




		solution.deleteNode(new ListNode(4 ,new ListNode(5, new ListNode(1, new ListNode(9, null)))));
		
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
    public void deleteNode(ListNode node) {
		ListNode dummy = new ListNode(-1);

		dummy.next = node;

		ListNode cur = dummy;

		while (true) {
			if (cur.next == null) {
				return;
			} else if (cur.next.val == node.val) {
				cur.next = cur.next.next;
				return;
			}
			cur = cur.next;
		}
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}
