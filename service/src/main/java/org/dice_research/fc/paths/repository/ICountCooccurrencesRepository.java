package org.dice_research.fc.paths.repository;

import org.dice_research.fc.paths.model.CountCooccurrences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICountCooccurrencesRepository extends JpaRepository<CountCooccurrences,Long> {
    List<CountCooccurrences> findByPredicateAndDomainAndRangeAndPathAndGraphName(String predicate, String domain, String range, String path, String graphName);
}
