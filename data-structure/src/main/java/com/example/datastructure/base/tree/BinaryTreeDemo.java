package src.main.java.com.example.datastructure.base.tree;

public class BinaryTreeDemo {

    public static void main(String[] args) {
        BinaryTree binaryTree = new BinaryTree();
        HearNode hearNode = new HearNode(1, "宋江");
        HearNode hearNode2 = new HearNode(2, "吴用");
        HearNode hearNode3 = new HearNode(3, "卢俊义");
        HearNode hearNode4 = new HearNode(4, "林冲");
        //先手动创建二叉树，后面学习递归方式创建二叉树
        hearNode.setLeft(hearNode2);
        hearNode.setRight(hearNode3);
        hearNode3.setRight(hearNode4);

        binaryTree.setRoot(hearNode);

        //测试
        System.out.println("前序遍历"); //1,2,3,4
        binaryTree.prevOrder();

        System.out.println("中序遍历"); //2,1,3,4
        binaryTree.midOrder();

        System.out.println("后序遍历"); //2,4,3,1
        binaryTree.rearOrder();


        HearNode hearNode5 = new HearNode(5, "关胜");
        hearNode3.setLeft(hearNode5);

        System.out.println("前序遍历"); //1,2,3,5,4
        binaryTree.prevOrder();

        System.out.println("中序遍历"); //2,1,5,3,4
        binaryTree.midOrder();

        System.out.println("后序遍历"); //2,5,4,3,1
        binaryTree.rearOrder();
    }

}

//定义二叉数
class BinaryTree {

    private HearNode root;

    public void setRoot(HearNode root) {
        this.root = root;
    }

    //前序遍历
    public void prevOrder() {
        if (this.root != null) {
            this.root.prevOrder();
        } else {
            System.out.println("二叉树为空 无法遍历");
        }
    }

    //中序遍历
    public void midOrder() {
        if (this.root != null) {
            this.root.midOrder();
        } else {
            System.out.println("二叉树为空 无法遍历");
        }
    }

    //后序遍历
    public void rearOrder() {
        if (this.root != null) {
            this.root.rearOrder();
        } else {
            System.out.println("二叉树为空 无法遍历");
        }
    }
}

class HearNode {

    private int no;
    private String name;
    private HearNode left;//默认null
    private HearNode right;//默认null

    public HearNode(int no,String name) {
        this.no = no;
        this.name = name;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HearNode getLeft() {
        return left;
    }

    public void setLeft(HearNode left) {
        this.left = left;
    }

    public HearNode getRight() {
        return right;
    }

    public void setRight(HearNode right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "HearNode{" +
                "no=" + no +
                ", name='" + name + '\'' +
                '}';
    }

    //前序遍历

    public void prevOrder() {
        System.out.println(this);//先输出父节点
        //递归左子树
        if (this.left != null) {
            this.left.prevOrder();
        }
        //递归右子树
        if (this.right != null) {
            this.right.prevOrder();
        }
    }
    //中序遍历
    public void midOrder() {
        //递归左子树
        if (this.left != null) {
            this.left.midOrder();
        }
        System.out.println(this);//输出父节点
        //递归右子树
        if (this.right != null) {
            this.right.midOrder();
        }
    }

    //后续遍历

    public void rearOrder() {
        //递归左子树
        if (this.left != null) {
            this.left.rearOrder();
        }
        //递归右子树
        if (this.right != null) {
            this.right.rearOrder();
        }
        System.out.println(this);//输出父节点
    }
}