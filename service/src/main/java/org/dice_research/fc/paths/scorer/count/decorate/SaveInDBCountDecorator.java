package org.dice_research.fc.paths.scorer.count.decorate;

import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.model.CountCooccurrences;
import org.dice_research.fc.paths.model.CountMaxPredicate;
import org.dice_research.fc.paths.model.CountPathInstances;
import org.dice_research.fc.paths.model.CountPredicate;
import org.dice_research.fc.paths.repository.*;
import org.dice_research.fc.paths.scorer.ICountRetriever;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;

// this decorator does not save a counts with value 0

public class SaveInDBCountDecorator extends AbstractCountRetrieverDecorator{

    private static final Logger LOGGER = LoggerFactory.getLogger(SaveInDBCountDecorator.class);

    private String graphName;

    @Autowired
    protected ICountCooccurrencesRepository countCooccurrencesRepository;

    @Autowired
    protected ICountMaxPredicateRepository countMaxPredicateRepository;

    @Autowired
    protected ICountPathInstancesRepository countPathInstancesRepository;

    @Autowired
    protected ICountPredicateRepository countPredicateRepository;

    @Autowired
    protected IPathRepository repositoryForTest;


    public SaveInDBCountDecorator(ICountRetriever decorated, String graphName){
        super(decorated);
        LOGGER.info("initiate the saveInDBDecorator graph name is "+ graphName);
        this.graphName = graphName;
    }

    // if the count is less than zero it means nothing has been found in the DB
    @Override
    public long countPathInstances(QRestrictedPath path, ITypeRestriction domainRestriction, ITypeRestriction rangeRestriction) {
        long count = getPathInstancesCountFromDB(path, domainRestriction, rangeRestriction);
        if(count<0){
            count = decorated.countPathInstances(path, domainRestriction, rangeRestriction);
            updatePathInstancesInDB(path, domainRestriction, rangeRestriction, count);
        }
        return count;
    }

    private long getPathInstancesCountFromDB(QRestrictedPath path, ITypeRestriction domainRestriction, ITypeRestriction rangeRestriction) {
        String pathStr = path.toStringWithTag();
        String domainStr = getDomainOrRangeFromObject(domainRestriction.getRestriction());
        if(domainStr == null) {
            LOGGER.info("domain is null");
            return -1; }

        String rangeStr = getDomainOrRangeFromObject(rangeRestriction.getRestriction());
        if(rangeStr == null) {
            LOGGER.info("range is null");
            return -1;}

        if(repositoryForTest == null){
            LOGGER.info("repositoryForTest is null!!");
        }else{
            LOGGER.info("repositoryForTest has value");
        }

        if(countPathInstancesRepository == null){
            LOGGER.info("countPathInstancesRepository is null !");
        }else{
            LOGGER.info("countPathInstancesRepository has value !");
        }
        LOGGER.info("countPathInstancesRepository :"+countPathInstancesRepository.getClass().toString());

        List<CountPathInstances> foundInstances = countPathInstancesRepository.findByPathAndGraphNameAndDomainAndRange(pathStr, graphName, domainStr, rangeStr);
        if(foundInstances.size()>0){
            CountPathInstances item = foundInstances.get(0);
            return item.getcCount();
        }
        return -1;
    }

    private void updatePathInstancesInDB(QRestrictedPath path, ITypeRestriction domainRestriction, ITypeRestriction rangeRestriction, long count) {
        if(count>0) {
            String pathStr = path.toStringWithTag();

            String domainStr = getDomainOrRangeFromObject(domainRestriction.getRestriction());
            if(domainStr == null) {
                LOGGER.info("domain is null");
                return ; }

            String rangeStr = getDomainOrRangeFromObject(rangeRestriction.getRestriction());
            if(rangeStr == null) {
                LOGGER.info("range is null");
                return ;}
            //String path, String graphName, String domain, String range, Long count
            CountPathInstances item = new CountPathInstances(pathStr, graphName, domainStr, rangeStr, count);
            countPathInstancesRepository.save(item);
        }
    }

    @Override
    public long countPredicateInstances(Predicate predicate) {
        long count = getPredicateInstancesFromDB(predicate);
        if(count<0){
            count = decorated.countPredicateInstances(predicate);
            updatePredicateInstancesInDB(predicate, count);
        }
        return count;
    }

    private long getPredicateInstancesFromDB(Predicate predicate) {
        String predicateStr = predicate.getProperty().getURI();
        List<CountPredicate> foundInstances = countPredicateRepository.findByGraphNameAndPredicate(graphName, predicateStr);
        if(foundInstances.size()>0){
            CountPredicate item = foundInstances.get(0);
            return item.getcCount();
        }
        return -1;
    }

