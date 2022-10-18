package org.dice_research.fc.paths.verbalizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.triple2nl.TripleConverter;
import org.apache.commons.math3.util.Pair;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.dice_research.fc.data.IPieceOfEvidence;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.util.RDFUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Lists;
import simplenlg.features.Feature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

/**
 * Verbalizer that supports the verbalization of paths of length up to 3. Paths of length 3 are
 * decomposed into paths of length 2 and verbalized as 2+1 instead.
 * 
 * @author Alexandra Silva
 *
 */
public class MultiplePathVerbalizer extends DefaultPathVerbalizer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MultiplePathVerbalizer.class);

  private static TripleConverter converter;
  private NLGFactory nlgFactory;
  private Realiser realiser;

  // Variants of length 2
  private static final String L2_1 = "?s ?p1 ?x1 . ?x1 ?p2 ?o .";
  private static final String L2_2 = "?s ?p1 ?x1 . ?o ?p2 ?x1 .";
  private static final String L2_3 = "?x1 ?p1 ?s . ?x1 ?p2 ?o .";
  private static final String L2_4 = "?x1 ?p1 ?s . ?o ?p2 ?x1 .";

  // Variants of length 3
  private static final String L3_1 = "?s ?p1 ?x1 . ?x1 ?p2 ?x2 . ?x2 ?p3 ?o .";
  private static final String L3_2 = "?s ?p1 ?x1 . ?x2 ?p2 ?x1 . ?x2 ?p3 ?o .";
  private static final String L3_3 = "?x1 ?p1 ?s . ?x1 ?p2 ?x2 . ?x2 ?p3 ?o .";
  private static final String L3_4 = "?x1 ?p1 ?s . ?x2 ?p2 ?x1 . ?x2 ?p3 ?o .";
  private static final String L3_5 = "?s ?p1 ?x1 . ?x1 ?p2 ?x2 . ?o ?p3 ?x2 .";
  private static final String L3_6 = "?s ?p1 ?x1 . ?x2 ?p2 ?x1 . ?o ?p3 ?x2 .";
  private static final String L3_7 = "?x1 ?p1 ?s . ?x1 ?p2 ?x2 . ?o ?p3 ?x2 .";
  private static final String L3_8 = "?x1 ?p1 ?s . ?x2 ?p2 ?x1 . ?o ?p3 ?x2 .";


  private static final String OTHERS = "Other";
  private static final String SAME = "with the same";
  private static final String WELL_AS = "as well as";
  private static final String PLURAL_IS = " are";
  private static final String SING_IS = " is";
  private static final String PLURAL = "s";
  private static final String SPACE = " ";


  /**
   * Maps the Regex pattern to the corresponding variant
   */
  private static Map<String, String> variantMap;

  /**
   * Constructor.
   * 
   * @param qef QueryExecutionFactory for intermediate nodes querying and verbalization
   */
  public MultiplePathVerbalizer(QueryExecutionFactory qef) {
    super(qef);
    Lexicon lexicon = Lexicon.getDefaultLexicon();
    String cache = System.getProperty("java.io.tmpdir");
    converter = new TripleConverter(qef, cache + "/triple2nl-cache", lexicon);
    converter.setEncapsulateStringLiterals(false);
    nlgFactory = new NLGFactory(lexicon);
    realiser = new Realiser(lexicon);
    buildVariantMap();
  }

  @Override
  public String parseResults(String queryStr, int size) {

    // 1. Map intermediate nodes with respective variable name through query
    Set<Variable> vars = getIntermediateNodes(queryStr, size);
    List<Triple> stmts = new ArrayList<>();
    String[] items = queryStr.toString().trim().split("\\s+[.]\\s+");
    for (String curStmt : items) {
      if (curStmt.isEmpty()) {
        continue;
      }
      // transform string to triple, allows for variable id'ing
      Triple stmt = RDFUtil.getTripleFromString(curStmt);
      stmts.add(stmt);
    }

    // 2. identify path variant and process accordingly
    String variant = identifyVariant(queryStr);
    vars.forEach(k -> assignPredicate(k, stmts));
    String parsedOutput;
    try {
      parsedOutput = processVariant(variant, stmts, vars);
    } catch (VerbalizerException e) {
      e.printStackTrace();
      return null;
    }
    StringBuilder resultBuilder = new StringBuilder();
    resultBuilder.append(parsedOutput).append("\n");

    // 3. Process the rest of the possibilities as alternatives
    if (size > 1) {

      // iterate variable names and build sentence
      for (Variable curVar : vars) {
        LinkedHashSet<Node> values = curVar.getValues();
        Set<Node> predicates = curVar.getPredicates();
        boolean isPlural = false;

        // remove the representative from the set
        values.removeIf(k -> {
          if (curVar.isSubject()) {
            return stmts.stream().anyMatch(
                n -> n.getMatchSubject().matches(k) && curVar.containsPredicate(n.getPredicate()));
          } else {
            return stmts.stream().anyMatch(
                n -> n.getMatchObject().matches(k) && curVar.containsPredicate(n.getPredicate()));
          }
        });
        if (values.size() == 0) {
          continue;
        }
        if (values.size() > 1) {
          isPlural = true;
        }

        StringBuilder verbalizedOps = new StringBuilder();
        verbalizedOps.append(OTHERS);
        // check if variable is subject or object to determine wording
        boolean isSubject = curVar.isSubject();

        // "Others with the same $predicate"
        if (isSubject) {
          if (isPlural) {
            verbalizedOps.append(PLURAL);
          }
          verbalizedOps.append(SPACE).append(SAME).append(SPACE);
        } else {
          verbalizedOps.append(SPACE);
        }

        // get the predicates and concatenate them if needed
        int j = 0;
        for (Node curPred : predicates) {
          NLGElement t = converter.processNode(curPred);
          String verbPred = ((WordElement) t.getFeature("head")).getBaseForm().toLowerCase();
          verbalizedOps.append(verbPred);
          if (++j < predicates.size() - 1) {
            verbalizedOps.append(",");
          } else if (j < predicates.size()) {
            verbalizedOps.append(SPACE).append(WELL_AS).append(SPACE);
          }
        }
        if (!isSubject && isPlural) {
          verbalizedOps.append(PLURAL);
        }

        // Add is/are
        if (isPlural) {
          verbalizedOps.append(PLURAL_IS);
        } else {
          verbalizedOps.append(SING_IS);
        }

        // concatenate resources
        int count = 0;
        for (Node curVal : values) {
          NLGElement t = converter.processNode(curVal);
          String word = ((WordElement) t.getFeature("head")).getBaseForm();
          verbalizedOps.append(SPACE).append(word);
          if (++count < values.size() - 1) {
            verbalizedOps.append(",");
          } else if (count < values.size()) {
            verbalizedOps.append(" and");
          } else {
            verbalizedOps.append(". ");
          }
        }
        resultBuilder.append(verbalizedOps.toString()).append("\n");
      }
    }
    return resultBuilder.toString().trim();
  }

  /**
   * Assigns the variable's representative predicate. Not necessarily the predicates that had a
   * connection to it.
   * 
   * @param k
   * @param stmts
   * @return
   */
  private void assignPredicate(Variable k, List<Triple> stmts) {
    int pathSize = stmts.size();
    List<Triple> commonTriples = stmts;

    // get triples with intermediate nodes
    if (pathSize == 3) {
      commonTriples = getTriplesWithVar(stmts, k.getName());
      if (commonTriples.size() != 2) {
        LOGGER.error(
            "For path length 3, there should always be 2 triples for each intermediate node.");
      }
    }
    Triple phrase1 = commonTriples.get(0);
    Triple phrase2 = commonTriples.get(1);

    boolean isSubjectSame = phrase1.getSubject().equals(phrase2.getSubject());
    boolean isObjectSame = phrase1.getObject().equals(phrase2.getObject());

    // concatenate if subjects or objects are the same
    if (isSubjectSame) {
      k.setPredicates(commonTriples.stream().map(Triple::getPredicate).collect(Collectors.toSet()));
      k.setSubject();
    } else if (isObjectSame) {
      k.setPredicates(commonTriples.stream().map(Triple::getPredicate).collect(Collectors.toSet()));
      k.setObject();
    } else {
      // else default to 1st phrase's predicate
      k.addPredicate(phrase1.getPredicate());
      if (phrase1.getSubject().isVariable() && phrase1.getSubject().getName().equals(k.getName())) {
        k.setSubject();
      } else {
        k.setObject();
      }
    }
  }

  /**
   * 
   * @param stmts
   * @param varName
   * @return The sublist of statements that have the variable as either subject or object.
   */
  private List<Triple> getTriplesWithVar(List<Triple> stmts, String varName) {
    List<Triple> subStmts = new ArrayList<>();
    Node var = NodeFactory.createVariable(varName);
    for (Triple triple : stmts) {
      if (triple.getSubject().matches(var) || triple.getObject().matches(var)) {
        subStmts.add(triple);
      }
    }
    return subStmts;
  }


  /**
   * Verbalizes the triples according to the identified variant
   * 
   * @param variant The identified variant
   * @param stmts The path statements
   * @param vars The variables present in the paths
   * @return The verbalization output
   * @throws VerbalizerException 
   */
  private String processVariant(String variant, List<Triple> triples, Set<Variable> vars) throws VerbalizerException {

    // return default behaviour for unsupported path lengths
    // List<Triple> triples = new ArrayList<>(stmts);
    int pathLength = triples.size();
    if (pathLength < 2 || pathLength > 3) {
      return converter.convert(triples);
    }

    // if there's no variable mapping, verbalization cannot be processed
    if (vars.isEmpty()) {
      throw new VerbalizerException(
          "Verbalization not found as no intermediate nodes were found for the meta-path.");
    }

    // replace with first intermediate node found
    for (int i = 0; i < triples.size(); i++) {
      Triple curTriple = triples.get(i);
      boolean isSubjVar = curTriple.getSubject().isVariable();
      boolean isObjVar = curTriple.getObject().isVariable();

      if (!isSubjVar && !isObjVar) {
        throw new VerbalizerException("One of the nodes must be a variable.");
      }

      // replace variables in all triples
      for (Variable key : vars) {
        String varName = key.getName();
        LinkedHashSet<Node> values = key.getValues();
        if (values != null && !values.isEmpty()) {
          Node interNode = values.iterator().next();

          // replace subject if variable is there
          if (isSubjVar && curTriple.getSubject().getName().equals(varName)) {
            triples.set(i, new Triple(interNode, curTriple.getPredicate(), curTriple.getObject()));
          }
          // replace object if variable is there
          if (isObjVar && curTriple.getObject().getName().equals(varName)) {
            triples.set(i, new Triple(curTriple.getSubject(), curTriple.getPredicate(), interNode));
          }

          // reassign, might have been changed
          curTriple = triples.get(i);
          isSubjVar = curTriple.getSubject().isVariable();
          isObjVar = curTriple.getObject().isVariable();
        } else {
          throw new VerbalizerException("Could not identify variables in the query.");
        }
      }
    }

    // process paths of length 2
    if (pathLength == 2) {
      switch (variant) {
        case L2_1:
          // <> <> ?x1 . ?x1 <> <> .
          return converter.convert(triples);
        case L2_2:
          // <> <> ?x1 . <> <> ?x1 .
          return processConjunctions(triples);
        case L2_3:
          // ?x1 <> <> . ?x1 <> <> .
          return converter.convert(triples);
        case L2_4:
          // ?x1 <> <> . <> <> ?x1 .
          // pronouns cannot be used in this case as the resulting
          // sentence becomes confusing
          return convertOtherTriples(triples);
        default:
          return converter.convert(triples);
      }
    }

    // process paths of length 3
    StringBuilder builder = new StringBuilder();
    if (pathLength == 3) {
      // get first 2 sentences
      List<Triple> subTriples = triples.subList(0, 2);
      if (variant.equals(L3_1) || variant.equals(L3_5) || variant.equals(L3_3)
          || variant.equals(L3_7)) {
        // subtriples now fall under <> <> ?x1 . ?x1 <> <>
        // or ?x1 <> <> . ?x1 <> <>
        builder.append(converter.convert(subTriples));
      } else if (variant.equals(L3_2) || variant.equals(L3_6)) {
        // subtriples now fall under <> <> ?x1 . <> <> ?x1
        builder.append(processConjunctions(subTriples));
      } else if (variant.equals(L3_4) || variant.equals(L3_8)) {
        // subtriples now fall under ?x1 <> <> . <> <> ?x1
        builder.append(convertOtherTriples(subTriples));
      } else {
        // default
        builder.append(converter.convert(subTriples));
      }
      builder.append(SPACE).append(converter.convert(triples.get(2)));
    }
    return builder.toString();
  }

  /**
   * Verbalizes triples with an and conjunction and no pronouns.
   * 
   * @param triples The path's triples
   * @return The verbalized output
   */
  protected String convertOtherTriples(List<Triple> triples) {
    CoordinatedPhraseElement othersConjunction = nlgFactory.createCoordinatedPhrase();
    List<SPhraseSpec> otherPhrases = converter.convertToPhrases(triples);
    othersConjunction.addCoordinate(otherPhrases.remove(0));
    for (SPhraseSpec phrase : otherPhrases) {
      othersConjunction.addCoordinate(phrase);
    }
    List<DocumentElement> sentences = new ArrayList<>();
    sentences.add(nlgFactory.createSentence(othersConjunction));

    DocumentElement paragraph = nlgFactory.createParagraph(sentences);
    String realisation = realiser.realise(paragraph).getRealisation().trim();

    return realisation;
  }


  /**
   * Merges the sentences based on same object node with "as well as". Works for any path length.
   * 
   * @param triples The path's triples.
   * @return The verbalized output.
   */
  protected String processConjunctions(List<Triple> triples) {
    // create sentence
    CoordinatedPhraseElement sameObjConjunction = nlgFactory.createCoordinatedPhrase();

    // convert to phrases
    List<SPhraseSpec> sameObjPhrases = converter.convertToPhrases(triples);
    if (sameObjPhrases.size() > 1) {
      CoordinatedPhraseElement combinedObject = nlgFactory.createCoordinatedPhrase();

      // combine last 2 with as well as
      SPhraseSpec phrase1 = sameObjPhrases.remove(sameObjPhrases.size() - 1);
      SPhraseSpec phrase2 = sameObjPhrases.get(sameObjPhrases.size() - 1);

      // combine subjects
      CoordinatedPhraseElement combinedLastTwo = null;
      combinedLastTwo =
          nlgFactory.createCoordinatedPhrase(phrase1.getSubject(), phrase2.getSubject());
      phrase2.setSubject(combinedLastTwo);
      combinedLastTwo.setConjunction("as well as");
      combinedLastTwo.setFeature(Feature.RAISE_SPECIFIER, false);

      // pick first phrase as representative
      // add the rest of the phrases with and conjunction
      Iterator<SPhraseSpec> iterator = sameObjPhrases.iterator();
      SPhraseSpec representative = iterator.next();
      combinedObject.addCoordinate(representative.getObject());
      while (iterator.hasNext()) {
        SPhraseSpec phrase = iterator.next();
        NLGElement node = null;
        node = phrase.getSubject();
        combinedObject.addCoordinate(node);
      }

      combinedObject.setFeature(Feature.RAISE_SPECIFIER, true);
      // set the coordinated phrase as the object
      representative.setObject(combinedObject);
      // return a single phrase
      sameObjPhrases = Lists.newArrayList(representative);
    }

    for (SPhraseSpec phrase : sameObjPhrases) {
      sameObjConjunction.addCoordinate(phrase);
    }

    List<DocumentElement> sentences = new ArrayList<>();
    if (!sameObjPhrases.isEmpty()) {
      sentences.add(nlgFactory.createSentence(sameObjConjunction));
    }
    DocumentElement paragraph = nlgFactory.createParagraph(sentences);
    String realisation = realiser.realise(paragraph).getRealisation().trim();
    return realisation;
  }



  /**
   * 
   * @param triples The path's triples.
   * @return True if all the triples have the same predicate
   */
  private boolean arePredicatesSame(List<Triple> triples) {
    return triples.stream().allMatch(k -> triples.get(0).getPredicate().equals(k.getPredicate()));
  }

  /**
   * 
   * @param triples The path's triples.
   * @return if all the triples have the same object
   */
  private boolean areObjectsSame(List<Triple> triples) {
    return triples.stream().allMatch(k -> triples.get(0).getObject().equals(k.getObject()));
  }

  /**
   * 
   * @param triples The path's triples.
   * @return if all the triples have the same subject
   */
  private boolean areSubjectsSame(List<Triple> triples) {
    return triples.stream().allMatch(k -> triples.get(0).getSubject().equals(k.getSubject()));
  }

  /**
   * Returns the most common Field with a given function, for example Triple::getSubject would
   * retrieve the most common subject in the triples.
   * 
   * @param triples The path's triples.
   * @param function The function.
   * @return Most common X.
   */
  private Node getMostCommonXTriples(List<Triple> triples, Function<Triple, Node> function) {
    return triples.stream().collect(Collectors.groupingBy(function, Collectors.counting()))
        .entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
  }

  /**
   * Matches the query against all supported patterns (up to length 3)
   * 
   * @param query
   * @return Variant it identified
   */
  private String identifyVariant(String query) {
    // identify variant
    String variant = "";
    for (String pattern : variantMap.keySet()) {
      if (query.trim().matches(pattern)) {
        return variantMap.get(pattern);
      }
    }
    return variant;
  }

  /**
   * Retrieves the intermediate nodes and verbalizes the path.
   */
  public String verbalizeMetaPath(Resource subject, Resource object, IPieceOfEvidence path) {
    StringBuilder builder = new StringBuilder();

    // initialize as if there was a previous path stretch
    String stretchStart = null;
    String stretchEnd = RDFUtil.format(subject);

    List<Pair<Property, Boolean>> pathElements =
        QRestrictedPath.create(path.getEvidence(), path.getScore()).getPathElements();

    for (int i = 0; i < pathElements.size(); i++) {
      Pair<Property, Boolean> pathStretch = pathElements.get(i);

      // new start is the old end, new end is updated
      stretchStart = stretchEnd;
      if (i == pathElements.size() - 1) {
        stretchEnd = RDFUtil.format(object);
      } else {
        stretchEnd = INTERMEDIATE_VAR + i;
      }

      // build string
      if (pathStretch.getSecond()) {
        builder.append(stretchStart);
        builder.append(RDFUtil.format(pathStretch.getFirst()));
        builder.append(stretchEnd);
      } else {
        builder.append(stretchEnd);
        builder.append(RDFUtil.format(pathStretch.getFirst()));
        builder.append(stretchStart);
      }
      builder.append(" . ");

    }
    return parseResults(builder.toString(), pathElements.size());
  }

  /**
   * Retrieves the intermediate nodes.
   * 
   * @param queryStr The query string
   * @param size The path size
   * @return The intermediate nodes
   */
  protected Set<Variable> getIntermediateNodes(String queryStr, int size) {
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT * WHERE {");
    builder.append(queryStr);
    builder.append("}");

    Query query = QueryFactory.create(builder.toString());
    Set<Variable> variableSet = new HashSet<>();
    try (QueryExecution queryExecution = qef.createQueryExecution(query)) {
      ResultSet resultSet = queryExecution.execSelect();
      while (resultSet.hasNext()) {
        QuerySolution curSol = resultSet.next();
        Iterator<String> varNames = curSol.varNames();
        while (varNames.hasNext()) {
          String curVarName = varNames.next();
          if (curVarName.equals("_star_fake")) {
            continue;
          }
          Variable newVar = new Variable(curVarName);
          newVar.addValue(curSol.get(curVarName).asNode());
          if (!variableSet.add(newVar)) {
            variableSet.forEach(k -> {
              if (k.equals(newVar)) {
                k.addValues(newVar);
                return;
              }
            });
          }
        }
      }
    }
    return variableSet;
  }

  private void buildVariantMap() {
    variantMap = new HashMap<String, String>();
    variantMap.put(
        "(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+(\\?x\\d)\\s+\\.\\s+(\\?x\\d)\\s(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+\\.",
        L2_1);
    variantMap.put(
        "(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+(\\?x\\d)\\s+\\.\\s+(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+(\\?x\\d)\\s+\\.",
        L2_2);
    variantMap.put(
        "(\\?x\\d)\\s+(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+\\.\\s+(\\?x\\d)\\s+(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+\\.",
        L2_3);
    variantMap.put(
        "(\\?x\\d)\\s+(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+\\.\\s+(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+(\\?x\\d)\\s+\\.",
        L2_4);
    variantMap.put(
        "(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+(\\?x0)\\s+\\.\\s+(\\?x0)\\s+(<[^<>]+?>)\\s+(\\?x1)\\s+\\.\\s+(\\?x1)\\s+(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+\\.",
        L3_1);
    variantMap.put(
        "(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+(\\?x0)\\s+\\.\\s+(\\?x1)\\s+(<[^<>]+?>)\\s+(\\?x0)\\s+\\.\\s+(\\?x1)\\s+(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+\\.",
        L3_2);
    variantMap.put(
        "(\\?x0)\\s+(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+\\.\\s+(\\?x0)\\s+(<[^<>]+?>)\\s+(\\?x1)\\s+\\.\\s+(\\?x1)\\s+(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+\\.",
        L3_3);
    variantMap.put(
        "(\\?x0)\\s+(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+\\.\\s+(\\?x1)\\s+(<[^<>]+?>)\\s+(\\?x0)\\s+\\.\\s+(\\?x1)\\s+(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+\\.",
        L3_4);
    variantMap.put(
        "(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+(\\?x0)\\s+\\.\\s+(\\?x0)\\s+(<[^<>]+?>)\\s+(\\?x1)\\s+\\.\\s+(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+(\\?x1)\\s+\\.",
        L3_5);
    variantMap.put(
        "(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+(\\?x0)\\s+\\.\\s+(\\?x1)\\s+(<[^<>]+?>)\\s+(\\?x0)\\s+\\.\\s+(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+(\\?x1)\\s+\\.",
        L3_6);
    variantMap.put(
        "(\\?x0)\\s+(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+\\.\\s+(\\?x0)\\s+(<[^<>]+?>)\\s+(\\?x1)\\s+\\.\\s+(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+(\\?x1)\\s+\\.",
        L3_7);
    variantMap.put(
        "(\\?x0)\\s+(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+\\.\\s+(\\?x1)\\s+(<[^<>]+?>)\\s+(\\?x0)\\s+\\.\\s+(<[^<>]+?>)\\s+(<[^<>]+?>)\\s+(\\?x1)\\s+\\.",
        L3_8);
  }

}
