package src.main.java.com.example.datastructure.base.tree;

public class ArrayBinaryTreeDemo {

    public static void main(String[] args) {
        int[] arr = {1,2,3,4,5,6,7};

        ArrayBinaryTree arrayBinaryTree = new ArrayBinaryTree(arr);
        arrayBinaryTree.preOrder();
        System.out.println("===============");
        arrayBinaryTree.midOrder();
        System.out.println("===============");
        arrayBinaryTree.rearOrder();


    }
}

//顺序存储二叉树

class ArrayBinaryTree {
    private int[] arr; //存储数据节点的数组

    public ArrayBinaryTree(int[] arr) {
        this.arr = arr;
    }

    //编写一个方法，完成顺序存储二叉树的前序遍历


    public void preOrder() {
        this.preOrder(0);
    }

    public void midOrder() {
        this.midOrder(0);
    }

    public void rearOrder() {
        this.rearOrder(0);
    }
    /**
     *
     * @param index 数组的下标
     */
    public void preOrder(int index) {
        //如果数组为空 或者arr.length=0
        if (arr == null || arr.length ==0) {
            System.out.println("数组为空 不能按照二叉树的前序遍历");
            return;
        }
        //输出当前元素
        System.out.println(arr[index]);
        //向左递归
        if ((2 * index + 1)  < arr.length) {//数组越界
            preOrder(2 * index + 1);
        }

        //向右递归
        if ((2 * index + 2)  < arr.length) {//数组越界
            preOrder(2 * index + 2);
        }
    }

    public void midOrder(int index) {
        //如果数组为空 或者arr.length=0
        if (arr == null || arr.length ==0) {
            System.out.println("数组为空 不能按照二叉树的前序遍历");
            return;
        }

        //向左递归
        if ((2 * index + 1)  < arr.length) {//数组越界
            preOrder(2 * index + 1);
        }
        //输出当前元素
        System.out.println(arr[index]);

        //向右递归
        if ((2 * index + 2)  < arr.length) {//数组越界
            preOrder(2 * index + 2);
        }
    }

    public void rearOrder(int index) {
        //如果数组为空 或者arr.length=0
        if (arr == null || arr.length ==0) {
            System.out.println("数组为空 不能按照二叉树的前序遍历");
            return;
        }

        //向左递归
        if ((2 * index + 1)  < arr.length) {//数组越界
            preOrder(2 * index + 1);
        }


        //向右递归
        if ((2 * index + 2)  < arr.length) {//数组越界
            preOrder(2 * index + 2);
        }

        //输出当前元素
        System.out.println(arr[index]);
    }
}