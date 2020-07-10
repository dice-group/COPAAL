package org.dice.FactCheck.Corraborative.filter;

import java.util.ArrayList;
import java.util.List;

import org.dice.FactCheck.Corraborative.filter.PathFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PathFilterTest {

	// private static Random generator ;
	List<PathFilter> pthLst;
	PathFilter pth;
	int[] minJ= {0,0};
    @Before
    public void setUp() {
        pthLst =new ArrayList<PathFilter>();
        //PathFilter pth;
        pthLst.add(new PathFilter(null, null, null, null, 1, 10,
                10, 10, null, null, 0));

        pthLst.add(new PathFilter(null, null, null, null, 1, 10,
                10, 10, null, null, 5));
        pthLst.add(new PathFilter(null, null, null, null, 2, 10,
                10, 10, null, null, 1));
        pthLst.add(new PathFilter(null, null, null, null, 2, 10,
                10, 10, null, null, 7));
        //System.out.println(pthLst.toArray().length);

    }
    @Test
    public void returnAll() {
        // with 0,0 return all
    	
    	//System.out.println( pthLst.get(0).filterPaths(pthLst,minJ).toArray().length);
        Assert.assertEquals(pthLst.toArray().length, pthLst.get(0).filterPaths(pthLst,minJ).toArray().length);
    }
    @Test
    public void returnNonz() {
        // 
    	this.minJ[0]=1;
    	this.minJ[1]=1;
    	
        Assert.assertEquals(3, pthLst.get(0).filterPaths(pthLst,minJ).toArray().length);
    }
    
    @Test
    public void returnNone() {
        // 
    	this.minJ[0]=10;
    	this.minJ[1]=10;
    	
        Assert.assertEquals(0, pthLst.get(0).filterPaths(pthLst,minJ).toArray().length);
    }
    
    @Test
    public void DiffLims() {
        // 
    	this.minJ[0]=2;
    	this.minJ[1]=3;
    	
        Assert.assertEquals(2, pthLst.get(0).filterPaths(pthLst,minJ).toArray().length);
    }
}
