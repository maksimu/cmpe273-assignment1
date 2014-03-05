package edu.sjsu.cmpe273.assignment1.webservice;

import edu.sjsu.cmpe273.assignment1.dto.ErrorMap;
import edu.sjsu.cmpe273.assignment1.dto.Link;
import edu.sjsu.cmpe273.assignment1.entity.Authors;
import edu.sjsu.cmpe273.assignment1.entity.BookStatus;
import edu.sjsu.cmpe273.assignment1.entity.Books;
import edu.sjsu.cmpe273.assignment1.entity.Reviews;
import edu.sjsu.cmpe273.assignment1.service.AuthorsService;
import edu.sjsu.cmpe273.assignment1.service.BooksService;
import edu.sjsu.cmpe273.assignment1.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: maksim
 * Date: 2/22/14 - 8:05 PM
 */
@RestController
@RequestMapping("/v1/books")
public class BooksWS {

    @Autowired
    BooksService booksService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    AuthorsService authorsService;

    private static final SimpleDateFormat sdfGTM = new SimpleDateFormat("MM/dd/yyyy");
    static {
        sdfGTM.setTimeZone(TimeZone.getTimeZone("GMT"));
    }


    @RequestMapping(value = "", method= RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    ResponseEntity<List<Books>> availableMethods1(){
          return availableMethods2();
    }


    @RequestMapping(value = "/", method= RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    ResponseEntity<List<Books>> availableMethods2(){

        List<Books> booksWithoutAuthor = new ArrayList<Books>();
        for (Books b : booksService.getAll()) {
            b.setAuthors(null);
            booksWithoutAuthor.add(b);
        }


        return new ResponseEntity<List<Books>>(booksWithoutAuthor, HttpStatus.OK);
    }



    /**
     * Requirement 1: Create Book API
     *      Resource: POST - /books
     *      Description: Add a new book along with the author information to the library.
     *
     *
     *      Example Request:
     *      POST /books with the following payload in the request body.
     *          {
     *                "title" : "Programming Amazon EC2",
     *                "publication-date" : "2/11/2011",
     *                "language" : "eng",
     *                "num-pages": 185,
     *                "status" : "available",
     *                "authors" : [
     *                      {"name" : "Jurg Vliet" },
     *                      {"name" : "FlaviaPagenelli" }
     *                ]
     *            }
     *
     *      Expected response:
     *
     *      {
     *          “links” : [
     *              { “rel”: “view-book”, “href”: “/books/1”, “method”: “GET” },
     *              { “rel”: “update-book”, “href”: “/books/1”, “method”: “PUT” },
     *              { “rel”: “delete-book”, “href”: “/books/1”, “method”: “DELETE” },
     *              { “rel”: “create-review”, “href”: “/books/1/reviews”, “method”: “POST” }
     *          ]
     *      }
     */

    @RequestMapping(value = "", method= RequestMethod.POST, consumes = "application/json" ,produces = "application/json")
    public ResponseEntity<Map<String, Object>> save1(@RequestBody(required = true) Books book){
        return save2(book);
    }

    @RequestMapping(value = "/", method= RequestMethod.POST, consumes = "application/json" ,produces = "application/json")
    public ResponseEntity<Map<String, Object>> save2(@RequestBody(required = true) Books book){

        // Validating
        if(book.getTitle() == null){
            String e = "Title cannot be null";
            HttpStatus es = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<Map<String, Object>>(new ErrorMap(e,es.getReasonPhrase()), es);
        }

        if(book.getTitle() == ""){
            String e = "Title cannot be empty";
            HttpStatus es = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<Map<String, Object>>(new ErrorMap(e,es.getReasonPhrase()), es);
        }

        if(book.getPublicationDate() == null){
            String e = "Publish date cannot be null";
            HttpStatus es = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<Map<String, Object>>(new ErrorMap(e,es.getReasonPhrase()), es);
        }

        if(book.getStatus() == null){
            String e = "Status cannot be null";
            HttpStatus es = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<Map<String, Object>>(new ErrorMap(e,es.getReasonPhrase()), es);
        }

        Books savedBook = booksService.save(book);

        if(savedBook == null){
            String e = "Was not able to save new book";
            HttpStatus es = HttpStatus.INTERNAL_SERVER_ERROR;

            return new ResponseEntity<Map<String, Object>>(new ErrorMap(e,es.getReasonPhrase()), es);
        }


        Map<String, Object> responseMap = new HashMap<String, Object>();

        // LINKS
        List<Link> links = new ArrayList<Link>();
        links.add(new Link("view-book",         "/books/" + savedBook.getIsbn(),                "GET"));
        links.add(new Link("update-book",       "/books/" + savedBook.getIsbn(),                "PUT"));
        links.add(new Link("delete-book",       "/books/" + savedBook.getIsbn(),                "DELETE"));
        links.add(new Link("create-review",     "/books/" + savedBook.getIsbn() + "/reviews",   "POST"));

        responseMap.put("links", links);

        return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.CREATED);
    }

    /**
     * Requirement 2: View Book API
     * Resource: GET - /books/{isbn}
     * Description: View an existing book from the library.
     *
     *
     * Expected Response:
     * HTTP Code: 200
     *     {
     *         “book” : {
     *             “isbn” : 1,
     *             “title” : “Programming Amazon EC2”,
     *             “publication-date” : “2/11/2011”,
     *             “language” : “eng”,
     *             “num-pages”: 185,
     *             “status” : “available”,
     *             “reviews” : [],
     *             “authors” : [
     *                 { “rel”: “view-author”, “href”: “/books/1/authors/1”, “method”: “GET” },
     *                 { “rel”: “view-author”, “href”: “/books/1/authors/2”, “method”: “GET” }
     *             ]
     *         },
     *
     *         "links” : [
     *             { “rel”: “view-book”, “href”: “/books/1”, “method”: “GET” },
     *             { “rel”: “update-book”, “href”: “/books/1”, “method”: “PUT” },
     *             { “rel”: “delete-book”, “href”: “/books/1”, “method”: “DELETE” },
     *             { “rel”: “create-review”, “href”: “/books/1/reviews”, “method”: “POST” },
     *             { “rel”: “view-all-reviews”, “href”: “/books/1/reviews”, “method”: “GET” } # if reviews > 0
     *         ]
     *     }
     */
    @RequestMapping(value = "/{isbn}", method= RequestMethod.GET ,produces = "application/json")
    public ResponseEntity<Map<String, Object>> get(@PathVariable("isbn") Long isbn){

        // 1. BOOK
        Books b = booksService.findByISBN(isbn);
        if(b == null){
            String e = "Was not able to fine a book by its isbn [" + isbn + "]";
            HttpStatus es = HttpStatus.NOT_FOUND;

            return new ResponseEntity<Map<String, Object>>(new ErrorMap(e,es.getReasonPhrase()), es);
        }


        Map<String, Object> bookMap = new HashMap<String, Object>();

        List<Link> reviewLinks = new ArrayList<Link>();
        for(Reviews r : b.getReviews()){
            reviewLinks.add(new Link("view-review", "/books/" + b.getIsbn() + "/reviews/" + r.getId(), "GET"));
        }
        List<Link> authorLinks = new ArrayList<Link>();
        for(Authors a : b.getAuthorsList()){
            authorLinks.add(new Link("view-author", "/books/" + b.getIsbn() + "/authors/" + a.getId(), "GET"));
        }

        bookMap.put("isbn", b.getIsbn());
        bookMap.put("title", b.getTitle());
        bookMap.put("publication-date", sdfGTM.format(b.getPublicationDate()));
        bookMap.put("language", b.getLanguage());
        bookMap.put("num-pages", b.getNumPages());
        bookMap.put("status", b.getStatus().getId());
        bookMap.put("reviews", reviewLinks);
        bookMap.put("authors", authorLinks);

        // 2. LINKS
        List<Link> links = new ArrayList<Link>();
        links.add(new Link("view-book",         "/books/" + b.getIsbn(),                "GET"));
        links.add(new Link("update-book",       "/books/" + b.getIsbn(),                "PUT"));
        links.add(new Link("delete-book",       "/books/" + b.getIsbn(),                "DELETE"));
        links.add(new Link("create-review",     "/books/" + b.getIsbn() + "/reviews",   "POST"));

        if(b.getReviews().size() > 0){
            links.add(new Link("view-all-reviews",  "/books/" + b.getIsbn() + "/reviews",   "GET"));
        }

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("book", bookMap);
        responseMap.put("links", links);

        return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.OK);
    }

