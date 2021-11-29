package org.dice_research.fc.paths;

import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class PreProcessProvider implements IPreProcessProvider{

    private static final Logger LOGGER = LoggerFactory.getLogger(PathBasedFactChecker.class);
    private String separator = ",";

    private Map<String,Long> pathInstancesCount;
    private Map<String,Long> predicateInstancesCount;
    private Map<String,Long> coOccurrenceCount;
    private Map<String,Long> maxCount;

    public PreProcessProvider(File pathInstancesCountFile, File predicateInstancesCountFile, File coOccurrenceCountFile, File maxCountFile){
        LOGGER.info("start load the preprocessing file");

        LOGGER.info("start process pathInstancesCount from {}",pathInstancesCountFile.getAbsolutePath());
        this.pathInstancesCount = processThePathInstancesCountFile(pathInstancesCountFile);
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
    }

    private Map<String, Long> processThePathInstancesCountFile(File pathInstancesCountFile) {
        Map<String, Long> map = new HashMap<>();
        // read file line by line
        // each line contain these part and comma seperated
        // query,count,path,predicate,domain,range
        try (BufferedReader br = new BufferedReader(new FileReader(pathInstancesCountFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(separator);
                //TODO
                //if(parts.length!=6){
                if(parts.length!=4){
                    LOGGER.error("the line in PathInstancesCountFile is not parsable it is not 6 parts : {}"+line);
                }
                // TODO :
                String key = keyForPathInstancesCount(parts[2],"http://dbpedia.org/ontology/Animal","http://dbpedia.org/ontology/Place");
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

    private String keyForPathInstancesCount(QRestrictedPath path, ITypeRestriction domainRestriction, ITypeRestriction rangeRestriction){
        HashSet domainSet =  (HashSet)domainRestriction.getRestriction();
        HashSet rangeSet =  (HashSet)rangeRestriction.getRestriction();
        if(domainSet.size() != 1 || rangeSet.size() != 1){
            LOGGER.error("range or domain are not parsable");
        }
        String domain = (String)domainSet.iterator().next();
        String range = (String)rangeSet.iterator().next();
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

    private String keyForMaxCount(Predicate predicate){
        return predicate.getProperty().getURI();
    }

    public long getPathInstances(QRestrictedPath path, ITypeRestriction domainRestriction, ITypeRestriction rangeRestriction) {
        return getFromMap(pathInstancesCount, keyForPathInstancesCount(path, domainRestriction, rangeRestriction));
    }

    public long getPredicateInstances(Predicate predicate) {
        return getFromMap(predicateInstancesCount, keyForPredicateInstancesCount(predicate));
    }

    public long getCooccurrences(Predicate predicate, QRestrictedPath path) {
        return getFromMap(coOccurrenceCount, keyForCoOccurrenceCount(path, predicate));
    }

    public long getMaxCount(Predicate predicate) {
        return getFromMap(maxCount, keyForMaxCount(predicate));
    }

    private long getFromMap(Map<String, Long> map, String key) {
        if(map.containsKey(key)){
            return map.get(key);
        }

        LOGGER.error("the map does not contain key, key is : "+ key);
        return 0;
    }
}
