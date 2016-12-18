package com.myjava.collection;

import java.util.Map;
import java.util.Random;

/**
 * 测试hashmap的性能
 */
public class TestHashMapTime {
    public static void main(String[] args) {
        Map<String,String> map = new HashMap<>();
        Map<String,String> map2 = new java.util.HashMap<>();
        long a=0,b=0;
        for(int i=0;i<100000;i++) {
            String str1 = random(500);
            String str2 = random(100);
            long t1 = System.currentTimeMillis();
            map.put(str1,str2);
            long t2 = System.currentTimeMillis();
            map2.put(str1,str2);
            long t3 = System.currentTimeMillis();
            a+=(t2-t1);
            b+=(t3-t2);
        }
        System.out.println("10万条不指定初始化，自己的耗时"+a+",hashmap耗时"+b);

        map = new HashMap<>(100000);
        map2 = new java.util.HashMap<>(100000);
        a=0;
        b=0;
        for(int i=0;i<100000;i++) {
            String str1 = random(500);
            String str2 = random(100);
            long t1 = System.currentTimeMillis();
            map.put(str1,str2);
            long t2 = System.currentTimeMillis();
            map2.put(str1,str2);
            long t3 = System.currentTimeMillis();
            a+=(t2-t1);
            b+=(t3-t2);
        }
        System.out.println("10万条指定初始化，自己的耗时"+a+",hashmap耗时"+b);
    }

    private static final char[] values = "0123456789".toCharArray();
    private static String random(int n){
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(n);
        for(int i=0;i<n;i++){
            stringBuilder.append(values[random.nextInt(values.length)]);
        }
        return stringBuilder.toString();
    }
}
