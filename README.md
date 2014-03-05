cmpe273-assignment1
===================

CMPE 273 Assignment 1



Available Services:

Req #  | URI                                                | Method     |Description
:-----:|----------------------------------------------------|------------|-------------------------------
       | library/v1/books                                   | **GET**       |Returns all available books
       | library/v1/books/                                  | **GET**        |Returns all available books (same as above)
1      | library/v1/books                                   | **POST**       |Create new Book
1      | library/v1/books/                                  | **POST**       |Create new Book (Save as above)
2      | library/v1/books/{isbn}                            | **GET**        |Get book by ISBN
3      | library/v1/books/{isbn}                            | **DELETE**     |Delete book by ISBN
4      | library/v1/books/{isbn}                            | **PUT**        |Update a book. Available query parameteres: `title`, `publication-date`, `language`, `numPage`, and `status`
5      | library/v1/books/{isbn}/reviews                    | **POST**       |Create book review
6      | library/v1/books/{isbn}/reviews/{reviewid}         | **GET**        |View book review by review id
7      | library/v1/books/{isbn}/reviews                    | **GET**        |View all book reviews
8      | library/v1/books/{isbn}/authors/{authorid}         | **GET**        |View book author by author id
9      | library/v1/books/{isbn}/authors                    | **GET**       |View all authors










