package org.dice_research.fc.paths.filter;

public class ZeroScoreFilter implements IScoreFilter{
    private double threshold;
    public ZeroScoreFilter(double threshold){
        this.threshold = threshold;
    }
    @Override
    public boolean test(double v) {
        if(v>= threshold){
            return true;
        }
        return false;
    }
}