    /**
     * Requirement 3: Delete Book API
     *      Resource: DELETE - /books/{isbn}
     *      Description: Delete an existing book from the library.
     *
     *
     *      Expected Response:
     *      HTTP Code: 200
     *     {
     *        “links” : [
     *          { “rel”: “create-book”, “href”: “/books”, “method”: “POST” }
     *        ]
     *     }
     */
    @RequestMapping(value = "/{isbn}", method= RequestMethod.DELETE ,produces = "application/json")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable("isbn") Long isbn){

        Map<String, Object> responseMap = new HashMap<String, Object>();

        Boolean isRemoved = booksService.delete(isbn);

        if(! isRemoved){
            String e = "Was not able to remove book (isbn = " + isbn + ")";
            HttpStatus es = HttpStatus.NOT_FOUND;

            return new ResponseEntity<Map<String, Object>>(new ErrorMap(e,es.getReasonPhrase()), es);
        }

        Link[] links =  { new Link("create-book", "/books", "POST") };

        responseMap.put("links", links);

        return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.OK );
    }

    /**
     * Requirement 4: Update Book API
     *   Resource: PUT - /books/{isbn}?status={new-status}
     *   Description: Update an existing book meta-data from the library. For instance,
     *   change the status: from “Available” to “Lost”.
     *
     *
     *       Expected Response:
     *            HTTP Code: 200
     *            {
     *                “links” : [
     *                    { “rel”: “view-book”, “href”: “/books/1”, “method”: “GET” },
     *                    { “rel”: “update-book”, “href”: “/books/1”, “method”: “PUT” },
     *                    { “rel”: “delete-book”, “href”: “/books/1”, “method”: “DELETE” },
     *                    { “rel”: “create-review”, “href”: “/books/1/reviews”, “method”: “POST” }
     *                    { “rel”: “view-all-reviews”, “href”: “/books/1/reviews”, “method”: “GET” } # if reviews > 0
     *                ]
     *            }
     */
    @RequestMapping(value = "/{isbn}", method= RequestMethod.PUT ,produces = "application/json")
    public ResponseEntity<Map<String, Object>> update(@PathVariable("isbn") Long isbn,
                                      @RequestParam(value = "title", required = false) String title,
                                      @RequestParam(value = "publication-date", required = false) String publication_date,
                                      @RequestParam(value = "language", required = false) String language,
                                      @RequestParam(value = "numPages", required = false) Long numPages,
                                      @RequestParam(value = "status", required = false) String status
                                      ){

        Books b = booksService.findByISBN(isbn);

        try {
            if (title != null)
                b.setTitle(title);

            if (publication_date != null)
                b.setPublicationDate(new Date(publication_date));

            if (language != null)
                b.setLanguage(language);

            if (numPages != null)
                b.setNumPages(numPages);

            if (status != null)
                b.setStatus(BookStatus.valueOf(status));

        } catch (Exception ex) {
            String e = "Problem parsing data.\n" + ex.getMessage();
            HttpStatus es = HttpStatus.INTERNAL_SERVER_ERROR;
            return new ResponseEntity<Map<String, Object>>(new ErrorMap(e,es.getReasonPhrase()), es);
        }

        booksService.save(b);


        List<Link> links = new ArrayList<Link>();
        links.add(new Link("view-book",         "/books/" + b.getIsbn(),                "GET"));
        links.add(new Link("update-book",       "/books/" + b.getIsbn(),                "PUT"));
        links.add(new Link("delete-book",       "/books/" + b.getIsbn(),                "DELETE"));
        links.add(new Link("create-review",     "/books/" + b.getIsbn() + "/reviews",   "POST"));

        if(b.getReviews().size() > 0){
            links.add(new Link("view-all-reviews",  "/books/" + b.getIsbn() + "/reviews",   "GET"));
        }

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("links", links);

        return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.OK);
    }

    /**
     * Requirement 5: Create Book Review API
     *    Resource: POST - /books/{isbn}/reviews
     *    Description: Add a new review to the book.
     *    (Neither updating nor deleting reviews is allowed to minimize the scope)
     *    Example Request: POST /books/1/reviews
     *        {
     *        "rating" : 4,
     *        "comment" : "Good book on AWS fundamentals"
     *        }
     *    Expected Response:
     *    HTTP Code: 201
     *        {
     *            “links” : [
     *               { “rel”: “view-review”, “href”: “/books/1/reviews/1”, “method”: “GET” },
     *           ]
     *   }
     */
    @RequestMapping(value = "/{isbn}/reviews", method= RequestMethod.POST ,produces = "application/json")
    public ResponseEntity<Map<String, Object>> newReview(@PathVariable("isbn") Long isbn,
                                         @RequestBody(required = true) Reviews review){

        review = reviewService.save(review);
        if(review == null){
            String e = "Was not able to save review";
            HttpStatus es = HttpStatus.INTERNAL_SERVER_ERROR;

            return new ResponseEntity<Map<String, Object>>(new ErrorMap(e,es.getReasonPhrase()), es);
        }


        Books b = booksService.findByISBN(isbn);

        if(b == null){
            String e = "Was not able to fine a book by its isbn [" + isbn + "]";
            HttpStatus es = HttpStatus.NOT_FOUND;

            return new ResponseEntity<Map<String, Object>>(new ErrorMap(e,es.getReasonPhrase()), es);
        }

        b.addReview(review);

        booksService.save(b);

        Link[] links =  { new Link("view-review", "/books/" + b.getIsbn() + "/reviews/" + review.getId(), "GET") };

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("links", links);

        return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.CREATED);
    }

    /**
     * Requirement 6: View Book Review API
     *    Resource: GET - /books/{isbn}/reviews/{id}
     *    Description: View a particular review of the book.
     *
     *
     *    Expected Response:
     *    HTTP Code: 200
     *        {
     *            “review” : {
     *                  “id” : 1,
     *                  “rating” : 4
     *                  “comment” : “Good book on AWS fundamentals”
     *            },
     *            “links” : [
     *                   { “rel”: “view-review”, “href”: “/books/1/reviews/1”, “method”: “GET” }
     *             ]
     *        }
     *
     */
    @RequestMapping(value = "/{isbn}/reviews/{reviewId}", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Map<String, Object>> getReview(@PathVariable("isbn") Long isbn,
                                         @PathVariable("reviewId") Long reviewId){

        Books book = booksService.findByISBN(isbn);
        if(book == null){
            String e = "Was not able to fine a book by its isbn [" + isbn + "]";
            HttpStatus es = HttpStatus.NOT_FOUND;

            return new ResponseEntity<Map<String, Object>>(new ErrorMap(e,es.getReasonPhrase()), es);
        }

        Reviews review = reviewService.findById(reviewId);
        if(review == null){
            String e = "Was not able to fina a review (id = " + reviewId + ")";
            HttpStatus es = HttpStatus.NOT_FOUND;

            return new ResponseEntity<Map<String, Object>>(new ErrorMap(e,es.getReasonPhrase()), es);
        }

        Link[] links =  { new Link("view-review", "/books/" + book.getIsbn() + "/reviews/" + review.getId(), "GET") };

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("review", review);
        responseMap.put("links", links);

        return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.OK);
    }

    /**
     * Requirement 7: View All Reviews API
         Resource: GET - /books/{isbn}/reviews
         Description: View all reviews of the book.


         Expected Response:
         HTTP Code: 200
             {
                 “reviews” :  [
                         {
                         “id” : 1,
                         “rating” : 4
                         “comment” : “Good book on AWS fundamentals”
                         },
                         {
                         “id” : 2,
                         “rating” : 5
                         “comment” : “Must read if you’re new to AWS”
                         }
                    ],
                 “links” : []
             }

     */

    @RequestMapping(value = "/{isbn}/reviews", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Map<String, Object>> viewAllReviews(@PathVariable("isbn") Long isbn){

        Books book = booksService.findByISBN(isbn);
        if(book == null){
            String e = "Was not able to fine a book by its isbn [" + isbn + "]";
            HttpStatus es = HttpStatus.NOT_FOUND;

            return new ResponseEntity<Map<String, Object>>(new ErrorMap(e,es.getReasonPhrase()), es);
        }

        Link[] links =  { };

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("reviews", book.getReviews());
        responseMap.put("links", links);

        return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.OK);
    }

    /**
     * Requirement 8: View Book Author API
     *    Resource: GET - /books/{isbn}/authors/{id}
     *    Description: View a particular author of the book.
     *
     *
     *    Expected Response:
     *    HTTP Code: 200
     *        {
     *            “author” : {
     *                “id” : 1,
     *                “name” : “Jurg Vliet”
     *            },
     *            “links” : [
     *                   { “rel”: “view-author”, “href”: “/books/1/authors/1”, “method”: “GET” }
     *             ]
     *        }
     *
     */
    @RequestMapping(value = "/{isbn}/authors/{authorId}", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Map<String, Object>> viewBookAuthor(@PathVariable("isbn") Long isbn,
                                                              @PathVariable("authorId") Long authorId) {


        Books book = booksService.findByISBN(isbn);
        if(book == null){
            String e = "Was not able to fine a book by its isbn [" + isbn + "]";
            HttpStatus es = HttpStatus.NOT_FOUND;

            return new ResponseEntity<Map<String, Object>>(new ErrorMap(e,es.getReasonPhrase()), es);
        }

        Authors author = authorsService.findById(authorId);
        if(author == null){
            String e = "Was not able to fine an author by id = [" + authorId + "]";
            HttpStatus es = HttpStatus.NOT_FOUND;

            return new ResponseEntity<Map<String, Object>>(new ErrorMap(e,es.getReasonPhrase()), es);
        }

        author.setBooks(null); // TODO **** Need to fix this null. Right now it is causing StackOverflow error ****

        Link[] links =  { new Link("view-author", "/books/" + book.getIsbn() + "/authors/" + author.getId(), "GET") };

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("author", author);
        responseMap.put("links", links);

        return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.OK);
    }

    /**
     * Requirement 9: View All Authors of the Book API
         Resource: GET - /books/{isbn}/authors
         Description: View all authors of the book.


         Expected Response:
         HTTP Code: 200
             {
                 “authors” : [
                     {
                     “id” : 1,
                     “name” : “Jurg Vliet”
                     },
                     {
                     “id” : 2,
                     “name” : “Flavia Pagenelli”
                     }
                     ],
                 “links” : []
             }
     */
    @RequestMapping(value = "/{isbn}/authors", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Map<String, Object>> viewAllBookAuthors(@PathVariable("isbn") Long isbn) {

        Books book = booksService.findByISBN(isbn);
        if(book == null){
            String e = "Was not able to fine a book by its isbn [" + isbn + "]";
            HttpStatus es = HttpStatus.NOT_FOUND;

            return new ResponseEntity<Map<String, Object>>(new ErrorMap(e,es.getReasonPhrase()), es);
        }

        // TODO **** Need to fix this null. Right now it is causing StackOverflow error ****
        for (Authors authors : book.getAuthorsList()) {
           authors.setBooks(null);
        }

        Link[] links =  { };

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("authors", book.getAuthorsList());
        responseMap.put("links", links);

        return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.OK);
    }
}
