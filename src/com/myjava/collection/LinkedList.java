package com.myjava.collection;


import java.io.*;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 自己实现的链表
 */
public class LinkedList<E> extends AbstractList<E> implements List<E>,Externalizable {

    private static final long serialVersionUID = -4851064770587108388L;
    private final Node<E> head;
    private Node<E> bottom;

    public LinkedList(){
        head = new Node<E>(null);
        bottom = head;
    }

    public LinkedList(Collection<E> collection){
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
        Node<E> node = new Node<E>(e);
        bottom.next = node;
        bottom = node;
        return true;
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
        Node<E> node = findNode(index);
        E old = node.value;
        node.value = element;
        return old;
    }

    @Override
    public void add(int index, E element) {
        Node<E> node = findPrevNode(index);
        Node<E> eleNode = new Node<E>(element,node.next);
        if(node.next==null){
            bottom = eleNode;
        }
        node.next = eleNode ;
    }

    @Override
    public E remove(int index) {
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

    private static class Node<E> implements Serializable {
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

        private Node<E> current = head;

        @Override
        public boolean hasNext() {
            return current.next!=null;
        }

        @Override
        public E next() {
            current = current.next;
            return current.value;
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
    }
}
