package org.dice_research.fc.paths.search;

import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.dice_research.fc.IMapper;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.IPathSearcher;
import org.dice_research.fc.paths.AbstractPathSearcherDecorator;
import org.dice_research.fc.paths.model.Path;
import org.dice_research.fc.paths.model.PathElement;
import org.dice_research.fc.paths.repository.IPathElementRepository;
import org.dice_research.fc.paths.repository.IPathRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is a decorator fot PathSearcher, for searching the paths at first try to find them in DB
 * if find then return them
 * if not use the actual search part.
 *
 * @author Farshad Afshari
 *
 */

public class CachingPathSearcherDecorator extends AbstractPathSearcherDecorator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachingPathSearcherDecorator.class);

    @Autowired
    protected IPathRepository pathRepository;

    @Autowired
    protected IPathElementRepository pathElementRepository;

    /**
     * the mappr for map from Path to QRestrictedPath
     */
    protected IMapper<Path, QRestrictedPath> mapper;
    /**
     * the mappr for map from Pair<Property, Boolean> to PathElement
     */
    protected IMapper<Pair<Property, Boolean>, PathElement> propertyElementMapper;
    /**
     * the name of the class of counterRetriever
     */
    protected String counterRetriever;
    /**
     * the name of the class of factPreprocessor
     */
    protected String factPreprocessor;
    /**
     * the name of the class of pathSearcher this is private because it is itself and we name it on constructor
     */
    private String pathSearcher;
    /**
     * the name of the class of pathScorer
     */
    protected String pathScorer;

    public CachingPathSearcherDecorator(IPathSearcher decoratedPathSearcher,
                                        IMapper<Path, QRestrictedPath> mapper,
                                        IMapper<Pair<Property, Boolean>, PathElement> propertyElementMapper,
                                        String counterRetriever,
                                        String factPreprocessor,
                                        String pathScorer
                                                      ) {
        super(decoratedPathSearcher);
        this.mapper = mapper;
        this.propertyElementMapper = propertyElementMapper;
        this.counterRetriever = counterRetriever;
        this.factPreprocessor = factPreprocessor;
        this.pathSearcher = this.getClass().getName();
        this.pathScorer = pathScorer;
    }

    @Override
    public Collection<QRestrictedPath> search(Resource subject, Predicate predicate, Resource object){
        LOGGER.info("use CachingPathSearcherDecorator for path search");
        Collection<QRestrictedPath> paths = retrievePathsFromDB(subject,predicate.getProperty(),object,factPreprocessor,counterRetriever,pathSearcher,pathScorer);
        LOGGER.info("there was "+paths.size()+" path in db");
        if(paths.size()==0){
            paths = decorator.search(subject,predicate,object);
            //save paths
            LOGGER.info("found "+paths.size()+ " paths and save them in db");
            savePaths(paths,subject,predicate.getProperty(),object,factPreprocessor,counterRetriever,pathSearcher,pathScorer);
        }

        return paths;
    }

    /**
     * retrive the path from DB
     * @return  a list of saved path
     * @param subject is a fact subject
     * @param predicate ia a fact predicate
     * @param object is a fact predicate
     * @param factPreprocessor shows which factPreprocessor used
     * @param counterRetriever shows which counterRetriever used
     * @param pathSearcher shows which pathSearcher used
     * @param pathScorer shows which pathScorer used
     */
    private List<QRestrictedPath> retrievePathsFromDB(Resource subject, Property predicate, Resource object, String factPreprocessor, String counterRetriever, String pathSearcher, String pathScorer) {
        List<Path> paths = pathRepository.findBySubjectAndPredicateAndObjectAndFactPreprocessorAndCounterRetrieverAndPathSearcherAndPathScorer(subject.getURI(),predicate.getURI(),object.getURI(),factPreprocessor,counterRetriever,pathSearcher,pathScorer);
        List<QRestrictedPath> returnVal = paths.stream().map(p -> mapper.map(p)).collect(Collectors.toList());
        return returnVal;
    }

    /**
     * save the path in DB
     * @param paths is a list of calculated path which we want to store
     * @param subject is a fact subject
     * @param predicate ia a fact predicate
     * @param object is a fact predicate
     * @param factPreprocessorClassName shows which factPreprocessor used
     * @param counterRetrieverClassName shows which counterRetriever used
     * @param pathSearcherClassName shows which pathSearcher used
     * @param pathScorerClassName shows which pathScorer used
     */
    private void savePaths(Collection<QRestrictedPath> paths, Resource subject, Property predicate, Resource object, String factPreprocessorClassName,String counterRetrieverClassName, String pathSearcherClassName, String pathScorerClassName) {
        for (QRestrictedPath p: paths) {
                // make a path
                Path forSave = new Path(subject.getURI(), predicate.getURI(), object.getURI(), factPreprocessorClassName, counterRetrieverClassName, pathSearcherClassName, pathScorerClassName, p.getScore());
                // save path elements for a Path
                List<PathElement> pathElements = p.getPathElements()
                        .stream()
                        .map(element -> propertyElementMapper.map(element))
                        .collect(Collectors.toList());
                for (PathElement pe : pathElements
                ) {

                    List<PathElement> retrivedPathelements = pathElementRepository.findByInvertedAndProperty(pe.isInverted(), pe.getProperty());

                    if (retrivedPathelements.size() > 0) {
                        forSave.addPathElement(retrivedPathelements.get(0));
                    } else {
                        // add new
                        PathElement savedPathElement = pathElementRepository.save(pe);
                        forSave.addPathElement(savedPathElement);
                    }
                }
                // save path
                pathRepository.save(forSave);
        }
        //  uncomment these lines if you want to save even empty paths
/*        if(paths==null || paths.size()==0){
            Path forSave = new Path(subject.getURI(),predicate.getURI(),object.getURI(),factPreprocessorClassName,counterRetrieverClassName,pathSearcherClassName,pathScorerClassName,0);
            pathRepository.save(forSave);
        }*/
    }
}
