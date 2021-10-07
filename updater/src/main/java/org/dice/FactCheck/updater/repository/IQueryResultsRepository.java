package org.dice.FactCheck.updater.repository;

import org.dice.FactCheck.updater.model.QueryResults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IQueryResultsRepository extends JpaRepository<QueryResults, Long> {
    List<QueryResults> findByQuery(String query);
    List<QueryResults> findByQueryAndIsdone(String query,boolean isDone);
}
