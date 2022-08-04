package com.example.datastructure.linkedList;

public class SingleLinkedListDemo {

    public static void main(String[] args) {
        //创建节点
        HeroNode heroNode1 = new HeroNode(1, "宋江", "及时雨");
        HeroNode heroNode2 = new HeroNode(2, "卢俊义", "玉麒麟");
        HeroNode heroNode3 = new HeroNode(3, "吴用", "智多星");
        HeroNode heroNode4 = new HeroNode(4, "林冲", "豹子头");
        SingleLinkedList singleLinkedList = new SingleLinkedList();
        singleLinkedList.add(heroNode1);
        singleLinkedList.add(heroNode2);
        singleLinkedList.add(heroNode3);
        singleLinkedList.add(heroNode4);

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
