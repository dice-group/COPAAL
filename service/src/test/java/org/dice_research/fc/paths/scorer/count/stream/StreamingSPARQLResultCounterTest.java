package org.dice_research.fc.paths.scorer.count.stream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class StreamingSPARQLResultCounterTest {

    protected String jsonString;
    protected int expectedCount;

    public StreamingSPARQLResultCounterTest(String jsonString, int expectedCount) {
        this.jsonString = jsonString;
        this.expectedCount = expectedCount;
    }

    @Test
    public void test() throws IOException {
        Assert.assertEquals(expectedCount, StreamingSPARQLResultCounter
                .countResults(new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8))));
    }

    @Parameters
    public static List<Object[]> parameters() {
        List<Object[]> testCases = new ArrayList<>();

        testCases.add(new Object[] {
                "{\"head\":{\"vars\":[\"s\",\"o\"]},\"results\":{\"bindings\":[{\"s\":{\"type\":\"uri\",\"value\":"
                        + "\"http://dbpedia.org/resource/Thunderstone_(card_game)\"},\"o\":{\"type\":\"uri\","
                        + "\"value\":\"http://dbpedia.org/resource/Alderac_Entertainment_Group\"}},{\"s\":{"
                        + "\"type\":\"uri\",\"value\":\"http://dbpedia.org/resource/GURPS_Autoduel\"},\"o\":{"
                        + "\"type\":\"uri\",\"value\":\"http://dbpedia.org/resource/Steve_Jackson_Games\"}},{\"s\""
                        + ":{\"type\":\"uri\",\"value\":"
                        + "\"http://dbpedia.org/resource/Go_to_the_Head_of_the_Class\"},\"o\":{\"type\":\"uri\","
                        + "\"value\":\"http://dbpedia.org/resource/Winning_Moves\"}}]}}",
                3 });
        // Example from https://www.w3.org/TR/sparql11-results-json/#example
        testCases.add(new Object[] {
                "{   \"head\": {       \"link\": [           \"http://www.w3.org/TR/rdf-sparql-XMLres/example.rq\"           ],       \"vars\": [           \"x\",           \"hpage\",           \"name\",           \"mbox\",           \"age\",           \"blurb\",           \"friend\"           ]       },   \"results\": {       \"bindings\": [               {                   \"x\" : { \"type\": \"bnode\", \"value\": \"r1\" },                   \"hpage\" : { \"type\": \"uri\", \"value\": \"http://work.example.org/alice/\" },                   \"name\" : {  \"type\": \"literal\", \"value\": \"Alice\" } ,                            \"mbox\" : {  \"type\": \"literal\", \"value\": \"\" } ,                   \"blurb\" : {                     \"datatype\": \"http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral\",                     \"type\": \"literal\",                     \"value\": \"<p xmlns=\\\"http://www.w3.org/1999/xhtml\\\">My name is <b>alice</b></p>\"                   },                   \"friend\" : { \"type\": \"bnode\", \"value\": \"r2\" }               },               {                   \"x\" : { \"type\": \"bnode\", \"value\": \"r2\" },                                      \"hpage\" : { \"type\": \"uri\", \"value\": \"http://work.example.org/bob/\" },                                      \"name\" : { \"type\": \"literal\", \"value\": \"Bob\", \"xml:lang\": \"en\" },                   \"mbox\" : { \"type\": \"uri\", \"value\": \"mailto:bob@work.example.org\" },                   \"friend\" : { \"type\": \"bnode\", \"value\": \"r1\" }               }           ]       }}",
                2 });
        // Empty result
        testCases.add(new Object[] {
                "{\"head\":{\"vars\":[\"s\",\"o\"]},\"results\":{\"bindings\":[]}}",
                0 });

        return testCases;
    }
}
