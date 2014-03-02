package edu.sjsu.cmpe273.assignment1.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * User: maksim
 * Date: 2/22/14 - 2:45 PM
 */
@Entity
public class Authors implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(targetEntity=Books.class, mappedBy = "authorsList", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Books> books;

    public Authors(){

    }

    public Authors(String name){
        this.name = name;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Books> getBooks() {
        return books;
    }

    public void setBooks(List<Books> books) {
        this.books = books;
    }
}

