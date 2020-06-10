package org.dice.FactCheck.Corraborative;

import org.junit.Assert;
import org.junit.Test;

public class NPMICalculatorTest {

    private static final double DELTA = 0.0001;

    /**
     * A simple method creating the {@link NPMICalculator} instance for the test and
     * calling its npmiValue method based on the given counts.
     * 
     * @param count_predicate_Occurrence
     * @param count_subject_Triples
     * @param count_object_Triples
     * @param count_Path_Occurrence
     * @param count_path_Predicate_Occurrence
     * @return
     */
    protected double calculateNPMI(int count_predicate_Occurrence, int count_subject_Triples, int count_object_Triples,
            int count_Path_Occurrence, int count_path_Predicate_Occurrence) {
        NPMICalculator calculator = new NPMICalculator(null, null, null, null, 0, count_predicate_Occurrence,
                count_subject_Triples, count_object_Triples, null, null, null);
        return calculator.pmiValue(count_Path_Occurrence, count_path_Predicate_Occurrence);
    }

    @Test
    public void testForIndependence() {
        // The path and predicate are independent of each other
        Assert.assertEquals(0.0, calculateNPMI(2, 2, 2, 2, 1), DELTA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPathDoesNotExist() {
        calculateNPMI(2, 2, 2, 0, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPredicateDoesNotExist() {
        calculateNPMI(0, 2, 2, 2, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubjectTypeDoesNotExist() {
        calculateNPMI(2, 0, 2, 2, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testObjectTypeDoesNotExist() {
        calculateNPMI(2, 2, 0, 2, 1);
    }

    @Test
    public void testPathNeverOccursWithPredicate() {
        Assert.assertEquals(-1.0, calculateNPMI(2, 2, 2, 2, 0), DELTA);
    }

    @Test
    public void testNPMICalculation() {
        double log1 = Math.log(1);
        double log2 = Math.log(2);
        double log10 = Math.log(10);

        // A and B always occur together (i.e., NPMI is 1.0)
        Assert.assertEquals(1.0, NPMICalculator.calculateNPMI(log10, log10, log1, log10, log1, log10), DELTA);

        // A and B occur together more often than expected (i.e., NPMI is positive)
        Assert.assertTrue(0 < NPMICalculator.calculateNPMI(log1, log2, log1, log10, log1, log10));

        // A and B never occur less often together than they should (i.e., NPMI is
        // negative)
        Assert.assertTrue(0 > NPMICalculator.calculateNPMI(log1, log10, log1, log2, log1, log2));

        // A and B are independent of each other (i.e., NPMI is 0.0)
        Assert.assertEquals(0.0, NPMICalculator.calculateNPMI(log1, Math.log(4), log1, log2, log1, log2), DELTA);
    }
}
