package org.dice.FactCheck.preprocess.model;

import org.apache.jena.rdf.model.Property;

import java.util.Objects;


/**
 * The representation of the predicate of a fact that should be checked.
 *
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class Predicate implements Cloneable {

    /**
     * The property (i.e., IRI) of the predicate
     */
    protected Property property;
    /**
     * The domain restriction for potential queries generated related to this predicate.
     */
    protected ITypeRestriction domain;
    /**
     * The range restriction for potential queries generated related to this predicate.
     */
    protected ITypeRestriction range;

    public Predicate(Property property, ITypeRestriction domain, ITypeRestriction range) {
        this.property = property;
        this.domain = domain;
        this.range = range;
    }

    /**
     * @return the property
     */
    public Property getProperty() {
        return property;
    }

    /**
     * @param property the property to set
     */
    public void setProperty(Property property) {
        this.property = property;
    }

    /**
     * @return the domain
     */
    public ITypeRestriction getDomain() {
        return domain;
    }

    /**
     * @param domain the domain to set
     */
    public void setDomain(ITypeRestriction domain) {
        this.domain = domain;
    }

    /**
     * @return the range
     */
    public ITypeRestriction getRange() {
        return range;
    }

    /**
     * @param range the range to set
     */
    public void setRange(ITypeRestriction range) {
        this.range = range;
    }

    public Object clone()throws CloneNotSupportedException{
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Predicate predicate = (Predicate) o;
        return Objects.equals(getProperty(), predicate.getProperty()) &&
                Objects.equals(getDomain(), predicate.getDomain()) &&
                Objects.equals(getRange(), predicate.getRange());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProperty(), getDomain(), getRange());
    }
}
