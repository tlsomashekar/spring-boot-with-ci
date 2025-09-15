package com.example.demo.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookTest {
    @Test
    void testBookConstructorAndGetters() {
        Book book = new Book(1L, "Title", "Author");
        assertEquals(1L, book.getId());
        assertEquals("Title", book.getTitle());
        assertEquals("Author", book.getAuthor());
    }

    @Test
    void testSetters() {
        Book book = new Book();
        book.setId(2L);
        book.setTitle("Another Title");
        book.setAuthor("Another Author");
        assertEquals(2L, book.getId());
        assertEquals("Another Title", book.getTitle());
        assertEquals("Another Author", book.getAuthor());
    }
}
