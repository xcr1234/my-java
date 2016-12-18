package com.myjava.collection;



import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TestItr {
    public static void main(String[] args) {
       HashMap<String,String> map = new HashMap<>();

       map.put("abc","aaa");
       map.put("100","ddd");
       map.put("001","xx");
       map.put("eee","e");
       map.put("rr","ddd");



        Iterator<Map.Entry<String, String>> entryIterator = map.entrySet().iterator();
        while (entryIterator.hasNext()){
            Map.Entry<String, String> entry = entryIterator.next();

            if("100".equals(entry.getKey())){

                entryIterator.remove();
            }
        }
        System.out.println(map);

    }
}
