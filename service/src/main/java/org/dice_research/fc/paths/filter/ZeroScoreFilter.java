package org.dice_research.fc.paths.filter;

public class ZeroScoreFilter implements IScoreFilter{
    @Override
    public boolean test(double v) {
        if(v>0.8){
            return true;
        }
        return false;
    }
}
