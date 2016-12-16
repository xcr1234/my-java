package com.myjava.collection;


import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

/**
 * 自己实现的HashSet
 * @see java.util.HashSet
 * @param <E>
 */
public class HashSet<E> extends AbstractSet<E> implements Serializable{
    private static final long serialVersionUID = -2914675136220136027L;

    private HashMap<E,Object> map;
    private static final Object OBJECT = new Object();

    public HashSet(){
        map = new HashMap<E, Object>();
    }

    public HashSet(int capacity){
        map = new HashMap<E, Object>(capacity);
    }

    public HashSet(Collection<? extends E> collection){
        this(collection.size());
        this.addAll(collection);
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public boolean add(E e) {
        return map.put(e,OBJECT)==OBJECT;
    }

    @Override
    public boolean remove(Object o) {
        return map.remove(o)==OBJECT;
    }

    @Override
    public void clear() {
        map.clear();
    }
}
