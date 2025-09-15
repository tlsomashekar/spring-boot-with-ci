package com.example.demo.adapter.outbound;

import com.example.demo.domain.Book;

public class BookMapper {
    public static BookEntity toEntity(Book book) {
        return new BookEntity(book.getId(), book.getTitle(), book.getAuthor());
    }

    public static Book toDomain(BookEntity entity) {
        if (entity == null) return null;
        return new Book(entity.getId(), entity.getTitle(), entity.getAuthor());
    }
}
