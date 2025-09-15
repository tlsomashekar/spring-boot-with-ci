package com.example.demo.adapter.outbound;

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

class BookRepositoryAdapterTest {
    private BookJpaRepository jpaRepository;
    private BookRepository adapter;

    @BeforeEach
    void setUp() {
        jpaRepository = Mockito.mock(BookJpaRepository.class);
        adapter = new BookRepositoryAdapter(jpaRepository);
    }

    @Test
    void testSave() {
    Book book = new Book(1L, "T", "A");
    BookEntity entity = BookMapper.toEntity(book);
    // Ensure the mock returns a valid entity for any BookEntity
    when(jpaRepository.save(any(BookEntity.class))).thenReturn(entity);
    Book saved = adapter.save(book);
    assertNotNull(saved);
    assertEquals(book.getId(), saved.getId());
    }

    @Test
    void testFindAll() {
        BookEntity entity = new BookEntity(1L, "T", "A");
        when(jpaRepository.findAll()).thenReturn(Arrays.asList(entity));
        List<Book> books = adapter.findAll();
        assertEquals(1, books.size());
    }

    @Test
    void testFindById() {
        BookEntity entity = new BookEntity(1L, "T", "A");
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity));
        Optional<Book> book = adapter.findById(1L);
        assertTrue(book.isPresent());
    }

    @Test
    void testDeleteById() {
        doNothing().when(jpaRepository).deleteById(1L);
        adapter.deleteById(1L);
        verify(jpaRepository, times(1)).deleteById(1L);
    }
}
