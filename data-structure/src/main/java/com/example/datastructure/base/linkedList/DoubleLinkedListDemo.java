package com.example.datastructure.base.linkedList;

public class DoubleLinkedListDemo {

    public static void main(String[] args) {
        System.out.println("双向链表测试");


        HeroNode2 heroNode1 = new HeroNode2(1, "宋江", "及时雨");
        HeroNode2 heroNode2 = new HeroNode2(2, "卢俊义", "玉麒麟");
        HeroNode2 heroNode3 = new HeroNode2(3, "吴用", "智多星");
        HeroNode2 heroNode4 = new HeroNode2(4, "林冲", "豹子头");

        DoubleLinkedList doubleLinkedList = new DoubleLinkedList();
//        doubleLinkedList.add(heroNode1);
//        doubleLinkedList.add(heroNode2);
//        doubleLinkedList.add(heroNode3);
//        doubleLinkedList.add(heroNode4);
//
//        doubleLinkedList.list();
//
//        HeroNode2 heroNode5= new HeroNode2(4, "公孙胜", "入云龙");
//        doubleLinkedList.update(heroNode5);
//        System.out.println("修改后的链表情况");
//        doubleLinkedList.list();
//
//
//        doubleLinkedList.del(3);
//        System.out.println("删除3后链表的情况");
//        doubleLinkedList.list();
//
//        doubleLinkedList.del(2);
//        System.out.println("删除2后链表的情况");
//        doubleLinkedList.list();
//
//        doubleLinkedList.del(1);
//        System.out.println("删除1后链表的情况");
//        doubleLinkedList.list();
//
//        doubleLinkedList.del(4);
//        System.out.println("删除4后链表的情况");
//        doubleLinkedList.list();



        doubleLinkedList.addBySort(heroNode1);
        doubleLinkedList.addBySort(heroNode1);
        doubleLinkedList.addBySort(heroNode3);
        doubleLinkedList.addBySort(heroNode2);
        doubleLinkedList.addBySort(heroNode4);

        doubleLinkedList.list();
    }



}



class DoubleLinkedList {
    //初始化头节点 头节点不要动

    private HeroNode2 dummy = new HeroNode2(0, "", "");

    public HeroNode2 getHead(){
        return dummy;
    }


    //显示链表 遍历
    public void list () {
        if (dummy.next == null){
            System.out.println("链表为空");
            return;
        }
        //head不能动 需要辅助节点
        HeroNode2 temp = dummy.next;
        while (true) {
            if (temp == null) {
                break;
            }
            System.out.println(temp);
            //要后移 要不是死循环
            temp = temp.next;

        }
    }

    public void  del (int no) {

        HeroNode2 temp = getHead();
        if (temp.next == null) {
            System.out.println("链表为空，无法删除");
            return;
        }

        boolean flag = false;
        while (temp!=null) {
            if (temp.no == no) {
                flag =true;
                break;
            }else {
                temp = temp.next;
            }
        }
        if (flag) {
            temp.prev.next = temp.next;
            if (temp.next !=null) {
                temp.next.prev = temp.prev;
            }
        } else {
            System.out.printf("要删除的节点不存在%d\n", no);
        }

    }



    /**
     * 添加到双向链表最后
     * @param heroNode2
     */
    public void add (HeroNode2 heroNode2) {
        //辅助节点
        HeroNode2 temp = getHead();

        while (true) {
            if (temp.next == null) {
                break;
            } else  {
                temp = temp.next;
            }
        }
        temp.next = heroNode2;
        heroNode2.prev = temp;

    }

    //根据排名no插入到指定位置
    //如果指定位置有值 添加失败
    public void addBySort(HeroNode2 insertNode) {
        //head不能动 需要辅助节点
        //双链表 我们找的是temp
        HeroNode2 temp = getHead();

        while (true) {
            if (temp.next == null) {
                break;
            }
            if (temp.next.no == insertNode.no) {
                System.out.printf("准备插入的英雄的编号 %d 已经存在\n", insertNode.no);
                return;
            }

            if (temp.next.no < insertNode.no) {
                break;
            }
            temp = temp.next;

        }
        insertNode.prev = temp;
        insertNode.next = temp.next;  //这里的temp如果是最后一个节点, insertNode.next = null

//        temp.next.prev = insertNode;  这样temp 就等于 insertNode
//        temp.next = insertNode; //    而insertNode 的next


        temp.next = insertNode;
        temp.next.prev = insertNode;

    }

    public void update (HeroNode2 heroNode2) {
        if (getHead().next == null){
            System.out.println("链表为空");
            return;
        }
        //辅助节点
        HeroNode2 temp = getHead();


        boolean flag = false;

        while (temp != null) {
            if (temp.no == heroNode2.no) {
                flag = true;
                break;
            } else  {
                temp = temp.next;
            }
        }

        if (flag) {
            temp.name = heroNode2.name;
            temp.nikeName = heroNode2.nikeName;
        } else {
            System.out.printf("没有找到编号 %d 的节点 不能修改\n", heroNode2.no);
        }

    }


}



//定义heroNode
class HeroNode2 {
    public int no;
    public String name;
    public String nikeName;
    public HeroNode2 next;//后一个节点
    public HeroNode2 prev;//前一个节点


    public HeroNode2(int no,String name,String nikeName) {
        this.name = name;
        this.no = no;
        this.nikeName = nikeName;

    }

    public HeroNode2() {

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