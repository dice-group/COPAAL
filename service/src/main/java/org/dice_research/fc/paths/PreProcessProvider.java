package org.dice_research.fc.paths;

import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * implementation for this interface{@link IPreProcessProvider} .
 * this class accept 4 file , convert them to maps and provide methods to get numbers for each path/predicate
 * also it has {@param threshold} which tha path with score more than this will return from {@link #allPathsForThePredicate(Predicate)}
 *
 */
 public class PreProcessProvider implements IPreProcessProvider{

    private static final Logger LOGGER = LoggerFactory.getLogger(PathBasedFactChecker.class);
    private String separator = ",";
    private double  threshold;
    private Map<String,Long> pathInstancesCount;
    private Map<String,Long> predicateInstancesCount;
    private Map<String,Long> coOccurrenceCount;
    private Map<String,Long> maxCount;
    private Map<Predicate,Set<String>> mapPathForPredicates;

    // accept 4 folders each folder contains some files for each predicate
    //
    public PreProcessProvider(String pathInstancesCountFolder, String predicateInstancesCountFolder, String coOccurrenceCountFolder, String maxCountFolder, double  threshold, List<Predicate> validPredicates) {
        LOGGER.info("start load the preprocessing folders");
        LOGGER.info("we have {} valid predicates",validPredicates.size());
        LOGGER.info("start process pathInstancesCount from {}",pathInstancesCountFolder);
        this.threshold = threshold;
        this.pathInstancesCount = new HashMap<>();
        File[] pathInstancesCountFiles = getAllFileInThisFolder(pathInstancesCountFolder);
        for(File pathInstancesCountFile:pathInstancesCountFiles) {
            LOGGER.info("processing  {}",pathInstancesCountFile.getAbsolutePath());
            this.pathInstancesCount.putAll(processThePathInstancesCountFile(pathInstancesCountFile, validPredicates));
        }
        LOGGER.info("done, the pathInstancesCount map has size : {}",pathInstancesCount.size());

        LOGGER.info("start process predicateInstancesCount from {}",predicateInstancesCountFolder);
        this.predicateInstancesCount = new HashMap<>();
        File [] predicateInstancesCountFiles = getAllFileInThisFolder(predicateInstancesCountFolder);
        for(File predicateInstancesCountFile : predicateInstancesCountFiles) {
            this.predicateInstancesCount.putAll(processThePredicateInstancesCount(predicateInstancesCountFile));
        }
        LOGGER.info("done, the predicateInstancesCount map has size : {}",predicateInstancesCount.size());

        LOGGER.info("start process coOccurrenceCount from {}",coOccurrenceCountFolder);
        this.coOccurrenceCount = new HashMap<>();
        File []coOccurrenceCountFiles = getAllFileInThisFolder(coOccurrenceCountFolder);
        for(File coOccurrenceCountFile:coOccurrenceCountFiles) {
            this.coOccurrenceCount.putAll(processTheCoOccurrenceCount(coOccurrenceCountFile));
        }
        LOGGER.info("done, the coOccurrenceCount map has size : {}",coOccurrenceCount.size());

        LOGGER.info("start process maxCount from {}",maxCountFolder);
        this.maxCount = new HashMap<>();
        File[] maxCountFiles = getAllFileInThisFolder(maxCountFolder);
        for(File maxCountFile:maxCountFiles) {
            this.maxCount.putAll(processTheMaxCount(maxCountFile));
        }
        LOGGER.info("done, the maxCount map has size : {}",maxCount.size());

        mapPathForPredicates = new HashMap<>();

        for(Predicate predicate:validPredicates){
            mapPathForPredicates.put(predicate ,calculatePathsForThePredicate(predicate));
        }
    }

    private File[] getAllFileInThisFolder(String pathInstancesCountFolder) {
        File folder = new File(pathInstancesCountFolder);
        File[] listOfFiles = folder.listFiles();
        return  listOfFiles;
    }

    public PreProcessProvider(File pathInstancesCountFile, File predicateInstancesCountFile, File coOccurrenceCountFile, File maxCountFile, double  threshold, List<Predicate> validPredicates){
        LOGGER.info("start load the preprocessing file");
        this.threshold = threshold;
        LOGGER.info("start process pathInstancesCount from {}",pathInstancesCountFile.getAbsolutePath());
        this.pathInstancesCount = processThePathInstancesCountFile(pathInstancesCountFile, validPredicates);
        LOGGER.info("done, the pathInstancesCount map has size : {}",pathInstancesCount.size());

        LOGGER.info("start process predicateInstancesCount from {}",predicateInstancesCountFile.getAbsolutePath());
        this.predicateInstancesCount = processThePredicateInstancesCount(predicateInstancesCountFile);
        LOGGER.info("done, the predicateInstancesCount map has size : {}",predicateInstancesCount.size());

        LOGGER.info("start process coOccurrenceCount from {}",coOccurrenceCountFile.getAbsolutePath());
        this.coOccurrenceCount = processTheCoOccurrenceCount(coOccurrenceCountFile);
        LOGGER.info("done, the coOccurrenceCount map has size : {}",coOccurrenceCount.size());

        LOGGER.info("start process maxCount from {}",maxCountFile.getAbsolutePath());
        this.maxCount = processTheMaxCount(maxCountFile);
        LOGGER.info("done, the maxCount map has size : {}",maxCount.size());

        mapPathForPredicates = new HashMap<>();

        for(Predicate predicate:validPredicates){
            mapPathForPredicates.put(predicate ,calculatePathsForThePredicate(predicate));
        }


    }

    private Map<String, Long> processThePathInstancesCountFile(File pathInstancesCountFile, List<Predicate> validPredicates) {
        Map<String, Long> map = new HashMap<>();
        Map<String, Predicate> validPredicatesMap = new HashMap<>();
        for(Predicate p : validPredicates) validPredicatesMap.put(p.getProperty().getURI(),p);

        // read file line by line
        // each line contain these part and comma separated
        // query,count,path,predicate,domain,range
        try (BufferedReader br = new BufferedReader(new FileReader(pathInstancesCountFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(separator);
                //TODO
                //if(parts.length!=6){
                if(parts.length!=4){
                    LOGGER.error("the line in PathInstancesCountFile is not parsable it is not 4 parts : {}"+line);
                }

                String predicate = parts[3];
                String path = parts[2];

                if(validPredicatesMap.containsKey(predicate)) {
                    String key = keyForPathInstancesCount(path, trim(validPredicatesMap.get(predicate).getDomain().getRestriction().toString()), trim(validPredicatesMap.get(predicate).getRange().getRestriction().toString()));
                    map.put(key, Long.parseLong(parts[1]));
                }else{
                    LOGGER.error("the predicate is not in valid map :"+predicate);
                }
            }
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
            LOGGER.error(e.getStackTrace().toString());
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            LOGGER.error(e.getStackTrace().toString());
            e.printStackTrace();
        }

        return map;
    }

    private String keyForPathInstancesCount(QRestrictedPath path, ITypeRestriction domainRestriction, ITypeRestriction rangeRestriction){
        HashSet domainSet =  (HashSet)domainRestriction.getRestriction();
        HashSet rangeSet =  (HashSet)rangeRestriction.getRestriction();
        if(domainSet.size() != 1 || rangeSet.size() != 1){
            LOGGER.error("range or domain are not parsable");
        }
        String domain = trim((String)domainSet.iterator().next());
        String range = trim((String)rangeSet.iterator().next());
        return keyForPathInstancesCount(path.toStringWithTag(), domain, range);
    }

    private String keyForPathInstancesCount(String path, String domainRestriction, String rangeRestriction){
        return path+","+domainRestriction+","+rangeRestriction;
    }

    private Map<String, Long> processThePredicateInstancesCount(File predicateInstancesCountFile) {
        Map<String, Long> map = new HashMap<>();
        // read file line by line
        // each line contain these part and comma seperated
        // query,count,path,predicate
        try (BufferedReader br = new BufferedReader(new FileReader(predicateInstancesCountFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(separator);
                if(parts.length!=4){
                    LOGGER.error("the line in processThePredicateInstancesCount is not parsable it is not 4 parts : {}"+line);
                }
                map.put(parts[3], Long.parseLong(parts[1]));
            }
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
            LOGGER.error(e.getStackTrace().toString());
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            LOGGER.error(e.getStackTrace().toString());
            e.printStackTrace();
        }

        return map;
    }

    private String keyForPredicateInstancesCount(Predicate predicate){
        return predicate.getProperty().getURI();
    }

    private Map<String, Long> processTheCoOccurrenceCount(File coOccurrenceCountFile) {
        Map<String, Long> map = new HashMap<>();
        // read file line by line
        // each line contain these part and comma seperated
        // query,count,path,predicate
        try (BufferedReader br = new BufferedReader(new FileReader(coOccurrenceCountFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(separator);
                if(parts.length!=4){
                    LOGGER.error("the line in processTheCoOccurrenceCount is not parsable it is not 4 parts : {}"+line);
                }
                String key = keyForCoOccurrenceCount(parts[2],parts[3]);
                map.put(key, Long.parseLong(parts[1]));
            }
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
            LOGGER.error(e.getStackTrace().toString());
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            LOGGER.error(e.getStackTrace().toString());
            e.printStackTrace();
        }

        return map;
    }

    private String keyForCoOccurrenceCount(QRestrictedPath path, Predicate predicate) {
        return keyForCoOccurrenceCount(path.toStringWithTag(),predicate.getProperty().getURI());
    }

    private String keyForCoOccurrenceCount(String path, String predicate) {
        return path+","+predicate;
    }

    private Map<String, Long> processTheMaxCount(File maxCountFile) {
        Map<String, Long> map = new HashMap<>();
        // read file line by line
        // each line contain these part and comma seperated
        // query,count,path,predicate
        try (BufferedReader br = new BufferedReader(new FileReader(maxCountFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(separator);
                if(parts.length!=4){
                    LOGGER.error("the line in processThePredicateInstancesCount is not parsable it is not 4 parts : {}"+line);
                }
                map.put(parts[3], Long.parseLong(parts[1]));
            }
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
            LOGGER.error(e.getStackTrace().toString());
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            LOGGER.error(e.getStackTrace().toString());
            e.printStackTrace();
        }

        return map;
    }

    private String keyForMaxCount(ITypeRestriction predicate){
        return trim(predicate.getRestriction().toString());
    }

    @Override
    public long getPathInstances(QRestrictedPath path, ITypeRestriction domainRestriction, ITypeRestriction rangeRestriction) {
        return getFromMap(pathInstancesCount, keyForPathInstancesCount(path, domainRestriction, rangeRestriction));
    }

    @Override
    public long getPredicateInstances(Predicate predicate) {
        return getFromMap(predicateInstancesCount, keyForPredicateInstancesCount(predicate));
    }

    @Override
    public long getCooccurrences(Predicate predicate, QRestrictedPath path) {
        return getFromMap(coOccurrenceCount, keyForCoOccurrenceCount(path, predicate));
    }

    @Override
    public long getMaxCount(Predicate predicate) {
        return getFromMap(maxCount, keyForMaxCount(predicate.getDomain())) * getFromMap(maxCount, keyForMaxCount(predicate.getRange()));
    }

    private Set<String> calculatePathsForThePredicate(Predicate predicate) {
        // from coOccurrenceCount map
        // select all paths that they are matched with predicate

        Set<String> resultset = new HashSet<>();

        for (Map.Entry<String, Long> entry : coOccurrenceCount.entrySet()) {
            double score = calculateScoreForCoOccurrencePath(entry.getKey().split(",")[0], predicate);
            if(score>threshold) {
                if (theKeyIsMatchedWithPredicate(entry.getKey(), predicate.getProperty().getURI())) {
                    resultset.add(getPathFromcoOccurrenceCountKey(entry.getKey()));
                }
            }
        }
        LOGGER.info("for the predicate "+predicate.getProperty().getURI()+" found "+ resultset.size()+" paths in map");
        return resultset;
    }

    @Override
    public Set<String> allPathsForThePredicate(Predicate predicate) {
        if(mapPathForPredicates.containsKey(predicate)){
            return mapPathForPredicates.get(predicate);
        }
        return new HashSet<String>();
    }

    private double calculateScoreForCoOccurrencePath(String path, Predicate predicate) {
        double pathCounts = getFromMap(pathInstancesCount, keyForPathInstancesCount(path, trim(predicate.getDomain().getRestriction().toString()),trim(predicate.getRange().getRestriction().toString())));
        if(pathCounts == 0){
            return 0;
        }

        double predicateCounts = getFromMap(predicateInstancesCount, keyForPredicateInstancesCount(predicate));
        if(predicateCounts == 0){
            return 0;
        }

        double cooccurrenceCounts = getFromMap(coOccurrenceCount, keyForCoOccurrenceCount(path, predicate.getProperty().getURI()));
        if(cooccurrenceCounts == 0){
            return 0;
        }

        double deriveMaxCount =getFromMap(maxCount, keyForMaxCount(predicate.getDomain())) * getFromMap(maxCount, keyForMaxCount(predicate.getRange()));
        if(deriveMaxCount == 0){
            return 0;
        }

        return calculateScore(pathCounts, predicateCounts, cooccurrenceCounts, deriveMaxCount);
    }

    private double calculateScore(double pathCounts, double predicateCounts,
                                    double cooccurrenceCounts, double deriveMaxCount) {
        // P - probability, c - count
        // PMI (without log) = P(p,path) / P(p)*P(path)
        // = (c(p,path)/c(max)) / (c(p)/c(max))*(c(path)/c(max))
        // = c(p,path)*c(max) / c(p)*(c(path)
        double npmi = Math.log((cooccurrenceCounts * deriveMaxCount) / (predicateCounts * pathCounts))
                / -Math.log(cooccurrenceCounts / deriveMaxCount);
        if (npmi > 1) {
            return 1;
        } else if (npmi < -1) {
            return -1;
        } else {
            return npmi;
        }
    }

    private String getPathFromcoOccurrenceCountKey(String key) {
        String[] parts = key.split(",");
        return parts[0];
    }

    private boolean theKeyIsMatchedWithPredicate(String key, String uri) {
        String[] parts = key.split(",");
        if(parts[1].equals(uri)){
            return true;
        }
        return false;
    }

    private long getFromMap(Map<String, Long> map, String key) {
        if(map.containsKey(key)){
            return map.get(key);
        }

        LOGGER.trace("the map does not contain key, key is : "+ key);
        return 0;
    }

    private String trim(String input){
        return input.trim().replace("[","").replace("]","");
    }

}
