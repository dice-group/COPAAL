package org.dice_research.fc.sparql.filter;

/**
        * An IRI filter that excludes all IRIs that equal one of the given IRIs.
        * With out Using Filter
        * @author Farshad Afshari
        *
        */
public class EqualsFilterWithoutFilter implements IRIFilter {

    /**
     * The excluded IRIs.
     */
    protected String[] excludedIRIs;

    /**
     * Constructor.
     *
     * @param excludedIRIs the excluded IRIs
     */
    public EqualsFilterWithoutFilter(String[] excludedIRIs) {
        super();
        this.excludedIRIs = excludedIRIs;
    }

    @Override
    public void addFilter(String variableName, StringBuilder queryBuilder) {
        for (int i = 0; i < excludedIRIs.length; ++i) {
            queryBuilder.append(" ?");
            queryBuilder.append(variableName);
            queryBuilder.append(" != <");
            queryBuilder.append(excludedIRIs[i]);
            queryBuilder.append("> .");
        }
    }

}
