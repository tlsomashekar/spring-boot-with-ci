package com.example.demo.application;

import com.example.demo.domain.Book;
import java.util.List;
import java.util.Optional;

public interface BookService {
    Book save(Book book);
    List<Book> findAll();
    Optional<Book> findById(Long id);
    void deleteById(Long id);
}
