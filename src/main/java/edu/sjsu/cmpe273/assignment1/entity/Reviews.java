package edu.sjsu.cmpe273.assignment1.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * User: maksim
 * Date: 2/22/14 - 2:45 PM
 */
@Entity
public class Reviews implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable=false)
    private Integer rating;
    @Column(nullable=false)
    private String comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
