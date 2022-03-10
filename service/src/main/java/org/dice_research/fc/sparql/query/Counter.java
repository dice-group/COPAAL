package org.dice_research.fc.sparql.query;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is an atomic counter
 * the usage is to find how many Queries run until face an error (for example 404 from Virtuoso).
 * then find that if the error occurs after some fix queries or not
 * @author Farshad Afshari
 *
 */
public class Counter {

    private static final AtomicInteger count = new AtomicInteger();
    /* add method */
    public static void add(){
        if(count.get()+1 < Integer.MAX_VALUE){
            count.getAndIncrement();
        }else{
            count.set(0);
        }
    }
    public static int show(){
        return count.get();
    }
}
