package com.example.demo.application;

import com.example.demo.domain.Book;
import com.example.demo.domain.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceImplTest {
    private BookRepository bookRepository;
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        bookRepository = Mockito.mock(BookRepository.class);
        bookService = new BookServiceImpl(bookRepository);
    }

    @Test
    void testSave() {
        Book book = new Book(1L, "Title", "Author");
        when(bookRepository.save(book)).thenReturn(book);
        Book saved = bookService.save(book);
        assertEquals(book, saved);
    }

    @Test
    void testFindAll() {
        List<Book> books = Arrays.asList(new Book(1L, "A", "B"));
        when(bookRepository.findAll()).thenReturn(books);
        assertEquals(books, bookService.findAll());
    }

    @Test
    void testFindById() {
        Book book = new Book(1L, "A", "B");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        assertTrue(bookService.findById(1L).isPresent());
    }

    @Test
    void testDeleteById() {
        doNothing().when(bookRepository).deleteById(1L);
        bookService.deleteById(1L);
        verify(bookRepository, times(1)).deleteById(1L);
    }
}
