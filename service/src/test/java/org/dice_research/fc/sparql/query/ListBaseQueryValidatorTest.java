package org.dice_research.fc.sparql.query;

import org.dice_research.fc.run.Application;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ListBaseQueryValidatorTest {
    @Test
    public void shouldReturnFalseWhenQueryIsInTheInputList(){
        List<String> inputList = new ArrayList<>();
        inputList.add("select * where { \t?s ?p ?o .} limit 100 ");
        ListBaseQueryValidator validator = new ListBaseQueryValidator(inputList);

        String queryToValidate = "Select * Where {?s ?p ?o .} limit 100";
        boolean actual = validator.validate(queryToValidate);

        boolean expected = false;
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnTrueWhenQueryIsNotInTheInputList(){
        List<String> inputList = new ArrayList<>();
        inputList.add("select * where { \t?s ?p ?o .} limit 100 ");
        ListBaseQueryValidator validator = new ListBaseQueryValidator(inputList);

        String queryToValidate = "Select * Where {?x ?p ?o .} limit 100";
        boolean actual = validator.validate(queryToValidate);

        boolean expected = true;
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnTrueWhenTheInputListIsNull(){
        List<String> inputList = new ArrayList<>();
        ListBaseQueryValidator validator = new ListBaseQueryValidator(inputList);

        String queryToValidate = "Select * Where {?x ?p ?o .} limit 100";
        boolean actual = validator.validate(queryToValidate);

        boolean expected = true;
        Assert.assertEquals(expected, actual);
    }
}
