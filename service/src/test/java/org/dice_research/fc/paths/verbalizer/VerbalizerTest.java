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

    private final QueryExecutionFactory qef =
            new QueryExecutionFactoryCustomHttp("https://dbpedia.org/sparql");


    public VerbalizerTest(Resource subject, Resource object, QRestrictedPath path) {
        this.path = path;
        this.subject = subject;
        this.object = object;
    }

    @Test
    public void testVerbalization() {
        IPathVerbalizer verbalizer = new MultiplePathVerbalizer(qef);
        String output = verbalizer.verbalizePaths(subject, object, path);
        Assert.assertNotNull(output);
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
        String genreGenreAssociate =
                "<http://dbpedia.org/ontology/genre>/^<http://dbpedia.org/ontology/genre>/^<http://dbpedia.org/ontology/associatedMusicalArtist>";
        String almaBirthBirth =
                "^<http://dbpedia.org/ontology/almaMater>/<http://dbpedia.org/ontology/birthPlace>/^<http://dbpedia.org/ontology/birthPlace>";
        String almaMajorMovement =
                "^<http://dbpedia.org/property/training>/^<http://dbpedia.org/property/majorfigures>/^<http://dbpedia.org/property/movement>";
        String backBackBirth =
                "<http://dbpedia.org/ontology/background>/^<http://dbpedia.org/ontology/background>/<http://dbpedia.org/ontology/birthPlace>";
        String starStarBirth =
                "^<http://dbpedia.org/ontology/starring>/<http://dbpedia.org/ontology/starring>/<http://dbpedia.org/ontology/birthPlace>";

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
        QRestrictedPath genreGenreAssociatePath = QRestrictedPath.create(genreGenreAssociate, 0);
        QRestrictedPath almaBirthBirthPath = QRestrictedPath.create(almaBirthBirth, 0);
        QRestrictedPath almaMajorMovementPath = QRestrictedPath.create(almaMajorMovement, 0);
        QRestrictedPath backBackBirthPath = QRestrictedPath.create(backBackBirth, 0);
        QRestrictedPath starStarBirthPath = QRestrictedPath.create(starStarBirth, 0);

        // paths of length 1
        // "Nia Gill's alma mater is Bachelor of Arts."
        testConfigs.add(new Object[] {niaGill, bachelorArts, almaPathPath});

        // paths of length 2
        // L2_1 -> ?s ?p1 ?x1 . ?x1 ?p2 ?o .
        // "Bill Gates' birth place is Washington (state) and his country is United States."
        testConfigs.add(new Object[] {billGates, unitedStates, birthCountryPath});
        // "Bill Gates' birth place is Seattle and its subdivision is Washington (state)."
        testConfigs.add(new Object[] {billGates, washingtonState, birthSubPath});
        // L2_2 -> ?s ?p1 ?x1 . ?o ?p2 ?x1 .
        // "Ward Forrest's birth place as well as Bill Gates' birth place is Seattle."
        testConfigs.add(new Object[] {billGates, wardForrest, birthBirthPath});
        // L2_3 -> ?x1 ?p1 ?s . ?x1 ?p2 ?o .
        // "Wilbur Schramm's education is Harvard University and his occupation is Journalist.\n" +
        // "Others with the same education as well as occupation are Eugene Robinson (journalist),
        // Harold L. Humes, Hendrik Hertzberg, Neil Sheehan, Ravi Agrawal, Stephanie Flanders, Jeff Wise
        // Jeff Wise 1, David Binder (journalist), David Frum, David Ignatius, Jean-Daniel Flaysakier,
        // John Fox Jr., John Jay Chapman, John Reed (journalist), Josh Barro, Komla Dumor, Kristen
        // Welker, William Dietrich (novelist), Irin Carmon, Judith Matloff, Jacob Landau (journalist),
        // Fareed Zakaria, Mary Louise Kelly, Ian Johnson (writer), Richard Bernstein, Tangeni Amupadhi,
        // Matthew Yglesias, Melissa Block, Michael Luo and Paula Broadwell."
        testConfigs.add(new Object[] {harvardUni, journalist, eduOccupationPath});
        // L2_4 -> ?x1 ?p1 ?s . ?o ?p2 ?x1 .
        // "Barack Obama Sr.'s child is Barack Obama and Ann Dunham's spouse is Barack Obama Sr.\n" +
        // "Other with the same child is Lolo Soetoro."
        testConfigs.add(new Object[] {obama, annDunham, childSpousePath});

        // paths of length 3
        // L3_1 -> ?s ?p1 ?x1 . ?x1 ?p2 ?x2 . ?x2 ?p3 ?o .
        // "Barack Obama's spouse is Michelle Obama and her education is Harvard University. Harvard
        // University's city is Cambridge, Massachusetts."
        testConfigs.add(new Object[] {obama, cambridge, spEdCityPath});
        // L3_2 -> ?s ?p1 ?x1 . ?x2 ?p2 ?x1 . ?x2 ?p3 ?o .
        // "David Cass' birth place as well as Barack Obama's birth place is Honolulu. David Cass' alma
        // mater is University of Oregon.\n"
        // "Other with the same alma mater as well as birth place is Steve Alm."
        testConfigs.add(new Object[] {obama, oregonUni, birthAlmaPath});
        // "Martin Olav Sabo's death place as well as Tay Zonday's birth place is Minneapolis. Martin
        // Olav Sabo's residence is Minneapolis.\n" +
        // "Others with the same death place as well as residence are Mabeth Hurd Paige, Diane Loeffler,
        // Edward J. Gearty, Arnold Fredrickson and Howard Davis (chemical engineer)."
        testConfigs.add(new Object[] {tayZonday, minneapolis, birthDeathResidPath});
        // L3_3 -> ?x1 ?p1 ?s . ?x1 ?p2 ?x2 . ?x2 ?p3 ?o .
        // "We Are the Ones' auteur is Barack Obama and its producer is Will.i.am. Will.i.am's birth
        // place is California.\n" +
        // "Other with the same auteur as well as producer is Yes We Can (will.i.am song)."
        testConfigs.add(new Object[] {obama, california, writerProdBirthPath});
        // L3_4 -> ?x1 ?p1 ?s . ?x2 ?p2 ?x1 . ?x2 ?p3 ?o .
        // "Peter Fitzgerald (politician)'s after is Barack Obama and Dick Durbin's alongside is Peter
        // Fitzgerald (politician). Dick Durbin's birth place is East St. Louis, Illinois."
        testConfigs.add(new Object[] {obama, illinois, afterAlongBirthPath});
        // L3_5 -> ?s ?p1 ?x1 . ?x1 ?p2 ?x2 . ?o ?p3 ?x2 .
        // L3_6 -> ?s ?p1 ?x1 . ?x2 ?p2 ?x1 . ?o ?p3 ?x2 .
        // "Macrosick's genre as well as Tay Zonday's genre is Electronica. Earthsuit is associated
        // musical artist Macrosick.\n"
        // + "Other with the same genre is Mutemath."
        testConfigs.add(new Object[] {tayZonday, earthSuit, genreGenreAssociatePath});
        // L3_7 -> ?x1 ?p1 ?s . ?x1 ?p2 ?x2 . ?o ?p3 ?x2 .
        // "A.B.C. Whipple's alma mater is Harvard University and its birth place is New York (state).
        // Barbara Corday's birth place is New York (state).\n" +
        // "Others with the same alma mater as well as birth place are Walter Licht, Walter Tuckerman,
        // Erez Lieberman Aiden, Eric Schneiderman, Alexander Morgan Hamilton, Alicia Munnell, Allan
        // Lichtman, August Heckscher II, Austen George Fox, Divya Narendra, Donald J. Newman, Donald
        // Kennedy, Schuyler Cammann, Scott Weinger, Peter Tufano, Abbot Low Moffat, Edward Asahel
        // Birge, Edwin Vernon Morgan, Elaine Ostrander, Ellery Sedgwick, Elliott Shepard, Harry M.
        // Rubin, Harry Wilson (businessman), Lee S. Wolosky, Leo Hurwitz, Leonard Parker, Lincoln
        // Gordon, Lisa Randall, Stuart Beck, Gideon Dreyfuss, Blair Clark, Henry Harpending, Herbert
        // Pell, Phillip Steck, Stanley G. Mortimer Jr., Stephen E. Levinson, Stephen Joel Trachtenberg,
        // Ed Redlich, Anson Burlingame, Arthur A. Houghton Jr., Arthur R. Miller, Stephen Cook Stephen
        // Cook 1, Brooks Hansen, C. Suydam Cutting, Clifford Taubes, Cornelius V. S. Roosevelt, David
        // Bragdon, David Harbater, Jeffrey A. Hoffman, John Austin Stevens, John C. Cort, John C.
        // Harkness, John D. Boice Jr., John J. Emery, John Lord O'Brian, Lathrop Brown, Lawrence Fuchs,
        // William Bayard Cutting Jr., William Melvin Kelley, William Starr Miller II, George Emlen
        // Roosevelt, Robert Hessen, Roger MacBride, Roland L. Redmond, Hayley Barna, Allegra Goodman,
        // Amory Houghton, Amy S. Bruckman, Benjamin Barber, Benjamin Boss, Bill Drayton, Charles
        // Bradford Isham, Charles Pence Slichter, Charles S. Maier, Chris Terrio, Francine D. Blau,
        // Francis Skiddy von Stade Sr., Franco Mormando, Frederic M. Richards, Junius Spencer Morgan
        // III, Barbara Engel (historian), J. Griswold Webb, James M. Poterba, James Roosevelt, Janellen
        // Huttenlocher, Sheila Greibach, Sheldon Glashow, Theodore Douglas Robinson, Theodore Roosevelt
        // III, Thomas Perkins (businessman), Faye-Ellen Silverman, Marshall Rosenbluth, Marvin Minsky,
        // Mary Bowman, Hugh David Politzer, Ian Heath Gershengorn, Marc Postman, Richard Arnowitt,
        // Richard Lewontin, Richard M. Friedberg, Richard Pan, Richard Stallman, Richard Wilbur, Robert
        // E. Simon, Robert Livingston Gerry Sr., Robert Solow, Robert Walton Goelet, Simon Rich, Randy
        // Haykin, Erinn Westbrook, Kamala Shirin Lakhdhir, Orme Wilson Jr., Otto Maass, Victor
        // Niederhoffer, Leslie T. Chang Leslie T. Chang 1, Glenn Slater, Irwin I. Shapiro, Kenward
        // Elmslie, Moses Abramovitz, Warren Delano Robbins, Erich Segal, Alfred H. Conrad, Max Cantor,
        // Melissa Lee (journalist), Melvin Hochster, Paul Dana (journalist), Paul Sweezy, Jonathan
        // Kaufman, George Bergman, Abraham Klein (physicist), Edwin Chesley Estes Lord, Harvey P.
        // Greenspan, Leo Goldberg, Leon Wieseltier, Herbert E. Kaufman, Heywood Broun, Lucy Ozarin,
        // Nelson Horatio Darton, Stanley Hart White, Stephen Barnett, Stephen Lichtenbaum, Arnold
        // Zellner, Vanessa Lann, Jeff Cheeger, Jeffrey P. Buzen, John Robert Cobb, Jon Caramanica,
        // Lawrence Spivak, William Earl Dodge Scott, William J. Clench, Mark E. Kingdon, Bertrand
        // Halperin, James J. Wynne, Jane Gerber, Marilyn French, Robert Metcalfe, Robert P. Goldberg,
        // Robin Lakoff, Ira M. Lapidus, Maxwell Rosenlicht, Mildred Dresselhaus, Paul Bender (jurist)
        // and Paul Goodman (historian). \n" +
        // "Other birth place is Brooklyn."
        testConfigs.add(new Object[] {harvardUni, barbara, almaBirthBirthPath});
        // L3_8 -> ?x1 ?p1 ?s . ?x2 ?p2 ?x1 . ?o ?p3 ?x2 .
        // "Jack Levine trains Harvard University and American Figurative Expressionism's majorfigure is
        // Jack Levine. Julius Hatofsky's movement is American Figurative Expressionism.\n"
        // + "Others with the same training are Hyman Bloom and Robert Motherwell. \n"
        // + "Other with the same majorfigures is Abstract expressionism."
        testConfigs.add(new Object[] {harvardUni, julius, almaMajorMovementPath});
        // example with literal intermediate nodes
        // "Mark Olson (musician)'s background as well as Tay Zonday's background is solo_singer. Mark
        // Olson (musician)'s birth place is Minneapolis.\n" +
        // "Others with the same background as well as birth place are Mark Naftalin, P.O.S (rapper),
        // Susie Allanson, John Wozniak, Paul Peterson, Sims (rapper), Paul Westerberg, Tommy Stinson,
        // Shannon Selberg and Larry Verne."
        testConfigs.add(new Object[] {tayZonday, minneapolis, backBackBirthPath});
        // "Hamlet A.D.D. stars Tay Zonday and he stars Trace Beaulieu. Trace Beaulieu's birth place is
        // Minneapolis."
        testConfigs.add(new Object[] {tayZonday, minneapolis, starStarBirthPath});

        return testConfigs;
    }

}
