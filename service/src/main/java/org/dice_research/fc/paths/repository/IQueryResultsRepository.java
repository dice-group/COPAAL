package org.dice_research.fc.paths.repository;

import org.dice_research.fc.paths.model.QueryResults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IQueryResultsRepository extends JpaRepository<QueryResults, Long> {
    List<QueryResults> findByQueryAndIsdoneTrue(String query);
}
