package com.example.demo.bdd;

import com.example.demo.domain.Book;
import com.example.demo.domain.BookRepository;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration
public class BookCrudStepDefs {
    @Autowired
    private BookRepository bookRepository;

    private Book book;
    private Book createdBook;
    private List<Book> books;
    private Optional<Book> foundBook;

    @Given("a book with title {string} and author {string}")
    public void a_book_with_title_and_author(String title, String author) {
        book = new Book(title, author);
    }

    @Given("a book with title {string} and author {string} exists")
    public void a_book_with_title_and_author_exists(String title, String author) {
        book = new Book(title, author);
        createdBook = bookRepository.save(book);
    }

    @When("the user creates the book")
    public void the_user_creates_the_book() {
        createdBook = bookRepository.save(book);
    }

    @When("the user requests all books")
    public void the_user_requests_all_books() {
        books = bookRepository.findAll();
    }

    @When("the user requests the book by id")
    public void the_user_requests_the_book_by_id() {
        foundBook = bookRepository.findById(createdBook.getId());
    }

    @When("the user deletes the book by id")
    public void the_user_deletes_the_book_by_id() {
        bookRepository.deleteById(createdBook.getId());
    }

    @Then("the book should be created with title {string} and author {string}")
    public void the_book_should_be_created_with_title_and_author(String title, String author) {
        assertNotNull(createdBook);
        assertEquals(title, createdBook.getTitle());
        assertEquals(author, createdBook.getAuthor());
    }

    @Then("the list of books should be returned")
    public void the_list_of_books_should_be_returned() {
        assertNotNull(books);
        assertTrue(books.size() >= 0);
    }

    @Then("the book should be returned with title {string} and author {string}")
    public void the_book_should_be_returned_with_title_and_author(String title, String author) {
        assertTrue(foundBook.isPresent());
        assertEquals(title, foundBook.get().getTitle());
        assertEquals(author, foundBook.get().getAuthor());
    }

    @Then("the book should not be found by id")
    public void the_book_should_not_be_found_by_id() {
        Optional<Book> deleted = bookRepository.findById(createdBook.getId());
        assertTrue(deleted.isEmpty());
    }
}
