package org.dice_research.fc.sparql.query;

import java.util.concurrent.atomic.AtomicInteger;

public class QueryCounter {
    private static final AtomicInteger count = new AtomicInteger();
    /* main method and other methods here */
    public static void add(){
        if(count.get()+1<Integer.MAX_VALUE){
            count.getAndIncrement();
        }else{
            count.set(0);
        }
    }
    public static int show(){
        return count.get();
    }
}
