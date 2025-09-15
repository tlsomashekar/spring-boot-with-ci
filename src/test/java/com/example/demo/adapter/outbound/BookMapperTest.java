package com.example.demo.adapter.outbound;

import com.example.demo.domain.Book;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookMapperTest {
    @Test
    void testToEntityAndToDomain() {
        Book book = new Book(1L, "Title", "Author");
        BookEntity entity = BookMapper.toEntity(book);
        assertEquals(book.getId(), entity.getId());
        assertEquals(book.getTitle(), entity.getTitle());
        assertEquals(book.getAuthor(), entity.getAuthor());

        Book mapped = BookMapper.toDomain(entity);
        assertEquals(book.getId(), mapped.getId());
        assertEquals(book.getTitle(), mapped.getTitle());
        assertEquals(book.getAuthor(), mapped.getAuthor());
    }
}
