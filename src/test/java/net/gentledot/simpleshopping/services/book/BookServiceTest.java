package net.gentledot.simpleshopping.services.book;

import net.gentledot.simpleshopping.models.book.Book;
import net.gentledot.simpleshopping.models.book.BookCategory;
import net.gentledot.simpleshopping.models.response.book.BookResponse;
import net.gentledot.simpleshopping.repositories.book.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    BookRepository bookRepository;

    @Test
    @DisplayName("Book 정보 저장")
    void addBookTest() {
        // given
        String category = "ESSAY";
        String categoryCode = BookCategory.valueOf(category).getCode();
        String name = "testName";
        String description = "testDesc";
        LocalDate publishDate = LocalDate.of(2019, 2, 2);

        Book book = getTestBook(categoryCode, name, description, publishDate);

        given(bookRepository.save(any(Book.class))).willReturn(book);

        // when
        Book savedBook = bookRepository.save(book);
        BookResponse response = new BookResponse.Builder(savedBook).build();

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getCategory(), is(BookCategory.getCategory(categoryCode)));
        assertThat(response.getName(), is(name));
        assertThat(response.getDescription().get(), is(description));
        assertThat(response.getPublishDate(), is(publishDate));
    }

    @Test
    @DisplayName("Book 정보 수정")
    void editBookDescriptionTest() {
        // given
        String category = "ESSAY";
        String categoryCode = BookCategory.valueOf(category).getCode();
        String name = "testName";
        String description = "testDesc";
        LocalDate publishDate = LocalDate.of(2019, 2, 2);
        String bookId = getBookId(categoryCode, publishDate, name);

        Book getBook = getTestBook(categoryCode, name, description, publishDate);

        given(bookRepository.findbyBookId(bookId)).willReturn(Optional.ofNullable(getBook));

        // when
        Book changedBook = bookRepository.findbyBookId(bookId).orElse(null);
        String newDesc = "description changed.";
        changedBook.editDescription(newDesc);

        BookResponse response = new BookResponse.Builder(changedBook).build();

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getCategory(), is(BookCategory.getCategory(categoryCode)));
        assertThat(response.getName(), is(name));
        assertThat(response.getDescription().get(), is(newDesc));
        assertThat(response.getPublishDate(), is(publishDate));
    }

    @Test
    void getBookInfo() {
        // given
        String category = "ESSAY";
        String categoryCode = BookCategory.valueOf(category).getCode();
        String name = "testName";
        String description = "testDesc";
        LocalDate publishDate = LocalDate.of(2019, 2, 2);

        Book book = getTestBook(categoryCode, name, description, publishDate);
        String bookId = getBookId(categoryCode, publishDate, name);

        lenient().when(bookRepository.findbyBookId(anyString())).thenReturn(Optional.ofNullable(book));

        Book getBook = bookRepository.findbyBookId(bookId).orElse(null);
        BookResponse response = new BookResponse.Builder(getBook).build();

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getCategory(), is(BookCategory.getCategory(categoryCode)));
        assertThat(response.getName(), is(name));
        assertThat(response.getDescription().get(), is(description));
        assertThat(response.getPublishDate(), is(publishDate));
    }

    @Test
    @DisplayName("Book List 조회 - 카테고리별")
    void getBooksByCategoryTest() {
        // given
        String category = "ESSAY";
        String categoryCode = BookCategory.valueOf(category).getCode();
        String name = "testName";
        String description = "testDesc";
        LocalDate publishDate = LocalDate.of(2019, 2, 2);

        Book testBook = getTestBook(categoryCode, name, description, publishDate);

        given(bookRepository.findAllByCategoryCode(categoryCode)).willReturn(Arrays.asList(testBook));

        // when
        List<Book> bookList = bookRepository.findAllByCategoryCode(categoryCode);
        List<BookResponse> responseList = new ArrayList<>();
        bookList.forEach(book -> responseList.add(new BookResponse.Builder(book).build()));

        // then
        assertThat(responseList, is(notNullValue()));
        assertThat(responseList.get(0).getCategory(), is(BookCategory.getCategory(categoryCode)));
        assertThat(responseList.get(0).getName(), is(name));
        assertThat(responseList.get(0).getDescription().get(), is(description));
        assertThat(responseList.get(0).getPublishDate(), is(publishDate));
    }

    @Test
    @DisplayName("Book 정보 삭제")
    void deleteBookTest() {
        // given
        String category = "ESSAY";
        String categoryCode = BookCategory.valueOf(category).getCode();
        String name = "testName";
        String description = "testDesc";
        LocalDate publishDate = LocalDate.of(2019, 2, 2);

        Book book = getTestBook(categoryCode, name, description, publishDate);
        String bookId = getBookId(categoryCode, publishDate, name);

        given(bookRepository.findbyBookId(anyString())).willReturn(Optional.ofNullable(book));

        // when
        Book targetBook = bookRepository.findbyBookId(bookId).orElseThrow(() -> new RuntimeException("해당 id의 Book 없음."));
        bookRepository.delete(targetBook);
        Optional<Book> result = Optional.empty();

        // then
        assertThat(result.isEmpty(), is(true));

    }

    private Book getTestBook(String categoryCode, String name, String description, LocalDate publishDate) {
        return new Book.Builder(categoryCode, name, publishDate)
                .description(description)
                .build();
    }

    private String getBookId(String categoryCode, LocalDate publishDate, String name) {
        return categoryCode + publishDate.getYear()
                + String.format("%02d", publishDate.getMonthValue())
                + name.replaceAll("\\s", "");
    }
}