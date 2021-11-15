package org.dice.FactCheck.preprocess.service;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class JsonCounterService implements ICounter{
    public long count(String fileName) {
        try {
            long count = 0;
            InputStream in = new ObjectInputStream(new FileInputStream(fileName));
            JsonFactory f = new MappingJsonFactory();
            JsonParser jp = f.createJsonParser(in);
            JsonToken current;

            current = jp.nextToken();

            if (current != JsonToken.START_OBJECT) {
                System.out.println("Error: root should be object: quiting.");
                return -1;
            }

            while (jp.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = jp.getCurrentName();
                // move from field name to field value
                if (fieldName.equals("results")) {
                    current = jp.nextToken();
                    current = jp.nextToken();
                    current = jp.nextToken();
                    if (current == JsonToken.START_ARRAY) {
                        // For each of the records in the array
                        while (jp.nextToken() != JsonToken.END_ARRAY) {
                            // read the record into a tree model,
                            // this moves the parsing position to the end of it
                            JsonNode node = jp.readValueAsTree();
                            count = count+ 1;
                        }
                    } else {
                        System.out.println("Error: records should be an array: skipping.");
                        jp.skipChildren();
                    }
                } else {
                    //System.out.println("Unprocessed property: " + fieldName);
                    jp.skipChildren();
                }
            }

            return count;

        }catch (Exception err){
            System.out.println(err.getMessage());
        }
        return -1;
    }
}
