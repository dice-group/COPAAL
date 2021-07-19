package org.dice_research.fc.paths.repository;

import org.dice_research.fc.paths.model.Path;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PathRepository extends JpaRepository<Path, Long> {
    List<Path> findBysubjectSubjectAndPredicateAndObjectAndFactPreprocessorAndCounterRetrieverAndPathSearcherAndPathScorer(String subject, String predicate, String object, String factPreprocessor, String counterRetriever, String pathSearcher, String pathScorer);
}
