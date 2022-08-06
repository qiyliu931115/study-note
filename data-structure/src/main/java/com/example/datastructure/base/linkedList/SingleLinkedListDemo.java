package com.example.datastructure.base.linkedList;

public class SingleLinkedListDemo {

    public static void main(String[] args) {
        //创建节点
        HeroNode heroNode1 = new HeroNode(1, "宋江", "及时雨");
        HeroNode heroNode2 = new HeroNode(2, "卢俊义", "玉麒麟");
        HeroNode heroNode3 = new HeroNode(3, "吴用", "智多星");
        HeroNode heroNode4 = new HeroNode(4, "林冲", "豹子头");
        SingleLinkedList singleLinkedList = new SingleLinkedList();
        singleLinkedList.add(heroNode1);
        System.out.printf("链表的长度%d\n", singleLinkedList.getLength());
        singleLinkedList.add(heroNode2);
        System.out.printf("链表的长度%d\n", singleLinkedList.getLength());
        singleLinkedList.add(heroNode3);
        System.out.printf("链表的长度%d\n", singleLinkedList.getLength());
        singleLinkedList.add(heroNode4);
        System.out.printf("链表的长度%d\n", singleLinkedList.getLength());

        System.out.println("原来链表的情况");
        singleLinkedList.list();

        System.out.println("反转后的情况");
        reverseList(singleLinkedList.getHead());
        singleLinkedList.list();

//        System.out.println("链表中倒数第[1]的节点:" + getKthFromEnd(singleLinkedList.getHead(), 1));
//
//        System.out.println("链表中倒数第[2]的节点:" + getKthFromEnd(singleLinkedList.getHead(), 2));
//
//        System.out.println("链表中倒数第[3]的节点:" + getKthFromEnd(singleLinkedList.getHead(), 3));
//
//        System.out.println("链表中倒数第[4]的节点:" + getKthFromEnd(singleLinkedList.getHead(), 4));
//
//
//
//
//        System.out.printf("链表的长度%d\n", getLength(singleLinkedList.getHead()));
//
//
//        singleLinkedList.addBySort(heroNode1);
//        singleLinkedList.addBySort(heroNode4);
//        singleLinkedList.addBySort(heroNode3);
//        singleLinkedList.addBySort(heroNode2);
//        singleLinkedList.addBySort(heroNode2);
//
//        singleLinkedList.list();
//        HeroNode heroNode5= new HeroNode(2, "小卢", "玉麒麟~~");
//        singleLinkedList.update(heroNode5);
//        System.out.println("修改后的链表情况");
//        singleLinkedList.list();
//
//
//        singleLinkedList.del(1);
//        System.out.println("删除后链表的情况");
//        singleLinkedList.list();
//        System.out.printf("链表的长度%d\n", getLength(singleLinkedList.getHead()));
//        singleLinkedList.del(4);
//        System.out.println("删除后链表的情况");
//        singleLinkedList.list();
//        System.out.printf("链表的长度%d\n", getLength(singleLinkedList.getHead()));
//        singleLinkedList.del(2);
//        System.out.println("删除后链表的情况");
//        singleLinkedList.list();
//        System.out.printf("链表的长度%d\n", getLength(singleLinkedList.getHead()));
//        singleLinkedList.del(3);
//        System.out.println("删除后链表的情况");
//        singleLinkedList.list();
//        System.out.printf("链表的长度%d\n", getLength(singleLinkedList.getHead()));
    }


    public static int getLength (HeroNode heroNode) {
        if (heroNode.next == null) {
            return 0;
        }
        int length = 0;
        HeroNode temp = heroNode.next;
        while (temp != null) {
            length++;
            temp = temp.next;
        }
        return length;
    }


    public static void reverseList (HeroNode head) {

        if (head.next == null || head.next.next == null) {
            return;
        }

        //定义辅助指针
        HeroNode temp  = head.next;

        HeroNode next = null;//指向当前节点下一个节点

        HeroNode reverseHead = new HeroNode(0,"","");

        //从头遍历原来的链表 ，每遍历一个放到 reverseHead最前面

        while (temp != null) {
            next = temp.next;//暂时保存当前节点的下一个节点

            temp.next = reverseHead.next; //将temp下一个节点指向新链表的最前端

            reverseHead.next = temp; //将temp连接到新的链表上

            temp = next;//让temp指向下个位置
        }
        //将head.next指向reverseHead.next
        head.next = reverseHead.next;

    }

