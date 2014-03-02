package edu.sjsu.cmpe273.assignment1.webservice;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import edu.sjsu.cmpe273.assignment1.LibraryApplication;

import edu.sjsu.cmpe273.assignment1.dto.Link;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User: maksim
 * Date: 2/28/14 - 8:24 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LibraryApplication.class)
@WebAppConfiguration
public class BooksWSTest {


    @Autowired
    WebApplicationContext wac;

    private MockMvc mockMvc;


    String title = "Programming Amazon EC2";
    String language = "eng";
    int pgNumber = 185;
    String publicationDate = "02/11/2011";
    String status = "available";
    String authorName1 = "Jurg Vliet";
    String authorName2 = "Flavia Pagenelli";


    @Before
    public void setup() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        // Setup Spring test in webapp-mode (same config as spring-boot)
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }


    @Test
    public void testShouldNotFailIfServerIsRunningOK() throws Exception {

        mockMvc.perform(get("/health"))
                .andDo(print())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("status", is("ok")))
                .andExpect(status().isOk());
    }

    @Test
    public void testShouldSaveNewBook() throws Exception {

        String json = getNewBookJson(title, publicationDate, language, pgNumber, status, authorName1, authorName2);

        mockMvc.perform(
                post("/v1/books/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("links").isArray())
                .andExpect(jsonPath("links", hasSize(4)))
                .andExpect(jsonPath("links[*].rel", containsInAnyOrder("view-book", "update-book", "delete-book", "create-review")))
                .andExpect(jsonPath("links[0].href", containsString("/books/")))
                .andExpect(status().isCreated());
    }

    @Test
    public void testShouldGetOneBookByItsISBN() throws Exception {

        Integer isbn = addNewBook(title, publicationDate, language, pgNumber, status, authorName1, authorName2);

        mockMvc.perform(
                get("/v1/books/" + isbn)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("book.isbn", is(isbn)))
                .andExpect(jsonPath("book.title", is(title)))
                .andExpect(jsonPath("book.num-pages", is(pgNumber)))
                .andExpect(jsonPath("book.publication-date", is(publicationDate)))
                .andExpect(jsonPath("book.language", is(language)))
                .andExpect(jsonPath("book.status", is(status)))
                .andExpect(jsonPath("book.authors", hasSize(2)))
                .andExpect(jsonPath("book.authors[*].rel[0]", is("view-author")))
                .andExpect(jsonPath("book.reviews", hasSize(0)))
                .andExpect(status().isOk());
    }


    @Test
    public void testShouldUpdateBook() throws Exception {

        Integer isbn = addNewBook(title, publicationDate, language, pgNumber, status, authorName1, authorName2);

        String newTitle = "New Book Title";

        mockMvc.perform(
                put("/v1/books/" + isbn)
                        .param("title", newTitle)
                        .param("language", "russian")
                        .param("publication-date", "12/13/2014"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("links").isArray())
                .andExpect(jsonPath("links[*].rel", containsInAnyOrder("view-book", "update-book", "delete-book", "create-review")))
                .andExpect(jsonPath("links[0].href", containsString("/books/")))
                .andExpect(jsonPath("links[*].method", containsInAnyOrder("POST", "GET", "PUT", "DELETE")))
                .andExpect(status().isOk());

        testShouldDeleteBookByItsISBN();
    }

    @Test
    public void testShouldDeleteBookByItsISBN() throws Exception {

        Integer isbn = addNewBook(title, publicationDate, language, pgNumber, status, authorName1, authorName2);

        mockMvc.perform(delete("/v1/books/" + isbn))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("links[0].rel", is("create-book")))
                .andExpect(jsonPath("links[0].href", is("/books")))
                .andExpect(jsonPath("links[0].method", is("POST")))
                .andExpect(status().isOk());
    }

    @Test
    public void testSaveNewBookThenShouldCreateNewReviewForBookThenRemove() throws Exception {

        String json = getNewBookJson(title, publicationDate, language, pgNumber, status, authorName1, authorName2);
        MvcResult mvcResult = mockMvc.perform(
                post("/v1/books/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("links").isArray())
                .andExpect(jsonPath("links", hasSize(4)))
                .andExpect(jsonPath("links[*].rel", containsInAnyOrder("view-book", "update-book", "delete-book", "create-review")))
                .andExpect(jsonPath("links[0].href", containsString("/books/")))
                .andExpect(status().isCreated())
                .andReturn();

        Map<String, List> resposne = new Gson().fromJson(mvcResult.getResponse().getContentAsString(), Map.class);

        List<LinkedTreeMap<String, String>> links = resposne.get("links");
        String isbn = links.get(0).get("href").replace("/books/", "");


        String json2 = getNewReviewJson(4, "Good book on AWS fundamentals");

        MvcResult result2 = mockMvc.perform(
                post("/v1/books/" + isbn + "/reviews")
                        .content(json2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("links[0].rel", is("view-review")))
                .andExpect(jsonPath("links[0].href", containsString("/books")))
                .andExpect(jsonPath("links[0].href", containsString("/reviews/")))
                .andExpect(jsonPath("links[0].method", is("GET")))
                .andExpect(status().isCreated())
                .andReturn();

        result2.getResponse().getContentAsString();
    }

    @Test
    public void testShouldCreateBookThenAddReviewThenGetReview() throws Exception {
        String json = getNewBookJson(title, publicationDate, language, pgNumber, status, authorName1, authorName2);
        MvcResult mvcResult = mockMvc.perform(
                post("/v1/books/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("links").isArray())
                .andExpect(jsonPath("links", hasSize(4)))
                .andExpect(jsonPath("links[*].rel", containsInAnyOrder("view-book", "update-book", "delete-book", "create-review")))
                .andExpect(jsonPath("links[0].href", containsString("/books/")))
                .andExpect(status().isCreated())
                .andReturn();

        Map<String, List> resposne = new Gson().fromJson(mvcResult.getResponse().getContentAsString(), Map.class);

        List<LinkedTreeMap<String, String>> links = resposne.get("links");
        String isbn = links.get(0).get("href").replace("/books/", "");


        int reviewRating = 4;
        String reviewComment = "Good book on AWS fundamentals";
        String json2 = getNewReviewJson(reviewRating, reviewComment);

        MvcResult result2 = mockMvc.perform(
                post("/v1/books/" + isbn + "/reviews")
                        .content(json2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("links[0].rel", is("view-review")))
                .andExpect(jsonPath("links[0].href", containsString("/books")))
                .andExpect(jsonPath("links[0].href", containsString("/reviews/")))
                .andExpect(jsonPath("links[0].method", is("GET")))
                .andExpect(status().isCreated())
                .andReturn();


        Map<String, List> resposne2 = new Gson().fromJson(result2.getResponse().getContentAsString(), Map.class);

        List<LinkedTreeMap<String, String>> links2 = resposne2.get("links");
        String reviewIdStr = links2.get(0).get("href").replace("/books/" + isbn + "/reviews/", "");
        Integer reviewId = Integer.parseInt(reviewIdStr);


        mockMvc.perform(
                get("/v1/books/" + isbn + "/reviews/" + reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("review.id", is(reviewId)))
                .andExpect(jsonPath("review.rating", is(reviewRating)))
                .andExpect(jsonPath("review.comment", is(reviewComment)))
                .andExpect(status().isOk());


    }


    @Test
    public void testShouldCreateBookThenAddReviewThenGetAllReviews() throws Exception {
        String json = getNewBookJson(title, publicationDate, language, pgNumber, status, authorName1, authorName2);
        MvcResult mvcResult = mockMvc.perform(
                post("/v1/books/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("links").isArray())
                .andExpect(jsonPath("links", hasSize(4)))
                .andExpect(jsonPath("links[*].rel", containsInAnyOrder("view-book", "update-book", "delete-book", "create-review")))
                .andExpect(jsonPath("links[0].href", containsString("/books/")))
                .andExpect(status().isCreated())
                .andReturn();

        Map<String, List> resposne = new Gson().fromJson(mvcResult.getResponse().getContentAsString(), Map.class);

        List<LinkedTreeMap<String, String>> links = resposne.get("links");
        String isbn = links.get(0).get("href").replace("/books/", "");


        int reviewRating1 = 4;
        String reviewComment1 = "Good book on AWS fundamentals";
        Integer reviewId1 = addNewReview(isbn, reviewRating1, reviewComment1);

        int reviewRating2 = 5;
        String reviewComment2 = "Must read if you are new to AWS";
        Integer reviewId2 = addNewReview(isbn, reviewRating2, reviewComment2);


        mockMvc.perform(
                get("/v1/books/" + isbn + "/reviews")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("reviews").isArray())
                .andExpect(jsonPath("links").isArray())
                .andExpect(jsonPath("reviews[*].id", containsInAnyOrder(reviewId1, reviewId2)))
                .andExpect(jsonPath("reviews[*].rating", containsInAnyOrder(reviewRating1, reviewRating2)))
                .andExpect(jsonPath("reviews[*].comment", containsInAnyOrder(reviewComment1, reviewComment2)))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateNewBookThenAddAuthorsThenGetAllAuthors() throws Exception {
        Integer isbn = addNewBook(title, publicationDate, language, pgNumber, status, authorName1, authorName2);

        mockMvc.perform(
                get("/v1/books/" + isbn + "/authors"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("authors").isArray())
                .andExpect(jsonPath("links").isArray())
                .andExpect(jsonPath("authors[*].name", containsInAnyOrder(authorName1, authorName2)))
                .andExpect(status().isOk());


    }

    @Test
    public void testCreateNewBookWithAuthorsThenGetAuthor() throws Exception {
        Integer isbn = addNewBook(title, publicationDate, language, pgNumber, status, authorName1, authorName2);

        Integer authorId1= 1;
        Integer authorId2= 2;

        mockMvc.perform(
                get("/v1/books/" + isbn + "/authors/" + authorId1))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("author.id", is(authorId1)))
                .andExpect(jsonPath("author.name", is(authorName1)))
                .andExpect(jsonPath("links").isArray())
                .andExpect(status().isOk());

        mockMvc.perform(
                get("/v1/books/" + isbn + "/authors/" + authorId2))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("author.id", is(authorId2)))
                .andExpect(jsonPath("author.name", is(authorName2)))
                .andExpect(jsonPath("links").isArray())
                .andExpect(status().isOk());


    }


    // //////////// HELPER METHODS //////////// //

    public String getNewBookJson(String title, String publicationDate, String language, int pgNumber, String status, String... authorNames) {
        String json = "{\n" +
                "       \"title\" : \"" + title + "\",\n" +
                "       \"publication-date\" : \"" + publicationDate + "\",\n" +
                "       \"language\" : \"" + language + "\",\n" +
                "       \"num-pages\": " + pgNumber + ",\n" +
                "       \"status\" : \"" + status + "\",\n" +
                "       \"authors\" : [\n" +
                "           {\"name\" : \"" + authorNames[0] + "\" },\n" +
                "           {\"name\" : \"" + authorNames[1] + "\" }\n" +
                "      ]\n" +
                "  }";

        return json;
    }

    public String getNewReviewJson(int rating, String comment) {
        String json = "{\n" +
                "         \"rating\" : " + rating + ",\n" +
                "         \"comment\" : \"" + comment + "\"\n" +
                "      }";

        return json;
    }

    public Integer addNewBook(String title, String publicationDate, String language, int pgNumber, String status, String... authorNames) throws Exception {
        String json = getNewBookJson(title, publicationDate, language, pgNumber, status, authorNames);
        MvcResult mvcResult = mockMvc.perform(
                post("/v1/books/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("links").isArray())
                .andExpect(jsonPath("links", hasSize(4)))
                .andExpect(jsonPath("links[*].rel", containsInAnyOrder("view-book", "update-book", "delete-book", "create-review")))
                .andExpect(jsonPath("links[0].href", containsString("/books/")))
                .andExpect(status().isCreated())
                .andReturn();

        Map<String, List> resposne = new Gson().fromJson(mvcResult.getResponse().getContentAsString(), Map.class);

        List<LinkedTreeMap<String, String>> links = resposne.get("links");
        String isbnStr = links.get(0).get("href").replace("/books/", "");
        Integer isbn = Integer.parseInt(isbnStr);
        return isbn;
    }

    public Integer addNewReview(String isbn, int reviewRating, String reviewComment) throws Exception {

        String json2 = getNewReviewJson(reviewRating, reviewComment);

        MvcResult result2 = mockMvc.perform(
                post("/v1/books/" + isbn + "/reviews")
                        .content(json2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("links[0].rel", is("view-review")))
                .andExpect(jsonPath("links[0].href", containsString("/books")))
                .andExpect(jsonPath("links[0].href", containsString("/reviews/")))
                .andExpect(jsonPath("links[0].method", is("GET")))
                .andExpect(status().isCreated())
                .andReturn();


        Map<String, List> resposne2 = new Gson().fromJson(result2.getResponse().getContentAsString(), Map.class);

        List<LinkedTreeMap<String, String>> links2 = resposne2.get("links");
        String reviewIdStr = links2.get(0).get("href").replace("/books/" + isbn + "/reviews/", "");
        Integer reviewId = Integer.parseInt(reviewIdStr);

        return reviewId;
    }
}
