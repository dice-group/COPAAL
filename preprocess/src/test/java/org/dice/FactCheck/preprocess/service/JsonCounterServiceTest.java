package org.dice.FactCheck.preprocess.service;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class JsonCounterServiceTest {
    @Test
    public void serviceShouldCountFromFileCorrect(){
        ICounter service = new JsonCounterService();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("exampleJsonResult.json").getFile());

        long actual = service.count(file.getAbsolutePath());
        long expected = 56;

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void serviceShouldCountFromEmptyFileCorrect(){
        ICounter service = new JsonCounterService();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("exampleJsonResultEmpty.json").getFile());

        long actual = service.count(file.getAbsolutePath());
        long expected = 0;

        Assert.assertEquals(expected, actual);
    }
}
