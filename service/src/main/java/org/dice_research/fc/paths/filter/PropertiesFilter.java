package org.dice_research.fc.paths.filter;

import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Property;
import org.dice_research.fc.data.QRestrictedPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

// this class will filter a path if contains a filtered properties
public class PropertiesFilter implements IPathFilter{
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesFilter.class);
    private List<String> invalidProperties;
    public PropertiesFilter(String[] invalidProperties){
        this.invalidProperties  = new ArrayList<>();
        if(invalidProperties!= null) {
            LOGGER.info("PropertiesFilter loaded with "+invalidProperties.length+" properties");
            for (int i = 0; i < invalidProperties.length; i++) {
                this.invalidProperties.add(invalidProperties[i]);
            }
        }else{
            LOGGER.info("PropertiesFilter loaded with no properties");
        }
    }

    @Override
    public boolean test(QRestrictedPath path) {
        boolean isValid = true;
        List<Pair<Property, Boolean>> elements = path.getPathElements();
        for (Pair<Property, Boolean> p: elements) {
            if(invalidProperties.contains(p.getFirst().getURI())){
                isValid = false;
                return false;
            }
        }

        return isValid;
    }
}
