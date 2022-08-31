package src.main.java.com.example.datastructure.base.tree;

public class BinaryTreeDemo {

    public static void main(String[] args) {
        BinaryTree binaryTree = new BinaryTree();
        HeroNode HeroNode = new HeroNode(1, "宋江");
        HeroNode HeroNode2 = new HeroNode(2, "吴用");
        HeroNode HeroNode3 = new HeroNode(3, "卢俊义");
        HeroNode HeroNode4 = new HeroNode(4, "林冲");
        //先手动创建二叉树，后面学习递归方式创建二叉树
        HeroNode.setLeft(HeroNode2);
        HeroNode.setRight(HeroNode3);
        HeroNode3.setRight(HeroNode4);

        binaryTree.setRoot(HeroNode);

        //测试
        System.out.println("前序遍历"); //1,2,3,4
        binaryTree.prevOrder();

        System.out.println("中序遍历"); //2,1,3,4
        binaryTree.midOrder();

        System.out.println("后序遍历"); //2,4,3,1
        binaryTree.rearOrder();


        HeroNode HeroNode5 = new HeroNode(5, "关胜");
        HeroNode3.setLeft(HeroNode5);

        System.out.println("前序遍历"); //1,2,3,5,4
        binaryTree.prevOrder();

        System.out.println("中序遍历"); //2,1,5,3,4
        binaryTree.midOrder();

        System.out.println("后序遍历"); //2,5,4,3,1
        binaryTree.rearOrder();

        System.out.println("前序遍历查找"); //次数4
        HeroNode prevSearch = binaryTree.prevSearch(5);
        if (prevSearch!=null) {
            System.out.printf("找到了,信息为 no=%d, name=%s", prevSearch.getNo(), prevSearch.getName());
            System.out.println();
        } else {
            System.out.println("没有找到,信息为 no=" + 5 + "的英雄");
        }

        System.out.println("中序遍历查找"); //次数3
        HeroNode midSearch = binaryTree.midSearch(5);
        if (midSearch!=null) {
            System.out.printf("找到了,信息为 no=%d, name=%s", midSearch.getNo(), midSearch.getName());
            System.out.println();
        } else {
            System.out.println("没有找到,信息为 no=" + 5 + "的英雄");
        }

        System.out.println("后序遍历查找"); //次数2
        HeroNode postSearch = binaryTree.postSearch(5);
        if (postSearch!=null) {
            System.out.printf("找到了,信息为 no=%d, name=%s", postSearch.getNo(), postSearch.getName());
            System.out.println();
        } else {
            System.out.println("没有找到,信息为 no=" + 5 + "的英雄");
        }

        System.out.println("删除前,前序遍历");
        binaryTree.prevOrder();
        //binaryTree.deleteNode(5);
        binaryTree.deleteNode(3);
        System.out.println("删除后,前序遍历");
        binaryTree.prevOrder();

    }

}

//定义二叉数
class BinaryTree {

    private HeroNode root;

    public void setRoot(HeroNode root) {
        this.root = root;
    }

    public void deleteNode(int no) {
        if (root == null) {
            System.out.println("空数，不能删除");
            return;
        }
        if (root.getNo() == no) {
            root = null;
            return;
        }
        //递归删除
        root.deleteNode(no);
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

    //前序查找
    public HeroNode prevSearch(int no) {
        if (this.root != null) {
            return this.root.prevSearch(no);
        } else {
            System.out.println("二叉树为空 无法查找");
            return null;
        }
    }

    //中序查找
    public HeroNode midSearch(int no) {
        if (this.root != null) {
            return this.root.midSearch(no);
        } else {
            System.out.println("二叉树为空 无法查找");
            return null;
        }
    }

    //后序查找
    public HeroNode postSearch(int no) {
        if (this.root != null) {
            return this.root.postSearch(no);
        } else {
            System.out.println("二叉树为空 无法查找");
            return null;
        }
    }
}

class HeroNode {

    private int no;
    private String name;
    private HeroNode left;//默认null
    private HeroNode right;//默认null

    public HeroNode(int no,String name) {
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

    public HeroNode getLeft() {
        return left;
    }

    public void setLeft(HeroNode left) {
        this.left = left;
    }

    public HeroNode getRight() {
        return right;
    }

    public void setRight(HeroNode right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "HeroNode{" +
                "no=" + no +
                ", name='" + name + '\'' +
                '}';
    }

    //递归删除节点
    public void deleteNode(int no) {
        if (this.left!= null && this.left.no == no) {
            this.left = null;
            return;
        }
        if (this.right!= null && this.right.no == no) {
            this.right = null;
            return;
        }

        if (this.left!=null) {
            this.left.deleteNode(no);
        }
        if (this.right!=null) {
            this.right.deleteNode(no);
        }
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

    //前序查找
    public HeroNode prevSearch(int no) {
        System.out.println("进入前序查找");
        if (this.no == no) {
            return this;
        }
        HeroNode heroNode = null;
        if (this.left!= null) {
            heroNode =  this.left.prevSearch(no);
        }
        if (heroNode != null) {
            return heroNode;
        }

        if (this.right!= null) {
            heroNode =   this.right.prevSearch(no);
        }
        return heroNode;
    }

    //中序查找
    public HeroNode midSearch(int no) {

        HeroNode heroNode = null;
        if (this.left!= null) {
            heroNode = this.left.midSearch(no);
        }
        if (heroNode!= null) {
            return heroNode;
        }
        System.out.println("进入中序查找");
        if (this.no == no) {
            return this;
        }
        if (this.right!= null) {
            heroNode = this.right.midSearch(no);
        }
        return heroNode;
    }

    //后序查找
    public HeroNode postSearch(int no) {

        HeroNode heroNode = null;
        if (this.left!= null) {
            heroNode = this.left.postSearch(no);
        }
        if (heroNode!= null) {
            return heroNode;
        }

        if (this.right!= null) {
            heroNode = this.right.postSearch(no);
        }
        if (heroNode!= null) {
            return heroNode;
        }
        System.out.println("进入后序查找");
        if (this.no == no) {
            return this;
        }
        return heroNode;
    }
}