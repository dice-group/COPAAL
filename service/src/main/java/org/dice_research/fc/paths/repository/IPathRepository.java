package org.dice_research.fc.paths.repository;

import org.dice_research.fc.paths.model.Path;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * repository for Path
 *
 * @author Farshad Afshari
 *
 */

@Repository
public interface IPathRepository extends JpaRepository<Path, Long> {
    /**
     * hibernate will convert this to a query , this method search for path in a db
     * */
    List<Path> findBySubjectAndPredicateAndObjectAndFactPreprocessorAndCounterRetrieverAndPathSearcherAndPathScorer(String subject, String predicate, String object, String factpreprocessor, String counterRetriever, String pathSearcher, String pathScorer);
}
