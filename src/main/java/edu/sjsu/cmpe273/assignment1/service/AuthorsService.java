package edu.sjsu.cmpe273.assignment1.service;

import edu.sjsu.cmpe273.assignment1.entity.Authors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import edu.sjsu.cmpe273.assignment1.repository.AuthorsRepository;

import java.util.List;

/**
 * User: maksim
 * Date: 2/22/14 - 9:04 PM
 */
@Component
public class AuthorsService {

    @Autowired
    AuthorsRepository authorsRepository;

    /**
     * Returns all authors in the database
     * @return All authors
     */
    public List<Authors> getAll(){
        List<Authors> authors = authorsRepository.findAll();
        return authors;
    }

    /**
     * Returns one author for a give id
     * @param id Author id
     * @return Author
     */
    public Authors findById(Long id){
        return authorsRepository.findOne(id);
    }
}
