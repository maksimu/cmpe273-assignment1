package edu.sjsu.cmpe273.assignment1.service;

import edu.sjsu.cmpe273.assignment1.entity.Books;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import edu.sjsu.cmpe273.assignment1.repository.BooksRepository;

import java.util.List;

/**
 * User: maksim
 * Date: 2/22/14 - 7:53 PM
 */
@Component
public class BooksService {

    @Autowired
    BooksRepository booksRepository;

    /**
     * Returns all books in the database.
     *
     * @return All books in the database
     */
    public List<Books> getAll() {
        List<Books> allBooks = booksRepository.findAll();
        for (Books allBook : allBooks) {
            allBook.getAuthorsList().size();
        }

        return allBooks;
    }

    /**
     * Save or update a book
     *
     * @param book New book
     * @return Saved book
     */
    public Books save(Books book) {
        Books savedBook = booksRepository.save(book);
        return savedBook;
    }

    /**
     * Removing a book by its isbn
     * @param isbn Book's ISBN (primary key)
     * @return true if successfully removed, false otherwise
     */
    public Boolean delete(Long isbn){

        Books bookToRemove = booksRepository.findOne(isbn);
        booksRepository.delete(bookToRemove);

        // validating
        Books removedBook = booksRepository.findOne(isbn);
        if(removedBook != null){
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns book by its ISBN
     *
     * @param isbn Book's ISBN (primary key)
     * @return Found book
     */
    public Books findByISBN(Long isbn) {
        Books book = booksRepository.findOne(isbn);
        return book;
    }
}
