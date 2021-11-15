package org.dice.FactCheck.preprocess.service;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class JsonCounterServiceTest {
    @Test
    public void serviceShouldCountFromFileCorrect(){
        ICounter service = new JsonCounterService();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("fa753d79-3e7f-40d1-9f4b-dccdd10c56ff.tmp").getFile());

        long actual = service.count(file.getAbsolutePath());
        long expected = 3762;

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void serviceShouldCountFromEmptyFileCorrect(){
        ICounter service = new JsonCounterService();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("502fe08e-2edb-4edd-b204-17b62a281201.tmp").getFile());

        long actual = service.count(file.getAbsolutePath());
        long expected = 0;

        Assert.assertEquals(expected, actual);
    }
}
