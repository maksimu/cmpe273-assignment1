package edu.sjsu.cmpe273.assignment1.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * User: maksim
 * Date: 2/22/14 - 2:45 PM
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Books implements Serializable {

    @Id
    @GeneratedValue
    private Long isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty("publication-date")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="GMT")
    private Date publicationDate;

    private String language;

    @JsonProperty("num-pages")
    private Long numPages;

//    @ManyToMany(mappedBy = "bookz", cascade = CascadeType.ALL)
    @ManyToMany(targetEntity = Authors.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonProperty("authors")
    private List<Authors> authorsList;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Reviews> reviews;

    @Enumerated(EnumType.STRING)
    private BookStatus status = BookStatus.AVAILABLE;


    @PreUpdate
    public void preUpdate() {
    }

    @PrePersist
    public void prePersist() {
    }

    public Long getIsbn() {
        return isbn;
    }

    public void setIsbn(Long id) {
        this.isbn = id;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getLanguage() {
        return language.toLowerCase();
    }

    public void setLanguage(String language) {
        this.language = language.toLowerCase();
    }

    public Long getNumPages() {
        return numPages;
    }

    public void setNumPages(Long numPages) {
        this.numPages = numPages;
    }

    public List<Authors> getAuthorsList() {
        return authorsList;
    }

    public void setAuthors(List<Authors> authorsList) {
        this.authorsList = authorsList;
    }

    /**
     * Add one author. Helper method to make our life easier.
     *
     * @param a Author that will be added
     */
    public void addAuthor(Authors a) {
        if (getAuthorsList() == null) {
            setAuthors(new ArrayList<Authors>());
        }

        getAuthorsList().add(a);
    }

    public Set<Reviews> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Reviews> reviews) {
        this.reviews = reviews;
    }

    public void addReview(Reviews review){
        if(getReviews() == null){
            setReviews(new HashSet<Reviews>());
        }

        getReviews().add(review);
    }
//    @Override
//    public String toString() {
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        String json = gson.toJson(this);
//        return json;
//    }
}