    private void updatePredicateInstancesInDB(Predicate predicate, long count) {
        if(count>0) {
            String predicateStr = predicate.getProperty().getURI();
            //String predicate, String graphName, Long count
            CountPredicate item = new CountPredicate(predicateStr, graphName, count);
            countPredicateRepository.save(item);
        }
    }

    @Override
    public long countCooccurrences(Predicate predicate, QRestrictedPath path) {
        long count = getCooccurrencesFromDB(predicate, path);
        if(count<0){
            count = decorated.countCooccurrences(predicate, path);
            updateCooccurrencesInDB(predicate, path, count);
        }
        return count;
    }

    private long getCooccurrencesFromDB(Predicate predicate, QRestrictedPath path) {
        String pathStr = path.toStringWithTag();
        String domainStr = getDomainOrRangeFromObject(predicate.getDomain().getRestriction());
        if(domainStr == null) {
            LOGGER.info("domain is null");
            return -1;
        }

        String rangeStr = getDomainOrRangeFromObject(predicate.getRange().getRestriction());
        if(rangeStr == null) {
            LOGGER.info("range is null");
            return -1;}

        String predicateStr = predicate.getProperty().getURI();
        List<CountCooccurrences> foundInstances = countCooccurrencesRepository.findByPredicateAndDomainAndRangeAndPathAndGraphName(predicateStr, domainStr, rangeStr, pathStr, graphName);
        if(foundInstances.size()>0){
            CountCooccurrences item = foundInstances.get(0);
            return item.getcCount();
        }
        return -1;
    }

    private void updateCooccurrencesInDB(Predicate predicate, QRestrictedPath path, long count) {
        if(count>0) {
            String pathStr = path.toStringWithTag();
            String domainStr = getDomainOrRangeFromObject(predicate.getDomain().getRestriction());
            if(domainStr == null) {
                LOGGER.info("domain is null");
                return ;
            }

            String rangeStr = getDomainOrRangeFromObject(predicate.getRange().getRestriction());
            if(rangeStr == null) {
                LOGGER.info("range is null");
                return ;}

            String predicateStr = predicate.getProperty().getURI();
            //String predicate, String domain, String range, String path, String graphName, long count
            CountCooccurrences item = new CountCooccurrences(predicateStr, domainStr, rangeStr, pathStr, graphName, count);
            countCooccurrencesRepository.save(item);
        }
    }

    @Override
    public long deriveMaxCount(Predicate predicate) {
        long count = getMaxCountFromDB(predicate);
        if(count<0){
            count = decorated.deriveMaxCount(predicate);
            updateMaxCountInDB(predicate, count);
        }
        return count;
    }

    private long getMaxCountFromDB(Predicate predicate) {
        String predicateStr = predicate.getProperty().getURI();
        String domainStr = getDomainOrRangeFromObject(predicate.getDomain().getRestriction());
        if(domainStr == null) {
            LOGGER.info("domain is null");
            return -1;
        }

        String rangeStr = getDomainOrRangeFromObject(predicate.getRange().getRestriction());
        if(rangeStr == null) {
            LOGGER.info("range is null");
            return -1;}

        List<CountMaxPredicate> foundInstances = countMaxPredicateRepository.findByPredicateAndDomainAndRangeAndGraphName(predicateStr, domainStr, rangeStr, graphName);
        if(foundInstances.size()>0){
            CountMaxPredicate item = foundInstances.get(0);
            return item.getcCount();
        }
        return -1;
    }

    private void updateMaxCountInDB(Predicate predicate, long count) {
        if(count>0) {
            String predicateStr = predicate.getProperty().getURI();
            String domainStr = getDomainOrRangeFromObject(predicate.getDomain().getRestriction());
            if(domainStr == null) {
                LOGGER.info("domain is null");
                return ;
            }

            String rangeStr = getDomainOrRangeFromObject(predicate.getRange().getRestriction());
            if(rangeStr == null) {
                LOGGER.info("range is null");
                return ;}

            //String predicate, String domain, String range, String graphName, long count
            CountMaxPredicate item = new CountMaxPredicate(predicateStr, domainStr, rangeStr, graphName, count);
            countMaxPredicateRepository.save(item);
        }
    }

    private String getDomainOrRangeFromObject(Object restriction) {
        if (restriction instanceof String) {
            return (String) restriction;
        }

        if (restriction instanceof HashSet) {
            HashSet<String> hs = (HashSet<String>)restriction;
            StringBuilder sb = new StringBuilder();

            for (String s:hs) {
                sb.append(s);
            }

            return sb.toString();
        }

        return null;
    }
}
