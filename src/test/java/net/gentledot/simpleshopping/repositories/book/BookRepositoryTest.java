package net.gentledot.simpleshopping.repositories.book;

import net.gentledot.simpleshopping.models.book.Book;
import net.gentledot.simpleshopping.models.book.BookCategory;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookRepositoryTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BookRepository bookRepository;

    @BeforeAll
    void setTestBook(){
        String category = "essay";
        String code = BookCategory.valueOf(category.toUpperCase()).getCode();
        String name = "testBook";
        String description = "테스트 상품";
        LocalDate publishDate = LocalDate.of(2020, 1, 1);

        Book book = new Book.Builder(code, name, publishDate)
                .description(description)
                .build();

        bookRepository.save(book);
    }

    @Test
    @DisplayName("book 정보 저장")
    @Order(1)
    void saveBookTest(){
        String category = "essay";
        String code = BookCategory.valueOf(category.toUpperCase()).getCode();
        String name = "new textbook";
        String description = "새 교과서 형식에 맞춘 테스트 상품";
        LocalDate publishDate = LocalDate.of(2019, 12, 12);

        Book book = new Book.Builder(code, name, publishDate)
                .description(description)
                .build();

        Book savedBook = bookRepository.save(book);

        assertThat(savedBook, is(notNullValue()));
        assertThat(savedBook.getCategoryCode(), is(code));
        assertThat(savedBook.getName(), is(name));
        assertThat(savedBook.getDescription().get(), is(description));
        assertThat(savedBook.getPublishDate(), is(publishDate));
        assertThat(savedBook.getCreateAt(), is(notNullValue()));

        log.debug("seq : {}", savedBook.getSeq());
        log.debug("생성 일시 : {}", savedBook.getCreateAt());
        log.debug("bookId : {}", savedBook.getId());
    }

    @Test
    @DisplayName("book 정보 저장 시 같은 ID는 허용되지 않음")
    @Order(2)
    void saveBookTestWithExistedId(){
        String category = "essay";
        String code = BookCategory.valueOf(category.toUpperCase()).getCode();
        String name = "testBook";
        String description = "테스트 상품";
        LocalDate publishDate = LocalDate.of(2020, 1, 1);

        Book book = new Book.Builder(code, name, publishDate)
                .description(description)
                .build();

        assertThrows(DataIntegrityViolationException.class,
                () -> bookRepository.save(book));
    }

    @Test
    @DisplayName("ID 값을 통한 book 정보 조회 ")
    @Order(3)
    void findByIdTest(){
        String category = "essay";
        String code = BookCategory.valueOf(category.toUpperCase()).getCode();
        String name = "testBook";
        String description = "테스트 상품";
        LocalDate publishDate = LocalDate.of(2020, 1, 1);

        // ess202001testBook
        String bookId = code + publishDate.getYear()
                + String.format("%02d", publishDate.getMonthValue())
                + name.replaceAll("\\s", "");

        Book book = bookRepository.findbyBookId(bookId).orElse(null);

        assertThat(book, is(notNullValue()));
        assertThat(book.getCategoryCode(), is(code));
        assertThat(book.getName(), is(name));
        assertThat(book.getDescription().get(), is(description));
        assertThat(book.getPublishDate(), is(publishDate));
        assertThat(book.getCreateAt(), is(notNullValue()));

        log.debug("seq : {}", book.getSeq());
        log.debug("생성 일시 : {}", book.getCreateAt());
        log.debug("bookId : {}", book.getId());
    }

    @Test
    @DisplayName("book 정보 변경 (+ 이름 변경 시 ID도 변경되어 저장) ")
    @Order(4)
    @Transactional
    void updateBookTest(){
        String category = "essay";
        String code = BookCategory.valueOf(category.toUpperCase()).getCode();
        String name = "testBook";
        String description = "테스트 상품";
        LocalDate publishDate = LocalDate.of(2020, 1, 1);

        // ess202001testBook
        String bookId = code + publishDate.getYear()
                + String.format("%02d", publishDate.getMonthValue())
                + name.replaceAll("\\s", "");

        Book book = bookRepository.findbyBookId(bookId).orElse(null);
        String oldId = book.getId();

        String newName = "anotherTestNote";
        String newDesc = "또테스트";
        Book changedBook = new Book.Builder(book)
                .name(newName)
                .description(newDesc)
                .build();


        String newBookId = code + publishDate.getYear()
                + String.format("%02d", publishDate.getMonthValue())
                + newName.replaceAll("\\s", "");
        boolean isIdNotExisted = bookRepository.findbyBookId(newBookId).isEmpty();

        bookRepository.update(changedBook);
        Book updatedBook = bookRepository.findbyBookId(newBookId).orElse(null);

        assertThat(isIdNotExisted, is(true));
        assertThat(updatedBook, is(notNullValue()));
        assertThat(updatedBook.getCategoryCode(), is(code));
        assertThat(updatedBook.getName().equals(name), is(false));
        assertThat(updatedBook.getName(), is(newName));
        assertThat(updatedBook.getDescription().get().equals(description), is(false));
        assertThat(updatedBook.getDescription().get(), is(newDesc));
        assertThat(updatedBook.getPublishDate(), is(publishDate));
        assertThat(updatedBook.getCreateAt(), is(notNullValue()));
        assertThat(updatedBook.getId().equals(oldId), is(false));

        log.debug("seq : {}", updatedBook.getSeq());
        log.debug("생성 일시 : {}", updatedBook.getCreateAt());
        log.debug("bookId : {}", updatedBook.getId());
    }

    @Test
    @DisplayName("book 정보 변경 (+ 이름 변경 시 ID도 변경되어 저장) ")
    @Order(5)
    @Transactional
    void getBookListByCategoryTest(){
        String category = "essay";
        String code = BookCategory.valueOf(category.toUpperCase()).getCode();
        LocalDate publishDate = LocalDate.of(2020, 1, 1);

        List<Book> allByCategoryCodeOrderByPublishDateDesc = bookRepository.findAllByCategoryCode(code);

        assertThat(allByCategoryCodeOrderByPublishDateDesc, is(notNullValue()));
        assertThat(allByCategoryCodeOrderByPublishDateDesc.get(0).getPublishDate(), is(publishDate));

        log.debug("목록 수 확인 : {}", allByCategoryCodeOrderByPublishDateDesc.size());

    }
}