package src.main.java.com.example.datastructure.base.lru;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCacheDemo<K,V> extends LinkedHashMap<K,V> {

    private int capacity; //缓存容量



    public LRUCacheDemo(int capacity) {


//            initialCapacity – the initial capacity
//            loadFactor – the load factor
//            accessOrder – the ordering mode - true for access-order, false for insertion-order

        //accessOrder = true 按插入顺序 = false 按 访问顺序
        super(capacity, 0.75F, false);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return super.size() > capacity;
    }

    public static void main(String[] args) {

        LRUCacheDemo lruCacheDemo  = new LRUCacheDemo<>(3);

        lruCacheDemo.put(1, "a");
        lruCacheDemo.put(2, "b");
        lruCacheDemo.put(3, "c");
        System.out.println(lruCacheDemo.keySet());

        lruCacheDemo.put(4, "d");
        System.out.println(lruCacheDemo.keySet());


        lruCacheDemo.put(3, "c");
        System.out.println(lruCacheDemo.keySet());

        lruCacheDemo.put(3, "c");
        System.out.println(lruCacheDemo.keySet());

        lruCacheDemo.put(3, "c");
        System.out.println(lruCacheDemo.keySet());

        lruCacheDemo.put(5, "x");
        System.out.println(lruCacheDemo.keySet());

        //如果accessOrder=true
//        [1, 2, 3]
//        [2, 3, 4]
//        [2, 4, 3]
//        [2, 4, 3]
//        [2, 4, 3]
//        [4, 3, 5]

        //如果accessOrder=false
//        [1, 2, 3]
//        [2, 3, 4]
//        [2, 3, 4]
//        [2, 3, 4]
//        [2, 3, 4]
//        [3, 4, 5]
    }
}
