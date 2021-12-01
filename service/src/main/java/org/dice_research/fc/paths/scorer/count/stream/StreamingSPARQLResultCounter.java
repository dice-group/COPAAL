package org.dice_research.fc.paths.scorer.count.stream;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * This class can be used to count the number of results within a SPARQL SELECT
 * query result. Note that it <b>consumes</b> the given stream while counting.
 * 
 * states:<br>
 * 0 = search for {@code "results"}<br>
 * 1 = search for {@code "bindings"}<br>
 * 2 = search for bindings array start<br>
 * 3 = within bindings array; use internal object stack to keep track of
 * starting and ending JSON objects; each ending object within the array that
 * leads to a 0 in the stack is counted as object of the array end = as soon as
 * the bindings array ends (and no other array has started before)
 * 
 * FIXME the may encounter issues if SPARQL variables have the name "results" or
 * "bindings"
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class StreamingSPARQLResultCounter {

    private static final String RESULTS_KEY_WORD = "results";
    private static final String BINDINGS_KEY_WORD = "bindings";

    /*
     * Example query result:
     * 
     * {"head":{"vars":["s","o"]},"results":{"bindings":[{"s":{"type":"uri","value":
     * "http://dbpedia.org/resource/Thunderstone_(card_game)"},"o":{"type":"uri",
     * "value":"http://dbpedia.org/resource/Alderac_Entertainment_Group"}},{"s":{
     * "type":"uri","value":"http://dbpedia.org/resource/GURPS_Autoduel"},"o":{
     * "type":"uri","value":"http://dbpedia.org/resource/Steve_Jackson_Games"}},{"s"
     * :{"type":"uri","value":
     * "http://dbpedia.org/resource/Go_to_the_Head_of_the_Class"},"o":{"type":"uri",
     * "value":"http://dbpedia.org/resource/Winning_Moves"}},
     */

    public static long countResults(InputStream is) throws IOException {
        int state = 0;
        long resultCount = 0;
        int jsonObjectsStarted = 0;
        int jsonArraysStarted = 0;

        try (JsonParser jParser = new JsonFactory().createParser(is);) {
            JsonToken token;
            token = jParser.nextToken();
            // While there is data to be read
            while (token != null) {
                switch (token) {
                case FIELD_NAME: {
                    switch (state) {
                    case 0:
                        if (RESULTS_KEY_WORD.equals(jParser.currentName())) {
                            state = 1;
                        }
                        break;
                    case 1:
                        if (BINDINGS_KEY_WORD.equals(jParser.currentName())) {
                            state = 2;
                        }
                        break;
                    default: // nothing to do
                        break;
                    } // switch (state)
                    break;
                }
                case START_OBJECT: {
                    if (state == 3) {
                        // We found an array within the bindings array
                        ++jsonObjectsStarted;
                    }
                    break;
                }
                case END_OBJECT: {
                    // We are only interested in objects within the bindings array
                    if (state == 3) {
                        --jsonObjectsStarted;
                        // If this completed a result object
                        if (jsonObjectsStarted == 0) {
                            ++resultCount;
                        }
                    }
                    break;
                }
                case START_ARRAY: {
                    switch (state) {
                    case 2:
                        // We found the bindings array!
                        state = 3;
                        break;
                    case 3:
                        // We found an array within the bindings array
                        ++jsonArraysStarted;
                        break;
                    default: // nothing to do
                        break;
                    } // switch (state)
                    break;
                }
                case END_ARRAY:
                    if (state == 3) {
                        --jsonArraysStarted;
                        if (jsonArraysStarted == 0) {
                            // If it was not some internal array that ended but the bindings array, we can
                            // directly return since we are done
                            return resultCount;
                        }
                    }
                    break;
                default: // nothing to do
                    break;
                }

                // Read the next token
                token = jParser.nextToken();
            }
        } catch (JsonGenerationException | JsonMappingException e) {
            throw new IOException(e);
        }

        return resultCount;
    }
}
