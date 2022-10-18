package org.dice_research.fc.paths.repository;

import org.dice_research.fc.paths.model.CountMaxPredicate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICountMaxPredicateRepository extends JpaRepository<CountMaxPredicate,Long> {
    List<CountMaxPredicate> findByPredicateAndDomainAndRangeAndGraphName(String predicate, String domain, String Range, String GraphName);
}
