package net.gentledot.simpleshopping.services;

import net.gentledot.simpleshopping.common.ObjectMappingUtil;
import net.gentledot.simpleshopping.domain.Book;
import net.gentledot.simpleshopping.domain.BookStatus;
import net.gentledot.simpleshopping.dto.BookDto;
import net.gentledot.simpleshopping.repositories.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    BookRepository bookRepository;

    @Test
    @DisplayName("꺼내온 List<Book> 객체를 List<BookDto> 객체로 변환되는지 확인")
    void getBookListDtoFromBookList() {
        // given
        String status = "NEW";
        List<Book> bookList = makeBookList(status);

        // when
        when(bookRepository.findAllByStatus(BookStatus.valueOf(status))).thenReturn(bookList);

        Map<String, List<BookDto>> bookListMap = new HashMap<>();

        List<Book> bookListByStatus = bookRepository.findAllByStatus(BookStatus.valueOf(status));
        List<BookDto> bookListDtoByStatus = ObjectMappingUtil.mapAll(bookListByStatus, BookDto.class);

        bookListMap.put(status, bookListDtoByStatus);

        // then
        assertAll(
                () -> assertThat(bookListDtoByStatus, notNullValue()),
                () -> assertThat(bookListDtoByStatus.get(0).getClass(), is(BookDto.class)),
                () -> assertThat(bookListDtoByStatus.get(0).getTitle(), is("테스트 북1")),
                () -> assertThat(bookListMap.get(status), is(notNullValue()))
        );


    }

    private List<Book> makeBookList(String status) {
        Book newBook1 = Book.builder()
                .title("테스트 북1")
                .status(BookStatus.valueOf(status))
                .build();

        Book newBook2 = Book.builder()
                .title("테스트 북2")
                .status(BookStatus.valueOf(status))
                .build();

        return List.of(newBook1, newBook2);
    }

}