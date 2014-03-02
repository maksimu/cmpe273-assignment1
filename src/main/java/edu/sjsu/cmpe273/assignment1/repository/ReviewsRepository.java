package edu.sjsu.cmpe273.assignment1.repository;

import edu.sjsu.cmpe273.assignment1.entity.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * User: maksim
 * Date: 2/22/14 - 2:44 PM
 */
public interface ReviewsRepository extends JpaRepository<Reviews, Long> {
}
