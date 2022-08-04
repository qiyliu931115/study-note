package com.example.datastructure.leetcode.editor.cn;


public class 稀疏数组搜索{
	public static void main(String[] args) {
		Solution solution = new 稀疏数组搜索().new Solution();
		int index = solution.findString(new String[]{"at", "", "", "", "ball", "", "", "car", "", "", "dad", "", ""}, "dad");
		System.out.println(index);

	}
//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int findString(String[] words, String s) {

    	if (s == null || s.equals("") || words.length == 0) {
    		return -1;
		}

    	int l = 0, r= words.length -1;

    	while (l <= r) {
    		int mid = l +(r-l) /2;

    		while (words[mid].equals("") && mid > l) {
    			mid--;
			}

    		if (words[mid].equals(s)) {
    			return mid;
			} else if (words[mid].compareTo(s) > 0) {
				r = mid -1;
			} else if (words[mid].compareTo(s) < 0) {
				l = mid + 1;
			}
		}

//		for (int i =0;i < words.length;i ++) {
//			if (words[i].equals(s)) {
//				return i;
//			}
//		}
		return -1;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}
