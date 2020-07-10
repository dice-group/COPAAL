package org.dice.FactCheck.Corraborative.filter.npmi;

public class LowCountBasedNPMIFilter implements NPMIFilter {

    @Override
    public boolean npmiIsOk(double npmi, int pathLength, double count_path_Predicate_Occurrence,
            double count_Path_Occurrence, int count_predicate_Occurrence) {
        // TODO put the filtering here
        return false;
    }

}
