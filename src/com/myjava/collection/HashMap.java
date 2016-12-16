package com.myjava.collection;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * 自己实现一个HashMap，基于自己实现的链表——LinkedList
 *
 * @see java.util.HashMap
 */
public class HashMap<K,V> extends AbstractMap<K,V> implements Map<K,V>,Serializable {

    private static final long serialVersionUID = 4340114131866878570L;
    private LinkedList<HashMapEntry<K,V>>[] table;

    private static final int initCapacity = 16;
    private static final double loadFactor = 0.75;

    @SuppressWarnings("unchecked")
    public HashMap(){
        this.table =  new LinkedList[initCapacity];
    }
    @SuppressWarnings("unchecked")
    public HashMap(int init){
        //保证数组的容量是2的幂次。为什么要这样设计呢？不懂
        int capacity = 1;
        while (capacity<init){
            capacity <<= 1;
        }
        this.table = new LinkedList[capacity];
    }

    public HashMap(Map<? extends K,? extends V> map){
        this(map.size());
        this.putAll(map);
    }

    private int hash(Object o){
        return hash(o,table.length);
    }

    private int hash(Object o,int length){
        //改进的hash算法，来源：http://zhangshixi.iteye.com/blog/672697
        //其实也可以用非常简单的hash，求余数 需要注意hash值可能是负数！
        // Math.abs(o==null?0:o.hashCode()) % length
        int h = (o==null?0:o.hashCode());
        h ^= (h >>> 20) ^ (h >>> 12);
        h = h ^ (h >>> 7) ^ (h >>> 4);
        return h &(length-1);
    }

    private void checkIfNeedResize(LinkedList<HashMapEntry<K,V>> currentList){
        //检查是否需要扩容，如果是，则扩容为2*table.length
        if(currentList.size() >= loadFactor * table.length){
            resize(2*table.length);
        }
    }

    @SuppressWarnings("unchecked")
    private void resize(int newLength){
        //hashmap扩容，newLength一般是原table的2倍。
        LinkedList<HashMapEntry<K,V>>[] newTable = new LinkedList[newLength];
        //遍历原table中每个元素，重新计算hash值，放到newTable中.
        for (LinkedList<HashMapEntry<K, V>> list : table) {
            if (list != null) {
                for (HashMapEntry<K, V> entry : list) {
                    int hash = hash(entry.key, newLength);
                    LinkedList<HashMapEntry<K, V>> newList = newTable[hash];
                    if (newList == null) {
                        newTable[hash] = newList = new LinkedList<>();
                    }
                    newList.add(entry);
                }
            }
        }
        this.table = newTable;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        final class EntryItr implements Iterator<Entry<K, V>> {

            private int i,size,t;
            private LinkedList<HashMapEntry<K,V>> currentList;
            private Iterator<HashMapEntry<K,V>> currentIterator;

            EntryItr(){
                i=t=0;
                size = HashMap.this.size();
                currentList = table[t];
            }

            @Override
            public boolean hasNext() {
                return i<size;
            }

            @Override
            public Entry<K, V> next() {
                while (currentList==null||currentList.isEmpty()||currentIterator==null||!currentIterator.hasNext()){
                    t++;
                    currentList = HashMap.this.table[t];
                    if(currentList!=null&&!currentList.isEmpty()){
                        currentIterator = currentList.iterator();
                    }
                }
                i++;
                return currentIterator.next();
            }
        }
        final class EntrySet extends AbstractSet<Entry<K,V>> {
            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new EntryItr();
            }

            @Override
            public void clear() {
                HashMap.this.clear();
            }

            @Override
            public int size() {
                return HashMap.this.size();
            }

            @Override
            public boolean contains(Object o) {
                return super.contains(o);
            }

            @Override
            public boolean remove(Object o) {
                if(o instanceof Map.Entry){
                    Map.Entry<?,?> e = (Map.Entry<?,?>) o;
                    Object key = e.getKey();
                    Object value = e.getValue();
                    return HashMap.this.remove(key,value);
                }
                return false;
            }
        }

        return new EntrySet();
    }

    @Override
    public int size() {
        int sum = 0;
        for (LinkedList<HashMapEntry<K, V>> list : table) {
            if(list!=null){
                sum+=list.size();
            }
        }
        return sum;
    }


    @Override
    public V get(Object key) {
        int h = hash(key);
        LinkedList<HashMapEntry<K,V>> list = table[h];
        if(list==null){
            return null;
        }
        for (HashMapEntry<K, V> entry : list) {
            if(objectEquals(entry.key,key)){
                return entry.value;
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        int h = hash(key);
        LinkedList<HashMapEntry<K,V>> list = table[h];
        if(list == null){
            table[h] = (list = new LinkedList<>());
        }
        for (HashMapEntry<K, V> entry : list){
            if(objectEquals(entry.key,key)){
                V old = entry.value;
                entry.value = value;
                return old;
            }
        }
        HashMapEntry<K, V> entry = new HashMapEntry<K, V>(key,value);
        list.add(entry);
        checkIfNeedResize(list);//检查扩容。
        return null;
    }

    @Override
    public V remove(Object key) {
        int h = hash(key);
        LinkedList<HashMapEntry<K,V>> list = table[h];
        if(list==null){
            return null;
        }
        Iterator<HashMapEntry<K,V>> iterator = list.iterator();
        for(int i=0;iterator.hasNext();i++){
            HashMapEntry<K,V> entry = iterator.next();
            if(objectEquals(entry.key,key)){
                return list.remove(i).value;
            }
        }
        return null;
    }

    @Override
    public void clear() {
        for(int i=0;i<table.length;i++){
            table[i] = null;
        }
    }

    protected boolean objectEquals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    /**
     * 关于Entry接口的实现，可以用{@link SimpleEntry}非常方便，但是还是自己学习实现了一下。
     * @param <K>
     * @param <V>
     */
    private static class HashMapEntry<K,V> implements Entry<K,V>,Serializable{

        private static final long serialVersionUID = 3549069266974728431L;
        final K key;
        V value;

        HashMapEntry(K key,V value){
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public String toString() {
            return key+"="+value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            HashMapEntry<?, ?> that = (HashMapEntry<?, ?>) o;

            if (key != null ? !key.equals(that.key) : that.key != null) return false;
            return value != null ? value.equals(that.value) : that.value == null;
        }

        @Override
        public int hashCode() {
            int result = key != null ? key.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }

    public static void main(String[] args) {
        HashMap<String,Integer> map = new HashMap<>();
        map.put("100",100);
        map.put("abc",10000);

        System.out.println(map);
        System.out.println(map.get("abc"));

        map.put("abc",40);
        System.out.println(map.get("abc"));
        map.put("001",1);
        System.out.println(map);

        HashMap<String,Integer> map2 = new HashMap<>(map);
        map2.put("222",2);
        System.out.println(map2);
        System.out.println(map2.size());

        Set<Entry<String,Integer>> set = map2.entrySet();
        set.remove(new HashMapEntry<>("abc",40));
        System.out.println(set);
        System.out.println(map2);
    }
}
