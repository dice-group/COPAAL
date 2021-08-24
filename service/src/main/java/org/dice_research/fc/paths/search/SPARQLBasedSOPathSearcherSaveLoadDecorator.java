package org.dice_research.fc.paths.search;

import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.dice_research.fc.IMapper;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.FactPreprocessor;
import org.dice_research.fc.paths.IPathScorer;
import org.dice_research.fc.paths.IPathSearcher;
import org.dice_research.fc.paths.IPathSearcherDecorator;
import org.dice_research.fc.paths.model.Path;
import org.dice_research.fc.paths.model.PathElement;
import org.dice_research.fc.paths.repository.IPathElementRepository;
import org.dice_research.fc.paths.repository.IPathRepository;
import org.dice_research.fc.paths.scorer.ICountRetriever;
import org.dice_research.fc.sum.ScoreSummarist;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is a decorator fot SPARQLBasedSOPathSearcher, for searching the paths at first try to find them in DB
 * if find then return them
 * if not use the actual search part.
 *
 * @author Farshad Afshari
 *
 */

public class SPARQLBasedSOPathSearcherSaveLoadDecorator extends IPathSearcherDecorator {

    @Autowired
    IPathRepository pathRepository;

    @Autowired
    IPathElementRepository pathElementRepository;

    @Autowired
    IMapper<Path, QRestrictedPath> mapper;

    @Autowired
    IMapper<Pair<Property, Boolean>, PathElement> propertyElementMapper;

    @Autowired
    ICountRetriever countRetriever;

    @Autowired
    FactPreprocessor factPreprocessor;

    @Autowired
    IPathSearcher pathSearcher;

    @Autowired
    protected IPathScorer pathScorer;

    @Autowired
    protected ScoreSummarist summarist;

    public SPARQLBasedSOPathSearcherSaveLoadDecorator(IPathSearcher decoratedPathSearcher) {
        super(decoratedPathSearcher);
    }

    @Override
    public Collection<QRestrictedPath> search(Resource subject, Predicate predicate, Resource object){
        String factPreprocessorClassName = this.factPreprocessor.getClass().getName();
        String pathSearcherClassName = this.pathSearcher.getClass().getName();
        String pathScorerClassName = this.pathScorer.getClass().getName();
        String counterRetrieverClassName = this.countRetriever.getClass().getName();

        Collection<QRestrictedPath> paths = retrievePathsFromDB(subject,predicate.getProperty(),object,factPreprocessorClassName,counterRetrieverClassName,pathSearcherClassName,pathScorerClassName);

        if(paths.size()==0){
            paths = decoratedPathSearcher.search(subject,predicate,object);
            //save paths
            savePaths(paths,subject,predicate.getProperty(),object,factPreprocessorClassName,counterRetrieverClassName,pathSearcherClassName,pathScorerClassName);
        }

        return paths;
    }

    /**
     * retrive the path from DB
     * @return  a list of saved path
     * @param subject is a fact subject
     * @param predicate ia a fact predicate
     * @param object is a fact predicate
     * @param factPreprocessorClassName shows which factPreprocessor used
     * @param counterRetrieverClassName shows which counterRetriever used
     * @param pathSearcherClassName shows which pathSearcher used
     * @param pathScorerClassName shows which pathScorer used
     */
    private List<QRestrictedPath> retrievePathsFromDB(Resource subject, Property predicate, Resource object, String factPreprocessorClassName, String counterRetrieverClassName, String pathSearcherClassName, String pathScorerClassName) {
        List<Path> paths = pathRepository.findBySubjectAndPredicateAndObjectAndFactPreprocessorAndCounterRetrieverAndPathSearcherAndPathScorer(subject.getURI(),predicate.getURI(),object.getURI(),factPreprocessorClassName,counterRetrieverClassName,pathSearcherClassName,pathScorerClassName);
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
            Path forSave = new Path(subject.getURI(),predicate.getURI(),object.getURI(),factPreprocessorClassName,counterRetrieverClassName,pathSearcherClassName,pathScorerClassName,p.getScore());
            // save path elements for a Path
            List<PathElement> pathElements = p.getPathElements()
                    .stream()
                    .map(element -> propertyElementMapper.map(element))
                    .collect(Collectors.toList());
            for (PathElement pe:pathElements
            ) {

                List<PathElement> retrivedPathelements = pathElementRepository.findByInvertedAndProperty(pe.isInverted(),pe.getProperty());

                if(retrivedPathelements.size()>0){
                    forSave.addPathElement(retrivedPathelements.get(0));
                }else{
                    // add new
                    PathElement savedPathElement=pathElementRepository.save(pe);
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
