package src.main.java.com.example.datastructure.base.hastab;

import java.util.Scanner;

public class HashTableDemo {

    public static void main(String[] args) {
        HashTable hashTable = new HashTable(7);
        String key = "";
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("add:添加雇员");
            System.out.println("list:显示雇员");
            System.out.println("find:查早雇员");
            System.out.println("exit:退出系统");

            key = scanner.next();
            switch (key) {
                case "add":
                    System.out.println("输入ID");
                    int id = scanner.nextInt();
                    System.out.println("输入名字");
                    String name = scanner.next();
                    Emp emp = new Emp(id, name);
                    hashTable.add(emp);
                    break;
                case "list":
                    hashTable.list();
                    break;
                case "exit":
                    scanner.close();
                    System.exit(0);
                case "find":
                    System.out.println("输入要查找的ID");
                    id = scanner.nextInt();
                    hashTable.getEmpById(id);
                    break;
                case "del":
                    System.out.println("输入要删除的ID");
                    id = scanner.nextInt();
                    hashTable.del(id);
                    break;
                default:
                    break;
            }
        }
    }

}

//雇员
class Emp {
    public int id;

    public String name;

    public Emp next;

    public Emp(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Emp(int id, String name, Emp next) {
        this.id = id;
        this.name = name;
        this.next = next;
    }
}

class HashTable {
    private EmpLinkedList[] empLinkedLists;
    private int size;

    public HashTable(int size) {
        this.empLinkedLists = new EmpLinkedList[size];
        this.size = size;
        for (int i = 0; i< size;i++) {
            empLinkedLists[i] = new EmpLinkedList();
        }
    }

    public void add(Emp emp) {
        //根据员工ID判断放入哪个链表
        int hash = hash(emp.id);
        empLinkedLists[hash].add(emp);
    }

    public int hash(int id) {
        return id % size;
    }

    public void list() {

        for (int i = 0; i< size;i++) {
            empLinkedLists[i].list(i);
        }
    }

    public Emp getEmpById(int id) {
        //使用散列函数 定位到哪条链表
        int hash = hash(id);

        Emp empById = empLinkedLists[hash].getEmpById(id);
        if (empById == null) {
            System.out.println("没有找到该雇员");
            return null;
        }
        System.out.println("在第" + (hash+1) + "条链表中找到该雇员,id=" + empById.id + ",name=" + empById.name);
        return empById;
    }

    public void del(int id) {

        int hash = hash(id);
        empLinkedLists[hash].del(id);
    }
}

class EmpLinkedList {
    //头指针
    private Emp head;

    public void add(Emp emp) {

        if (this.head == null) {
            this.head = emp;
            return;
        }

        Emp temp = this.head;
        if (temp.id == emp.id) {
            temp.name = emp.name;
            return;
        }
        while (temp.next != null) {
            temp = temp.next;
            if (temp.id == emp.id) {
                temp.name = emp.name;
                return;
            }
        }

        temp.next = emp;
    }

    public void list(int no) {
        if (this.head == null) {
            System.out.println("第" +(no+1)+"条链表为空");
            return;
        }
        System.out.print("第" +(no+1)+ "条链表信息为");
        Emp temp = this.head;
        while (true) {
            System.out.printf(" => id=%d name=%s\t", temp.id,temp.name);
            if (temp.next == null) {
                break;
            }
            temp = temp.next;
        }
        System.out.println();

    }

    public void del(int id) {

        if (head == null) {
            System.out.println("链表为空");
            return;
        }

        if (head.id == id) {
            head = head.next;
            return;
        }

        Emp temp = head;

        while (temp.next != null) {
            if (temp.next.id == id) {
                temp.next = temp.next.next;
                return;
            }
            temp = temp.next;
        }

        System.out.println("未找到数据");
    }


    public Emp getEmpById(int id) {
        if (head == null) {
            System.out.println("链表为空");
            return null;
        }
        Emp temp = head;
        while (true) {
            if (temp.id == id) {
                return temp;
            }

            if (temp.next == null) {
                return null;
            }
            temp = temp.next;
        }
    }
}
