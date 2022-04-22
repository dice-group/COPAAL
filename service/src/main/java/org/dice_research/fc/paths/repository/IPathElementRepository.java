package org.dice_research.fc.paths.repository;

import org.dice_research.fc.paths.model.PathElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for PathElement
 *
 * @author Farshad Afshari
 *
 */

@Repository
public interface IPathElementRepository extends JpaRepository<PathElement, Long> {
    List<PathElement> findByInvertedAndProperty(Boolean Inverted, String Property);
}
