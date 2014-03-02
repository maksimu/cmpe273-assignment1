package edu.sjsu.cmpe273.assignment1.service;

import edu.sjsu.cmpe273.assignment1.entity.Reviews;
import edu.sjsu.cmpe273.assignment1.repository.ReviewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: maksim
 * Date: 2/22/14 - 9:04 PM
 */
@Component
public class ReviewService {

    @Autowired
    ReviewsRepository reviewsRepository;

    public Reviews save(Reviews review){
        Reviews savedReview = reviewsRepository.save(review);
        return savedReview;
    }

    public Reviews findById(Long id){
        Reviews review = reviewsRepository.findOne(id);
        return review;
    }
}