    public static HeroNode getKthFromEnd (HeroNode heroNode, int k) {
        //获取链表长度
        int length = getLength(heroNode);
        //如果K小于1 或者K大于长度 返回null
        if (k < 1 || k > length) {
            return null;
        }

        HeroNode temp = heroNode.next;
        //for循环 遍历  //
        for (int i = 0; i < length - k ; i++) {
            temp = temp.next;
        }
        return temp;
    }


}


//定义singleLinkedList管理英雄

class SingleLinkedList {
    //初始化头节点 头节点不要动

    private  HeroNode head = new HeroNode(0, "", "");

    public HeroNode getHead(){
        return head;
    }


    //不考虑编号顺序时 找到链表的最后节点 将最后节点的next指向新的节点
    public void add(HeroNode heroNode) {

        //head不能动 需要辅助节点
        HeroNode temp = head;
        //遍历链表 找到最后
        while (true) {

            if (temp.next == null) {
                break;
            }
            temp = temp.next;
        }
        //当退出循环，temp指向新的节点
        temp.next = heroNode;
    }

    //根据排名no插入到指定位置
    //如果指定位置有值 添加失败
    public void addBySort(HeroNode heroNode) {
        //head不能动 需要辅助节点
        //单链表 我们找的是temp 是位于位置的前面
        HeroNode temp = head;
        boolean flag = false; //添加的no是否已存在
        while (true) {
            if (temp.next == null) {//说明已经在链表最后
                break;
            }
            if (temp.next.no > heroNode.no) { //位置找到了 就在temp的后面
                break;
            } else if (temp.next.no == heroNode.no) { //no已存在
                flag = true;
                break;
            }
            temp = temp.next;
        }
        if (flag) {//编号已存在
            System.out.printf("准备插入的英雄的编号 %d 已经存在\n", heroNode.no);
            return;
        }
        //插入到temp的后面
        heroNode.next = temp.next; //把后面接上
        temp.next = heroNode; //把前面接上
    }

    //显示链表 遍历
    public void list () {
        if (head.next == null){
            System.out.println("链表为空");
            return;
        }
        //head不能动 需要辅助节点
        HeroNode temp = head.next;
        while (true) {
            if (temp == null) {
                break;
            }
            System.out.println(temp);
            //要后移 要不是死循环
            temp = temp.next;

        }
    }

    //根据编号no 修改节点的信息
    public void update (HeroNode heroNode) {
        if (head.next == null){
            return;
        }
        //head不能动 需要辅助节点
        HeroNode temp = head.next;
        boolean flag = false; //表示是否找到该节点
        while (true) {
            if (temp == null) {
                break; //已经遍历完链表
            }
            if (temp.no == heroNode.no) {
                flag = true;
                break;
            }
            //后移
            temp = temp.next;
        }

        if (flag) {
            temp.name = heroNode.name;
            temp.nikeName = heroNode.nikeName;
        } else {
            System.out.printf("没有找到编号 %d 的节能 不能修改\n", heroNode.no);
        }
    }

    public void del (int no) {
        HeroNode temp = head;//temp节点找打待删除的前一个节点
        boolean flag = false;//是否找到待删除的节点

        while (true) {
            if (temp.next == null) {
               break;
            } else if (temp.next.no==no) {//找到待删除的前一个节点
                flag = true;
                break;
            }
            temp = temp.next;
        }
        if (flag) {
            temp.next = temp.next.next;
        } else {
            System.out.printf("要删除的节点不存在%d\n", no);
        }
    }


    public int getLength () {
        if (head.next == null) {
            return 0;
        }
        int length = 0;
        HeroNode temp = head.next;
        while (temp != null) {
            length++;
            temp = temp.next;
        }
        return length;
    }



}


//定义heroNode
class HeroNode {
    public int no;
    public String name;
    public String nikeName;
    public HeroNode next;


    public HeroNode(int no,String name,String nikeName) {
        this.name = name;
        this.no = no;
        this.nikeName = nikeName;

    }

    public HeroNode() {


    }


    @Override
    public String toString() {
        return "HeroNode{" +
                "no=" + no +
                ", name='" + name + '\'' +
                ", nikeName='" + nikeName +
                '}';
    }
}
