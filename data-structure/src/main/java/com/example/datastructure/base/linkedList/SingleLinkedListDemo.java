package com.example.datastructure.base.linkedList;

public class SingleLinkedListDemo {

    public static void main(String[] args) {
        //创建节点
        HeroNode heroNode1 = new HeroNode(1, "宋江", "及时雨");
        HeroNode heroNode2 = new HeroNode(2, "卢俊义", "玉麒麟");
        HeroNode heroNode3 = new HeroNode(3, "吴用", "智多星");
        HeroNode heroNode4 = new HeroNode(4, "林冲", "豹子头");
        SingleLinkedList singleLinkedList = new SingleLinkedList();
//        singleLinkedList.add(heroNode1);
//        singleLinkedList.add(heroNode2);
//        singleLinkedList.add(heroNode3);
//        singleLinkedList.add(heroNode4);


        singleLinkedList.addBySort(heroNode1);
        singleLinkedList.addBySort(heroNode4);
        singleLinkedList.addBySort(heroNode3);
        singleLinkedList.addBySort(heroNode2);
        //singleLinkedList.addBySort(heroNode2);

        singleLinkedList.list();
        HeroNode heroNode5= new HeroNode(2, "小卢", "玉麒麟~~");
        singleLinkedList.update(heroNode5);
        System.out.println("修改后的链表情况");
        singleLinkedList.list();
    }
}


//定义singleLinkedList管理英雄

class SingleLinkedList {
    //初始化头节点 头节点不要动

    private  HeroNode head = new HeroNode(0, "", "");


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


    @Override
    public String toString() {
        return "HeroNode{" +
                "no=" + no +
                ", name='" + name + '\'' +
                ", nikeName='" + nikeName +
                '}';
    }
}
