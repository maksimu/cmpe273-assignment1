package edu.sjsu.cmpe273.assignment1.repository;

import edu.sjsu.cmpe273.assignment1.entity.Books;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * User: maksim
 * Date: 2/22/14 - 2:43 PM
 */
public interface BooksRepository extends JpaRepository<Books, Long> {
}
