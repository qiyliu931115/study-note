package com.example.datastructure.base.linkedList;

public class DoubleLinkedListDemo {

    public static void main(String[] args) {

    }



}





class DoubleLinkedList {
    //初始化头节点 头节点不要动

    private HeroNode2 heroNode2 = new HeroNode2(0, "", "");

    public HeroNode2 getHead(){
        return heroNode2;
    }


    //显示链表 遍历
    public void list () {
        if (heroNode2.next == null){
            System.out.println("链表为空");
            return;
        }
        //head不能动 需要辅助节点
        HeroNode2 temp = heroNode2.next;
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
        if (heroNode2.next == null){
            System.out.println("链表为空");
            return;
        }
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

    public void update (HeroNode2 heroNode2) {
        if (heroNode2.next == null){
            System.out.println("链表为空");
            return;
        }
        //辅助节点
        HeroNode2 temp = heroNode2.next;


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