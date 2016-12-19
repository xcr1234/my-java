package com.myjava.collection;


import java.io.*;
import java.util.*;

/**
 * 自己实现的链表
 * @see java.util.LinkedList
 */
public class LinkedList<E> extends AbstractList<E> implements List<E>,Externalizable,Cloneable {

    private static final long serialVersionUID = -4851064770587108388L;
    private Node<E> head;
    private Node<E> bottom;
    private transient int modCount = 0;

    public LinkedList(){
        head = new Node<E>(null);
        bottom = head;
    }

    public LinkedList(Collection<? extends E> collection){
        this();
        addAll(collection);
    }

    @Override
    public E get(int index) {
        Node<E> node = findNode(index);
        return node.value;
    }

    @Override
    public int size() {
        Node<E> node = head;
        for(int i=0;;i++){
           node = node.next;
           if(node == null){
               return i;
           }
        }
    }

    @Override
    public boolean add(E e) {
        modCount++;
        Node<E> node = new Node<E>(e);
        bottom.next = node;
        bottom = node;
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object clone() {
        try {
            LinkedList linkedList = (LinkedList) super.clone();
            linkedList.head = new Node(null);
            linkedList.bottom = linkedList.head;
            linkedList.addAll(this);
            return linkedList;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }


    private Node<E> findNode(int index){
        if(index<0){
            throw new IndexOutOfBoundsException("index:"+index);
        }
        Node<E> node = head;
        for(int i=0;i<index;i++){
            node = node.next;
            if(node == null){
                throw new IndexOutOfBoundsException("index:"+index+",size:"+i);
            }
        }
        return node;
    }

    private Node<E> findPrevNode(int index){
        if(index<0){
            throw new IndexOutOfBoundsException("index:"+index);
        }
        Node<E> node = head;
        for(int i=0;i<index-1;i++){
            node = node.next;
            if(node == null){
                throw new IndexOutOfBoundsException("index:"+index+",size:"+i);
            }
        }
        return node;
    }

    @Override
    public E set(int index, E element) {
        modCount++;
        Node<E> node = findNode(index);
        E old = node.value;
        node.value = element;
        return old;
    }

    @Override
    public void add(int index, E element) {
        modCount++;
        Node<E> node = findPrevNode(index);
        Node<E> eleNode = new Node<E>(element,node.next);
        if(node.next==null){
            bottom = eleNode;
        }
        node.next = eleNode ;
    }

    @Override
    public E remove(int index) {
        modCount++;
        Node<E> node = findPrevNode(index);
        Node<E> current = node.next;
        if(current==null){
            throw new IndexOutOfBoundsException("index:"+index+",size:"+index);
        }
        if(current.next == null){
            node.next = null;
            bottom = node;
        }else{
            node.next = current.next;
        }
        return current.value;
    }

    @Override
    public void clear() {
        modCount++;
        head.next = null;
        bottom = head;
    }

    @Override
    public Iterator<E> iterator() {
        return new LinkedListItr();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        //序列化时，只序列化内部的值即可，不用序列化Node复杂的结构
        out.writeInt(size());
        Node<E> node = head;
        while (node.next!=null){
            node = node.next;
            out.writeObject(node.value);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int size = in.readInt();
        for(int i=0;i<size;i++){
            this.add((E) in.readObject());
        }
    }

    private static class Node<E> {
        Node(E value){
            this.value = value;
        }
        Node(E value,Node<E> next){
            this.value = value;
            this.next = next;
        }
        E value;
        Node<E> next;
    }

    private class LinkedListItr implements Iterator<E>{

        private int mod = modCount;
        private Node<E> node = head;
        private Node<E> prev;
        private boolean removed = false;

        @Override
        public boolean hasNext() {
            prev = null;
            return node.next != null;
        }

        @Override
        public E next() {
            checkMod();
            if(node.next==null){
                throw new NoSuchElementException();
            }
            removed = false;
            prev = node;
            node = node.next;
            return node.value;
        }


        public void remove() {
            checkMod();
            if(removed||prev==null||node==null){
                throw new IllegalStateException();
            }
            removed = true;
            if(node.next==null){
                bottom = prev;
            }
            prev.next = node.next;
            node = prev;
        }

        private void checkMod(){
            if(mod!=modCount){
                throw new ConcurrentModificationException();
            }
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(10);
        list.add(0);

        LinkedList<Integer> linkedList = new LinkedList<>();
        linkedList.add(3);
        linkedList.add(4);
        linkedList.addAll(list);

        System.out.println(linkedList);
        System.out.println(linkedList.size());

        linkedList.add(2,1);
        System.out.println(linkedList);

        System.out.println(linkedList.remove(4));
        System.out.println(linkedList);

        Iterator<Integer> iterator = linkedList.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }

        //测试序列化
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(linkedList);

        byte[] bytes = baos.toByteArray();

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        System.out.println(ois.readObject());

        LinkedList<Integer> clone = (LinkedList<Integer>) linkedList.clone();
        clone.add(5);
        System.out.println(linkedList);
        System.out.println(clone);
    }
}
