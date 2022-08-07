package com.example.datastructure.base.linkedList;

public class Josepfu {

    public static void main(String[] args) {

        CircleSingletLinkedList circleSingletLinkedList = new CircleSingletLinkedList();
        circleSingletLinkedList.addBoy(50);
        circleSingletLinkedList.showBoy();

    }
}

//环形单向链表
class CircleSingletLinkedList {
    //虚拟头结点
    private Boy first = null;

    public void addBoy (int nums) {
        if (nums < 1) {
            System.out.println("nums值不正确 需要大于1");
            return;
        }

        Boy curBoy = null;

        //创建环形链表
        for (int i= 1;i <= nums;i++) {
            Boy boy = new Boy(i);
            if (i == 1) {
                first = boy;
                first.setNext(first); //构成一个环
                curBoy = first; //指向第一个小孩
            } else {
                curBoy.setNext(boy); //当前的最后一个节点的下一个节点指向最新的节点

                boy.setNext(first);//构成一个环

                curBoy = boy; //把curBoy指向当前最后一个节点
            }
        }
    }

    public void showBoy () {

        if (first == null) {
            System.out.println("链表为空");
            return;
        }
        Boy curBoy = first;
        while (true) {
            System.out.printf("小孩的编号%d\n",curBoy.getNo());
            if (curBoy.getNext() == first) { //说明已经遍历完成
                return;
            }
            curBoy = curBoy.getNext();//后移
        }
    }

    /**\根据用户的输入，计算小孩出圈的顺序
     *
     * @param startNo   表示从第几个小孩数数
     * @param countNum  数了几下
     * @param nums  表示最初有多少小孩在圈中
     */
    public void countBoy(int startNo, int countNum, int nums) {
        if (first == null || startNo < 1 || startNo > nums) {
            System.out.println("输入参数有误");
            return;
        }
        //让helper的下一个节点指向first helper -> first
        Boy helper = first;
        while (true) {
            if (helper.getNext() == first) {//说明helper -> first
                break;
            }
            helper = helper.getNext();
        }
    }
}

//创建一个boy 表示一个节点

class Boy {

    private int no; //编号
    private Boy next;//下一个节点

    public Boy (int no) {
        this.no = no;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public Boy getNext() {
        return next;
    }

    public void setNext(Boy next) {
        this.next = next;
    }
}