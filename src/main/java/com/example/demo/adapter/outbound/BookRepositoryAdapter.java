package com.example.demo.adapter.outbound;

import com.example.demo.domain.Book;
import com.example.demo.domain.BookRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class BookRepositoryAdapter implements BookRepository {
    private final BookJpaRepository jpaRepository;

    public BookRepositoryAdapter(BookJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Book save(Book book) {
        BookEntity entity = BookMapper.toEntity(book);
        BookEntity saved = jpaRepository.save(entity);
        return BookMapper.toDomain(saved);
    }

    @Override
    public List<Book> findAll() {
        return jpaRepository.findAll().stream()
                .map(BookMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Book> findById(Long id) {
        return jpaRepository.findById(id).map(BookMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
