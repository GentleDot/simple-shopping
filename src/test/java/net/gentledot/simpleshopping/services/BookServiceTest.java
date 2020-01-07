package net.gentledot.simpleshopping.services;

import net.gentledot.simpleshopping.domain.Book;
import net.gentledot.simpleshopping.repositories.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class BookServiceTest {

    @Autowired
    BookRepository bookRepository;

    @Test
    @DisplayName("저장된 Book 정보를 조회할 수 있는지 확인")
    void getBook() {
        Optional<Book> book = bookRepository.findById(1);

        String bookTitle = book.get().getTitle();

        assertThat(bookTitle, is("book1"));
    }
}