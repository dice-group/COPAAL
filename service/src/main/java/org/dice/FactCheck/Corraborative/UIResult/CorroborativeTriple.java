package org.dice.FactCheck.Corraborative.UIResult;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class CorroborativeTriple {

    private String subject;
    private String object;
    private String property;

    public CorroborativeTriple(String subject, String property, String object) {
        this.subject = subject;
        this.object = object;
        this.property = property;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

}
