package src.main.java.com.example.datastructure.base.tree.threadedbinarytree;

public class ThreadedBinaryTreeDemo {

    //    1
    // 3     6
    //8 10 14 null
    public static void main(String[] args) {
        HeroNode heroNode = new HeroNode(1, "tom");
        HeroNode heroNode2 = new HeroNode(3, "jack");
        HeroNode heroNode3 = new HeroNode(6, "smith");
        HeroNode heroNode4 = new HeroNode(8, "mary");
        HeroNode heroNode5 = new HeroNode(10, "king");
        HeroNode heroNode6 = new HeroNode(14, "dim");

        heroNode.setLeft(heroNode2);
        heroNode.setRight(heroNode3);

        heroNode2.setLeft(heroNode4);
        heroNode2.setRight(heroNode5);

        heroNode3.setLeft(heroNode6);

        ThreadedBinaryTree threadedBinaryTree = new ThreadedBinaryTree();
        threadedBinaryTree.setRoot(heroNode);
        threadedBinaryTree.threadedNodes();


        HeroNode heroNode5Left = heroNode5.getLeft();
        System.out.println("10的前驱节点是：" + heroNode5Left);

        System.out.println("10的后继节点是：" + heroNode5.getRight());

        threadedBinaryTree.threadedList();
        System.out.println("使用线索化方式遍历二叉树"); //8,3,10,1,14,6
    }
}

//定义线索二叉数
class ThreadedBinaryTree {

    private HeroNode root;

    //指向当前节点的前驱节点 默认是null
    private HeroNode prev;

    //指向当前节点的后继节点 默认是null
    private HeroNode next;

    public void setRoot(HeroNode root) {
        this.root = root;
    }
    public void threadedNodes() {
        threadedNodes(root);
    }

    //遍历线索化二叉树的方法
    public void threadedList() {
        //存储当前遍历的节点 从root开始
        HeroNode node = root;

        while (node != null) {
            //循环找到leftType = 1
            //后面随着遍历而变化 因为当left等于1的时候，说明该节点是按照线索化
            //处理后的有效节点
            while (node.getLeftType() ==0) {
                node = node.getLeft();
            }
            //打印当前节点
            System.out.println(node);
            //如果当前节点的右指针 指向的是后继节点 就一直输出

            while (node.getRightType() == 1) {
                //获取当前节点的后继节点
                node = node.getRight();
                System.out.println(node);
            }
            //替换遍历的节点
            node = node.getRight();
        }
    }

    //编写对二叉树进行中序线索化的方法
    public void threadedNodes(HeroNode node) {
        //如果node=null 不能线索化
        if (node == null) {
            return;
        }

        //先线索化左子树
        threadedNodes(node.getLeft());

        //线索化当前节点

        //处理当前节点的前驱节点
        if (node.getLeft() == null) {
            //让当前节点的左指针指向前驱节点
            node.setLeft(prev);
            //修改当前节点左指针的类型
            node.setLeftType(1);
        }

        //处理后继节点
        if (prev != null && prev.getRight() == null) {
            //让前驱节点的右指针直线当前节点
            prev.setRight(node);
            //修改前驱节点的右指针类型
            prev.setRightType(1);
        }

        //每处理一个节点后 让当前节点是下一个节点的前驱节点
        prev = node;

        //线索化右子树
        threadedNodes(node.getRight());
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

//创建node
class HeroNode {

    private int no;
    private String name;
    private HeroNode left;//默认null
    private HeroNode right;//默认null

    private int leftType;//如果为0，指向左子树，如果为1，指向前驱节点
    private int rightType;//如果为0，指向右子树，如果为1，指向后继节点

    public int getLeftType() {
        return leftType;
    }

    public void setLeftType(int leftType) {
        this.leftType = leftType;
    }

    public int getRightType() {
        return rightType;
    }

    public void setRightType(int rightType) {
        this.rightType = rightType;
    }

    public HeroNode(int no, String name) {
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