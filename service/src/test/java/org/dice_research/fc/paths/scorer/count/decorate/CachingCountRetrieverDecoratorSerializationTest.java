/*
package org.dice_research.fc.paths.scorer.count.decorate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.scorer.ICountRetriever;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.dice_research.fc.sparql.restrict.TypeBasedRestriction;
//import org.dice_research.utils.GuavaCacheHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CachingCountRetrieverDecoratorSerializationTest implements ICountRetriever {

  private boolean countPathsCalled = false;
  private boolean countPredicateInstancesCalled = false;
  private boolean countCooccurrencesCalled = false;
  private boolean deriveMaxCountCalled = false;

  private Predicate predicate;
  private QRestrictedPath path;

  public CachingCountRetrieverDecoratorSerializationTest(Predicate predicate,
      QRestrictedPath path) {
    super();
    this.predicate = predicate;
    this.path = path;
  }

 */
/* @Test
  public void test() throws IOException {
    CachingCountRetrieverDecorator decorator = new CachingCountRetrieverDecorator(this);

    Assert.assertEquals(123,
        decorator.countPathInstances(path, predicate.getDomain(), predicate.getRange()));
    Assert.assertEquals(99, decorator.countPredicateInstances(predicate));
    Assert.assertEquals(42, decorator.countCooccurrences(predicate, path));
    Assert.assertEquals(1000, decorator.deriveMaxCount(predicate));

    *//*
*/
/*GuavaCacheHelper helper = new GuavaCacheHelper();
    File pathsFile = File.createTempFile("paths", ".cache");
    helper.writeCacheToJson(decorator.pathInstanceCache, pathsFile);
    File predFile = File.createTempFile("pred", ".cache");
    helper.writeCacheToJson(decorator.predicateInstanceCache, predFile);
    File coocFile = File.createTempFile("cooc", ".cache");
    helper.writeCacheToJson(decorator.cooccurrenceCache, coocFile);
    File maxFile = File.createTempFile("max", ".cache");
    helper.writeCacheToJson(decorator.maxCountCache, maxFile);*//*
*/
/*

    decorator = new CachingCountRetrieverDecorator(this);

//    helper.initCacheFromJson(decorator.pathInstanceCache, PathInstanceCountQuery.class, Long.class, pathsFile);
*//*
*/
/*    helper.initCacheFromJson(decorator.pathInstanceCache, pathsFile);
    helper.initCacheFromJson(decorator.predicateInstanceCache, predFile);
    helper.initCacheFromJson(decorator.cooccurrenceCache, coocFile);
    helper.initCacheFromJson(decorator.maxCountCache, maxFile);*//*
*/
/*

*//*
*/
/*    Assert.assertEquals(123,
        decorator.countPathInstances(path, predicate.getDomain(), predicate.getRange()));
    Assert.assertEquals(99, decorator.countPredicateInstances(predicate));
    Assert.assertEquals(42, decorator.countCooccurrences(predicate, path));
    Assert.assertEquals(1000, decorator.deriveMaxCount(predicate));*//*
*/
/*
  }
*//*

  @Override
  public long countPathInstances(QRestrictedPath path, ITypeRestriction domainRestriction,
      ITypeRestriction rangeRestriction) {
    if (countPathsCalled) {
      Assert.fail("countPathInstances has been called twice.");
    }
    countPathsCalled = true;
    return 123;
  }

  @Override
  public long countPredicateInstances(Predicate predicate) {
    if (countPredicateInstancesCalled) {
      Assert.fail("countPredicateInstances has been called twice.");
    }
    countPredicateInstancesCalled = true;
    return 99;
  }

  @Override
  public long countCooccurrences(Predicate predicate, QRestrictedPath path) {
    if (countCooccurrencesCalled) {
      Assert.fail("countCooccurrences has been called twice.");
    }
    countCooccurrencesCalled = true;
    return 42;
  }

  @Override
  public long deriveMaxCount(Predicate predicate) {
    if (deriveMaxCountCalled) {
      Assert.fail("deriveMaxCount has been called twice.");
    }
    deriveMaxCountCalled = true;
    return 1000;
  }

  @Parameters
  public static Collection<Object[]> data() {
    List<Object[]> testConfigs = new ArrayList<Object[]>();

    QRestrictedPath path = new QRestrictedPath(Arrays.asList(
        new Pair<Property, Boolean>(ResourceFactory.createProperty("http://ex.org/p1"), true),
        new Pair<Property, Boolean>(ResourceFactory.createProperty("http://ex.org/p2"), false),
        new Pair<Property, Boolean>(ResourceFactory.createProperty("http://ex.org/p3"), true)));
    Predicate predicate = new Predicate(ResourceFactory.createProperty("http://ex.org/p0"),
        new TypeBasedRestriction(new HashSet<String>(Arrays.asList("http://ex.org/t1"))),
        new TypeBasedRestriction(new HashSet<String>(Arrays.asList("http://ex.org/t2"))));
    testConfigs.add(new Object[] {predicate, path});

    return testConfigs;
  }
}
*/
