package org.dice_research.fc.sparql.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

// this class will compare query to saved invalid queries which saved at a file
// if the query is the same with one of them then it is not valid
public class ListBaseQueryValidator implements IQueryValidator{
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ListBaseQueryValidator.class);
    private final List<String> invalidQueries;

    public ListBaseQueryValidator(List<String> invalidQueries){
        LOGGER.info("initiated FileBaseQueryValidator with "+invalidQueries.size()+" queries");
        this.invalidQueries = new ArrayList<>();
        invalidQueries.stream().forEach(s -> this.invalidQueries.add(trimRemoveSpacesMakeLowercase(s)));
    }

    @Override
    public boolean validate(String query) {
        String trimmedQuery = trimRemoveSpacesMakeLowercase(query);
        for (String s: invalidQueries) {
            if(s.equals(trimmedQuery)){
                // because it found the same invalid query in the file then it is invalid
                return false;
            }
        }
        // find nothing invalid then it is valid
        return true;
    }

    String trimRemoveSpacesMakeLowercase(String input){
        return input.trim().toLowerCase().replace(" ","").replace("\n","").replace("\t","");
    }
}
