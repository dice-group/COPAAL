package org.dice_research.fc.paths.repository;

import org.dice_research.fc.paths.model.CountPredicate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICountPredicateRepository extends JpaRepository<CountPredicate, Long> {
    List<CountPredicate> findByGraphNameAndPredicate(String graphName, String predicate);
}
