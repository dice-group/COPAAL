package org.dice_research.fc.paths.scorer.count;

import java.util.Map;

public class PreProcessCounter implements IPreProcessCounter {

    Map<String, Long> mapQueryResults;

    public PreProcessCounter(Map<String, Long> mapQueryResults) {
        this.mapQueryResults = mapQueryResults;
    }

    @Override
    public long count(String query) {
        query = query.replace("\n","").replace(" ","");
        query = query.replace("(count(DISTINCT*)AS?sum)", "DISTINCT?s?o");
        // look at map for this key
        if(mapQueryResults.containsKey(query)){
            return mapQueryResults.get(query);
        }else{
            return 0;
        }
    }
}
