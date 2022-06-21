package org.dice_research.fc.paths.verbalizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.sparql.query.QueryExecutionFactoryCustomHttp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests the verbalization feature.
 *
 */
@RunWith(Parameterized.class)
public class VerbalizerTest {
  /**
   * The path
   */
  private QRestrictedPath path;
  /**
   * The fact's subject
   */
  private Resource subject;
  /**
   * The fact's object
   */
  private Resource object;
  /**
   * Expected verbalization
   */
  private String expected;

  private final QueryExecutionFactory qef =
      new QueryExecutionFactoryCustomHttp("https://dbpedia.org/sparql");


  public VerbalizerTest(Resource subject, Resource object, QRestrictedPath path, String expected) {
    this.path = path;
    this.subject = subject;
    this.object = object;
    this.expected = expected;
  }

  @Test
  public void testVerbalization() {
    IPathVerbalizer verbalizer = new MultiplePathVerbalizer(qef);
    String output = verbalizer.verbalizePaths(subject, object, path);
    Assert.assertEquals(expected, output);
  }

  @Parameters
  public static Collection<Object[]> data() {
    List<Object[]> testConfigs = new ArrayList<Object[]>();
    Resource billGates = ResourceFactory.createResource("http://dbpedia.org/resource/Bill_Gates");
    Resource unitedStates =
        ResourceFactory.createResource("http://dbpedia.org/resource/United_States");
    Resource washingtonState =
        ResourceFactory.createResource("http://dbpedia.org/resource/Washington_(state)");
    Resource california = ResourceFactory.createResource("http://dbpedia.org/resource/California");
    Resource wardForrest =
        ResourceFactory.createResource("http://dbpedia.org/resource/Ward_Forrest");
    Resource harvardUni =
        ResourceFactory.createResource("http://dbpedia.org/resource/Harvard_University");
    Resource journalist = ResourceFactory.createResource("http://dbpedia.org/resource/Journalist");
    Resource niaGill = ResourceFactory.createResource("http://dbpedia.org/resource/Nia_Gill");
    Resource bachelorArts =
        ResourceFactory.createResource("http://dbpedia.org/resource/Bachelor_of_Arts");
    Resource tayZonday = ResourceFactory.createResource("http://dbpedia.org/resource/Tay_Zonday");
    Resource minneapolis =
        ResourceFactory.createResource("http://dbpedia.org/resource/Minneapolis");
    Resource obama = ResourceFactory.createResource("http://dbpedia.org/resource/Barack_Obama");
    Resource annDunham = ResourceFactory.createResource("http://dbpedia.org/resource/Ann_Dunham");
    Resource cambridge =
        ResourceFactory.createResource("http://dbpedia.org/resource/Cambridge,_Massachusetts");
    Resource oregonUni =
        ResourceFactory.createResource("http://dbpedia.org/resource/University_of_Oregon");
    Resource illinois =
        ResourceFactory.createResource("http://dbpedia.org/resource/East_St._Louis,_Illinois");
    Resource monsterMax = ResourceFactory.createResource("http://dbpedia.org/resource/Monsterwax");
    Resource earthSuit = ResourceFactory.createResource("http://dbpedia.org/resource/Earthsuit");
    Resource barbara = ResourceFactory.createResource("http://dbpedia.org/resource/Barbara_Corday");
    Resource julius = ResourceFactory.createResource("http://dbpedia.org/resource/Julius_Hatofsky");


    String birthCountry =
        "<http://dbpedia.org/ontology/birthPlace>/<http://dbpedia.org/ontology/country>";
    String almaPath = "<http://dbpedia.org/ontology/almaMater>";
    String birthDeathResid =
        "<http://dbpedia.org/ontology/birthPlace>/^<http://dbpedia.org/ontology/deathPlace>/<http://dbpedia.org/ontology/residence>";
    String birthBirth =
        "<http://dbpedia.org/ontology/birthPlace>/^<http://dbpedia.org/ontology/birthPlace>";
    String eduOccupation =
        "^<http://dbpedia.org/ontology/education>/<http://dbpedia.org/ontology/occupation>";
    String birthSub =
        "<http://dbpedia.org/ontology/birthPlace>/<http://dbpedia.org/ontology/subdivision>";
    String childSpouse =
        "^<http://dbpedia.org/ontology/child>/^<http://dbpedia.org/property/spouse>";
    String spEdCity =
        "<http://dbpedia.org/property/spouse>/<http://dbpedia.org/ontology/education>/<http://dbpedia.org/ontology/city>";
    String birthAlma =
        "<http://dbpedia.org/ontology/birthPlace>/^<http://dbpedia.org/ontology/birthPlace>/<http://dbpedia.org/ontology/almaMater>";
    String writerProdBirth =
        "^<http://dbpedia.org/ontology/writer>/<http://dbpedia.org/ontology/producer>/<http://dbpedia.org/ontology/birthPlace>";
    String afterAlongBirth =
        "^<http://dbpedia.org/property/after>/^<http://dbpedia.org/property/alongside>/<http://dbpedia.org/ontology/birthPlace>";
    String birthCountryHq =
        "<http://dbpedia.org/ontology/birthPlace>/<http://dbpedia.org/ontology/country>/^<http://dbpedia.org/property/hqLocationCountry>";
    String genreGenreAssociate =
        "<http://dbpedia.org/ontology/genre>/^<http://dbpedia.org/ontology/genre>/^<http://dbpedia.org/ontology/associatedMusicalArtist>";
    String almaBirthBirth =
        "^<http://dbpedia.org/ontology/almaMater>/<http://dbpedia.org/ontology/birthPlace>/^<http://dbpedia.org/ontology/birthPlace>";
    String almaMajorMovement =
        "^<http://dbpedia.org/property/training>/^<http://dbpedia.org/property/majorfigures>/^<http://dbpedia.org/property/movement>";    
    String backBackBirth = "<http://dbpedia.org/ontology/background>/^<http://dbpedia.org/ontology/background>/<http://dbpedia.org/ontology/birthPlace>";
    String starStarBirth = "^<http://dbpedia.org/ontology/starring>/<http://dbpedia.org/ontology/starring>/<http://dbpedia.org/ontology/birthPlace>";
    
    QRestrictedPath birthCountryPath = QRestrictedPath.create(birthCountry, 0);
    QRestrictedPath almaPathPath = QRestrictedPath.create(almaPath, 0);
    QRestrictedPath birthDeathResidPath = QRestrictedPath.create(birthDeathResid, 0);
    QRestrictedPath birthBirthPath = QRestrictedPath.create(birthBirth, 0);
    QRestrictedPath eduOccupationPath = QRestrictedPath.create(eduOccupation, 0);
    QRestrictedPath birthSubPath = QRestrictedPath.create(birthSub, 0);
    QRestrictedPath childSpousePath = QRestrictedPath.create(childSpouse, 0);
    QRestrictedPath spEdCityPath = QRestrictedPath.create(spEdCity, 0);
    QRestrictedPath birthAlmaPath = QRestrictedPath.create(birthAlma, 0);
    QRestrictedPath writerProdBirthPath = QRestrictedPath.create(writerProdBirth, 0);
    QRestrictedPath afterAlongBirthPath = QRestrictedPath.create(afterAlongBirth, 0);
    QRestrictedPath birthCountryHqPath = QRestrictedPath.create(birthCountryHq, 0);
    QRestrictedPath genreGenreAssociatePath = QRestrictedPath.create(genreGenreAssociate, 0);
    QRestrictedPath almaBirthBirthPath = QRestrictedPath.create(almaBirthBirth, 0);
    QRestrictedPath almaMajorMovementPath = QRestrictedPath.create(almaMajorMovement, 0);
    QRestrictedPath backBackBirthPath = QRestrictedPath.create(backBackBirth, 0);
    QRestrictedPath starStarBirthPath = QRestrictedPath.create(starStarBirth, 0);

    // paths of length 1
    testConfigs.add(new Object[] {niaGill, bachelorArts, almaPathPath,
        "Nia Gill's alma mater is Bachelor of Arts."});

    // paths of length 2
    // L2_1 -> ?s ?p1 ?x1 . ?x1 ?p2 ?o .
    testConfigs.add(new Object[] {billGates, unitedStates, birthCountryPath,
        "Bill Gates' birth place is Washington and his country is United States."});
    testConfigs.add(new Object[] {billGates, washingtonState, birthSubPath,
        "Bill Gates' birth place is Seattle and its subdivision is Washington."});
    // L2_2 -> ?s ?p1 ?x1 . ?o ?p2 ?x1 .
    testConfigs.add(new Object[] {billGates, wardForrest, birthBirthPath,
        "Ward Forrest's birth place as well as Bill Gates' birth place is Seattle."});
    // L2_3 -> ?x1 ?p1 ?s . ?x1 ?p2 ?o .
    testConfigs.add(new Object[] {harvardUni, journalist, eduOccupationPath,
        "Wilbur Schramm's education is Harvard University and his occupation is Journalist.\n"
        + "Others with the same education as well as occupation are Eugene Robinson, Harold L. Humes, Hendrik Hertzberg, Neil Sheehan, Ravi Agrawal, Stephanie Flanders, Jeff Wise  Jeff Wise  1, David Binder, David Frum, David Ignatius, Jean- Daniel Flaysakier, John Fox Jr., John Jay Chapman, John Reed, Josh Barro, Komla Dumor, Kristen Welker, William Dietrich, Irin Carmon, Judith Matloff, Jacob Landau, Fareed Zakaria, Mary Louise Kelly, Ian Johnson, Richard Bernstein, Tangeni Amupadhi, Matthew Yglesias, Melissa Block, Michael Luo and Paula Broadwell."});
    // L2_4 -> ?x1 ?p1 ?s . ?o ?p2 ?x1 .
    testConfigs.add(new Object[] {obama, annDunham, childSpousePath,
        "Lolo Soetoro's child is Barack Obama and Ann Dunham's spouse is Lolo Soetoro.\n"
        + "Other with the same child is Barack Obama Sr.."});

    // paths of length 3
    // L3_1 -> ?s ?p1 ?x1 . ?x1 ?p2 ?x2 . ?x2 ?p3 ?o .
    testConfigs.add(new Object[] {obama, cambridge, spEdCityPath,
        "Barack Obama's spouse is Michelle Obama and her education is Harvard University. Harvard University's city is Massachusetts."});
    // L3_2 -> ?s ?p1 ?x1 . ?x2 ?p2 ?x1 . ?x2 ?p3 ?o .
    testConfigs.add(new Object[] {obama, oregonUni, birthAlmaPath,
        "David Cass' birth place as well as Barack Obama's birth place is Honolulu. David Cass' alma mater is University of Oregon.\n"
        + "Other with the same alma mater as well as birth place is Steve Alm."});
    testConfigs.add(new Object[] {tayZonday, minneapolis, birthDeathResidPath,
        "Martin Olav Sabo's death place as well as Tay Zonday's birth place is Minneapolis. Martin Olav Sabo's residence is Minneapolis.\n"
            + "Others with the same death place as well as residence are Mabeth Hurd Paige, Diane Loeffler, Edward J. Gearty, Arnold Fredrickson and Howard Davis."});
    // L3_3 -> ?x1 ?p1 ?s . ?x1 ?p2 ?x2 . ?x2 ?p3 ?o .
    testConfigs.add(new Object[] {obama, california, writerProdBirthPath,
        "Yes We Can's writer is Barack Obama and its producer is Will.i. am. Will.i. am's birth place is California."});
    // L3_4 -> ?x1 ?p1 ?s . ?x2 ?p2 ?x1 . ?x2 ?p3 ?o .
    testConfigs.add(new Object[] {obama, illinois, afterAlongBirthPath,
        "Peter Fitzgerald's after is Barack Obama and Dick Durbin's alongside is Peter Fitzgerald. Dick Durbin's birth place is Illinois."});
    // L3_5 -> ?s ?p1 ?x1 . ?x1 ?p2 ?x2 . ?o ?p3 ?x2 .
    testConfigs.add(new Object[] {obama, monsterMax, birthCountryHqPath,
        "Barack Obama's birth place is Honolulu and its country is United States. Monsterwax's hq location country is United States."});
    // L3_6 -> ?s ?p1 ?x1 . ?x2 ?p2 ?x1 . ?o ?p3 ?x2 .
    testConfigs.add(new Object[] {tayZonday, earthSuit, genreGenreAssociatePath,
        "Macrosick's genre as well as Tay Zonday's genre is Electronica. Earthsuit is associated musical artist Macrosick.\n"
            + "Other with the same genre is Mutemath."});
    // L3_7 -> ?x1 ?p1 ?s . ?x1 ?p2 ?x2 . ?o ?p3 ?x2 .
    testConfigs.add(new Object[] {harvardUni, barbara, almaBirthBirthPath,
        "Benjamin Boss' alma mater is Harvard University and his birth place is New York. Barbara Corday's birth place is New York.\n"
        + "Others with the same alma mater as well as birth place are Henry Harpending, Harry M. Rubin, Maxwell Rosenlicht, Divya Narendra, Junius Spencer Morgan III, Robert Walton Goelet, Francis Skiddy von Stade Sr., Hugh David Politzer, Robert Hessen, Mark E. Kingdon, Anson Burlingame, Robert Metcalfe, Robert Solow, Roger Mac Bride, Paul Sweezy, Richard Lewontin, Robert Caro, Sheldon Lee Glashow, Thomas Perkins, Jeff Cheeger, Kenward Elmslie, Lathrop Brown, Lawrence Spivak, Francine D. Blau, John Robert Cobb, Leo Hurwitz, Otto Maass, Abraham Klein, Stanley Hart White, Ed Redlich, Herbert Pell, Jeffrey A. Hoffman, Lisa Randall, Scott Weinger, Sheila Greibach, Bertrand Halperin, Bill Drayton, Jerome Karle, Lee S. Wolosky, Mildred Dresselhaus, Robert E. Simon, Theodore Roosevelt III, Arthur R. Miller, Glenn Slater, Nelson Horatio Darton, Robin Lakoff, Victor Niederhoffer, Henry H. Straight, Lucy Ozarin, Janellen Huttenlocher, Austen George Fox, Charles Bradford Isham, G. Hermann Kinnicutt, Stanley G. Mortimer Jr., Marvin Minsky, Ellery Sedgwick, Jeffrey P. Buzen, Peggy Mc Intosh, Stephen Joel Trachtenberg, Harry Wilson, John Austin Stevens, James J. Wynne, Jon Caramanica, Paul Goodman, Richard Wilbur, Benjamin Barber, Erich Segal, James Roosevelt, Allegra Goodman, Arnold Zellner, Chris Terrio, Leonard Parker, Warren Delano Robbins, William Starr Miller II, Anne Fadiman, Clifford Taubes, Cornelius V. S. Roosevelt, Eric Schneiderman, Frederic M. Richards, Faye- Ellen Silverman, Robert Livingston Gerry Sr., Walter Licht, Gideon Dreyfuss, Allan Lichtman, Heywood Broun, John C. Harkness, Marilyn French, George Emlen Roosevelt, Stephen Cook  Stephen Cook  1, Donald Kennedy, Leon Wieseltier, Amory Houghton, Edward Asahel Birge, Paul Dana, Ira M. Lapidus, Richard Pan, Schuyler Cammann, Hayley Barna, Vanessa Lann, David Harbater, Marshall Rosenbluth, Max Cantor, Melvin Hochster, Richard Stallman, Elliott Shepard, Edwin Vernon Morgan, William Earl Dodge Scott, Elaine Ostrander, Harvey P. Greenspan, Paul Bender, Barbara Engel, Mary Bowman, Stephen E. Levinson, William Bayard Cutting Jr., Orme Wilson Jr., Richard M. Friedberg, Arthur A. Houghton Jr., C. Suydam Cutting, Roland L. Redmond, Peter Tufano, Randy Haykin, Donald J. Newman, Irwin I. Shapiro, John C. Cort, Moses Abramovitz, Richard Arnowitt, Simon Rich, Theodore Douglas Robinson, Alicia Munnell, Charles Pence Slichter, Erez Lieberman Aiden, Melissa Lee, William Melvin Kelley, Charles S. Maier, Stephen Lichtenbaum, Edwin Chesley Estes Lord, Lawrence Fuchs, Phillip Steck, Brian, Alfred H. Conrad, Leo Goldberg, Stephen Barnett, William J. Clench, Jane Gerber, Kamala Shirin Lakhdhir, John D. Boice Jr., Robert P. Goldberg, George Bergman, Herbert E. Kaufman, A.B.C. Whipple, Blair Clark, David Bragdon, Ian Heath Gershengorn, Bowdoin B. Crowninshield, Marc Postman, Lincoln Gordon, Amy S. Bruckman, James M. Poterba, John J. Emery, Jonathan Kaufman, Brooks Hansen, Alexander Morgan Hamilton, Erinn Westbrook, Franco Mormando, J. Griswold Webb, Walter Tuckerman and Leslie T. Chang  Leslie T. Chang  1. \n"
        + "Other birth place is Brooklyn."});
    // L3_8 -> ?x1 ?p1 ?s . ?x2 ?p2 ?x1 . ?o ?p3 ?x2 .
    testConfigs.add(new Object[] {harvardUni, julius, almaMajorMovementPath,
        "Jack Levine trains Harvard University and American Figurative Expressionism's majorfigure is Jack Levine. Julius Hatofsky's movement is American Figurative Expressionism.\n"
        + "Others with the same training are Hyman Bloom and Robert Motherwell. \n"
        + "Other with the same majorfigures is Abstract expressionism."});
    // example with literal intermediate nodes
    testConfigs.add(new Object[] {tayZonday, minneapolis, backBackBirthPath,"Mark Olson's background as well as Tay Zonday's background is solo_singer. Mark Olson's birth place is Minneapolis.\n"
        + "Others with the same background as well as birth place are Mark Naftalin, P.O.S, Susie Allanson, John Wozniak, Paul Peterson, Sims, Paul Westerberg, Tommy Stinson, Shannon Selberg and Larry Verne."});
    testConfigs.add(new Object[] {tayZonday, minneapolis, starStarBirthPath,"Hamlet A.D.D. stars Tay Zonday and he stars Trace Beaulieu. Trace Beaulieu's birth place is Minneapolis."});
    
    return testConfigs;
  }

}
