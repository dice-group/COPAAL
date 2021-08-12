package org.dice.fact_check.corraborative.filter;

import org.dice.fact_check.corraborative.filter.npmi.LowCountBasedNPMIFilter;
import org.dice.fact_check.corraborative.filter.npmi.NPMIFilterException;
import org.junit.Assert;
import org.junit.Test;

public class NPMIFilterTest {

	// private static Random generator ;
//	List<PathFilter> pthLst;
//	PathFilter pth;
	
    protected boolean filterNPMI(double npmi, int pathLength, int count_path_Predicate_Occurrence,
            int count_Path_Occurrence, int count_predicate_Occurrence,int[] minJ) throws NPMIFilterException {
        LowCountBasedNPMIFilter filter = new LowCountBasedNPMIFilter(minJ);
        return filter.npmiIsOk(npmi,pathLength,count_path_Predicate_Occurrence,
                 count_Path_Occurrence,  count_predicate_Occurrence);
    }
    @Test
    public void returnAll() throws NPMIFilterException {
        // with 0,0 return all
    	int[] minJ= {0,0};
        Assert.assertEquals(true, filterNPMI(0.0,1,0,10,10,minJ));
    }
    @Test
    public void returnNonz() throws NPMIFilterException {
        // 
    	int[] minJ= {1,1};
    	
    	 Assert.assertEquals(false, filterNPMI(0.0,1,0,10,10,minJ));
    	 Assert.assertEquals(true, filterNPMI(0.0,1,5,10,10,minJ));
    }
    
    @Test
    public void returnNone() throws NPMIFilterException {
        // 
    	int[] minJ= {10,10};
    	
   	 Assert.assertEquals(false, filterNPMI(0.0,1,0,10,10,minJ));
   	 Assert.assertEquals(false, filterNPMI(0.0,2,5,10,10,minJ));
    }
    
    @Test
    public void diffLims() throws NPMIFilterException {
        // 
    	int[] minJ= {2,3};
    	
   	 Assert.assertEquals(false, filterNPMI(0.0,1,1,10,10,minJ));
   	 Assert.assertEquals(true, filterNPMI(0.0,2,3,10,10,minJ));
    }
}
