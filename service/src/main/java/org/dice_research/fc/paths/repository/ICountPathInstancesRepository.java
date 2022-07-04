package org.dice_research.fc.paths.repository;

import org.dice_research.fc.paths.model.CountPathInstances;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICountPathInstancesRepository extends JpaRepository<CountPathInstances, Long> {
    List<CountPathInstances> findByPathAndGraphNameAndDomainAndRange(String path, String graphName, String domain, String Range);
}
