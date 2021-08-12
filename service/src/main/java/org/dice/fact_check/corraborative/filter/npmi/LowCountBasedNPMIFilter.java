package org.dice.fact_check.corraborative.filter.npmi;

public class LowCountBasedNPMIFilter implements NPMIFilter {
    private int[] pathsMinJ;

    public LowCountBasedNPMIFilter(int[] pathsMinJ) {
        this.pathsMinJ = pathsMinJ;
    }

    public int[] getpathsMinJ() {
        return pathsMinJ;
    }

    public void setpathsMinJ(int[] pathsMinJ) {
        this.pathsMinJ = pathsMinJ;
    }

    @Override
    public boolean npmiIsOk(double npmi, int pathLength, double count_path_Predicate_Occurrence,
            double count_Path_Occurrence, int count_predicate_Occurrence) {
        return count_path_Predicate_Occurrence >= this.pathsMinJ[pathLength - 1];
    }

}