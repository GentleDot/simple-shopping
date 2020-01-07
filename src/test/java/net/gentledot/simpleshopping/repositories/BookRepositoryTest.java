package net.gentledot.simpleshopping.repositories;

import net.gentledot.simpleshopping.domain.Book;
import net.gentledot.simpleshopping.domain.BookStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookRepositoryTest {

    @Autowired
    BookRepository bookRepository;


    @Test
    @DisplayName("저장된 Book 정보를 조회할 수 있는지 확인")
    void getBook() {
        Optional<Book> book = bookRepository.findById(1);

        String bookTitle = book.get().getTitle();

        assertThat(bookTitle, is("book1"));
    }

    @Test
    @DisplayName("저장된 모든 Book 정보를 조회할 수 있는지 확인")
    void getBooksAll() {
        List<Book> books = bookRepository.findAll();

        assertThat(books, notNullValue());
    }


    @Test
    @DisplayName("카테고리별 모든 Book 정보를 조회할 수 있는지 확인")
    void getBooksAllbyCategory() {
        BookStatus status = BookStatus.NEW;

        List<Book> books = bookRepository.findAllByStatus(status);
        String bookStatus = books.get(0).getStatus().toString();

        assertAll(
                () -> assertThat(books, notNullValue()),
                () -> assertThat(bookStatus, is("NEW"))
        );

    }
}