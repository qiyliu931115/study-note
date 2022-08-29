package src.main.java.com.example.datastructure.base.lru;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * hashMap 加 双向链表
 */
public class LRUCacheDemo2 {

    //hashmap负责查找 构建虚拟的双向链表 里面是node节点 作为数据载体

    //构造一个node节点 作为数据载点
    class  Node<K,V> {

        K key;
        V value;
        Node<K,V> prev;
        Node<K,V> next;

        public Node() {
            this.prev = null;
            this.next = null;

        }

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.prev = null;
            this.next = null;
        }

    }

    //构造一个双向队列，里面安放的就是node

    class DoubleLinkedList<K,V> {
        Node<K,V>  head;
        Node<K,V>  tail;

        public  DoubleLinkedList() {
            this.head = new Node<>();
            this.tail = new Node<>();

            this.head.next = this.tail;
            this.tail.prev = this.head;
        }

        //添加到头
        public void addHead(Node<K,V> node) {
            node.next = this.head.next;
            node.prev = this.head;
            head.next.prev = node;
            head.next = node;
        }

        //删除节点
        public void removeNode(Node<K,V> node) {
            node.next.prev = node.prev;
            node.prev.next = node.next;
            node.prev = null;
            node.next = null;
        }

        //获得最后一个节点
        public Node<K,V> getLast() {
            return this.tail.prev;
        }
    }

    private int  cacheSize;

    private Map<Integer, Node<Integer,Integer>> map;
    private DoubleLinkedList<Integer, Integer> doubleLinkedList;


    public LRUCacheDemo2(int cacheSize) {
        this.cacheSize = cacheSize;
        map = new HashMap<>();
        doubleLinkedList = new DoubleLinkedList<>();
    }

    public int get (int key) {
        if (!map.containsKey(key)) {
            return -1;

        }

        Node<Integer, Integer> integerIntegerNode = map.get(key);

        doubleLinkedList.removeNode(integerIntegerNode);

        doubleLinkedList.addHead(integerIntegerNode);

        return  integerIntegerNode.value;
    }

    public void put (int key,int value) {
        if (map.containsKey(key)) {
            Node<Integer, Integer> integerIntegerNode = map.get(key);
            integerIntegerNode.value = value;
            map.put(key, integerIntegerNode);

            doubleLinkedList.removeNode(integerIntegerNode);
            doubleLinkedList.addHead(integerIntegerNode);
        } else {
            if (map.size() == cacheSize) {//容量已满
                Node<Integer, Integer> last = doubleLinkedList.getLast();
                map.remove(last.key);
                doubleLinkedList.removeNode(last);
            }
            //新增
            Node<Integer, Integer> newNode = new Node<>(key, value);
            map.put(key, newNode);
            doubleLinkedList.addHead(newNode);
        }
    }

    public static void main(String[] args) {
        LRUCacheDemo2 lruCacheDemo  = new LRUCacheDemo2(3);

        lruCacheDemo.put(1, 1);
        lruCacheDemo.put(2, 2);
        lruCacheDemo.put(3, 3);
        System.out.println(lruCacheDemo.map.keySet());

        lruCacheDemo.put(4, 4);
        System.out.println(lruCacheDemo.map.keySet());


        lruCacheDemo.put(3, 1);
        System.out.println(lruCacheDemo.map.keySet());

        lruCacheDemo.put(3, 1);
        System.out.println(lruCacheDemo.map.keySet());

        lruCacheDemo.put(3, 1);
        System.out.println(lruCacheDemo.map.keySet());

        lruCacheDemo.put(5, 1);
        System.out.println(lruCacheDemo.map.keySet());
    }

}
