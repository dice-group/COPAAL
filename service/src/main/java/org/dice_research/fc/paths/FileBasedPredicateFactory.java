package org.dice_research.fc.paths;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.sparql.restrict.TypeBasedRestriction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class FileBasedPredicateFactory implements FactPreprocessor {

    protected static final int DOMAIN_RESTRICTION_ID = 0;
    protected static final int RANGE_RESTRICTION_ID = 0;

    protected Map<String, Predicate> knownProperties;

    public FileBasedPredicateFactory(Map<String, Predicate> knownProperties) {
        this.knownProperties = knownProperties;
    }

    @Override
    public Predicate generatePredicate(Statement triple) {
        String propertyIri = triple.getPredicate().getURI();
        if (!knownProperties.containsKey(propertyIri)) {
            throw new IllegalArgumentException("Got an unknown property " + propertyIri);
        }
        return knownProperties.get(propertyIri);
    }

    public static FileBasedPredicateFactory create(File file) throws IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            return create(is);
        }
    }

    public static FileBasedPredicateFactory create(InputStream is) throws IOException {
        String content = IOUtils.toString(is, StandardCharsets.UTF_8);
        Map<String, Predicate> knownProperties = new HashMap<>();
        // Parse the json file and generate all known predicate objects
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(content);
            // A JSON object. Key value pairs are unordered.
            JSONArray Predicates = (JSONArray) obj;
            @SuppressWarnings("unchecked")
            Iterator<JSONObject> iterator = (Iterator<JSONObject>) Predicates.iterator();
            while (iterator.hasNext()) {
                JSONObject jsonPredicate = iterator.next();
                // get domain
                Set<String> domainsSet = new HashSet<>();
                JSONArray domainsJson = (JSONArray) jsonPredicate.get("Domain");
                for (int i = 0; i < domainsJson.size(); i++) {
                    domainsSet.add(domainsJson.get(i).toString());
                }
                TypeBasedRestriction domain = new TypeBasedRestriction(domainsSet);
                // get range
                Set<String> rangesSet = new HashSet<>();
                JSONArray rangesJson = (JSONArray) jsonPredicate.get("Range");
                for (int i = 0; i < rangesJson.size(); i++) {
                    rangesSet.add(rangesJson.get(i).toString());
                }
                TypeBasedRestriction range = new TypeBasedRestriction(rangesSet);

                Property p = new PropertyImpl(jsonPredicate.get("Predicate").toString());
                knownProperties.put(p.getURI(), new Predicate(p, domain, range));
            }
            return new FileBasedPredicateFactory(knownProperties);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

}
