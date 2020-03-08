package net.gentledot.simpleshopping.models.book;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    @DisplayName("카테고리는 enum으로 관리할 수 있고 entity에는 getCode() 값을 사용")
    void checkCategoryTest() {
        String category = "essay";
        String code = BookCategory.valueOf(category.toUpperCase()).getCode();

        log.debug("category_code : {}", code);
        log.debug("code를 통해 category 확인 : {}", BookCategory.getCategory(code));
        Class<BookCategory> bookCategoryClass = BookCategory.class;
        log.debug("enum 전체 확인_class의 getEnumConstants : {}", Arrays.toString(bookCategoryClass.getEnumConstants()));
        log.debug("enum 전체 확인_values : {}", Arrays.toString(BookCategory.values()));
    }

    @Test
    @DisplayName("Book 객체 생성")
    void createBookTest() {
        String category = "textbook";
        String categoryCode = BookCategory.valueOf(category.toUpperCase()).getCode();
        String name = "나를 위한 연습장";
        LocalDate date = LocalDate.now();
        Book book = new Book(categoryCode, name, date);

        log.debug("빈스페이스가 없어지는지 확인 : {}", name.replaceAll("\\s", ""));

        assertThat(book, is(notNullValue()));
        assertThat(book.getSeq(), is(0L));
        assertThat(book.getCategoryCode(), is(categoryCode));
        assertThat(book.getName(), is(name));
        assertThat(book.getPublishDate(), is(date));
        assertThat(book.getCreateAt(), is(notNullValue()));
        assertThat(book.getId(),
                is(book.getCategoryCode() +
                        book.getPublishDate().getYear() +
                        String.format("%02d", book.getPublishDate().getMonthValue()) +
                        name.replaceAll("\\s", "")));

        log.debug("생성 일자 : {}", book.getCreateAt());
        log.debug("발간 일자 : {}", book.getPublishDate());
        log.debug("생성된 ID : {}", book.getId());
    }

    @Test
    @DisplayName("Book 객체를 복사할 때 이름이 바뀌면 ID도 변경되도록 설정")
    void createBookTestWithChangeName() {
        String category = "textbook";
        String categoryCode = BookCategory.valueOf(category.toUpperCase()).getCode();
        String name = "나를 위한 연습장";
        LocalDate date = LocalDate.now();
        Book book = new Book(categoryCode, name, date);

        String oldId = book.getId();
        Long oldSeq = book.getSeq();
        String oldCat = book.getCategoryCode();
        String description = "한 문장씩 암기하면 하루가 나아진다.";

        Book newBook = new Book.Builder(book)
                .description(description)
                .build();

        assertThat(newBook, is(notNullValue()));
        assertThat(newBook.getSeq(), is(oldSeq));
        assertThat(newBook.getCategoryCode(), is(oldCat));
        assertThat(newBook.getPublishDate(), is(date));
        assertThat(newBook.getCreateAt(), is(notNullValue()));
        assertThat(newBook.getId(),
                is(newBook.getCategoryCode() +
                        newBook.getPublishDate().getYear() +
                        String.format("%02d", newBook.getPublishDate().getMonthValue()) +
                        name.replaceAll("\\s", "")));

        log.debug("생성 일자 : {}", newBook.getCreateAt());
        log.debug("발간 일자 : {}", newBook.getPublishDate());
        log.debug("생성된 ID : {}", newBook.getId());
    }

    @Test
    @DisplayName("Book 객체를 생성할 때 관리되지 않는 category_code를 입력하면 exception 발생")
    void createBookTestWithNotCategoryCode() {
        String category = "allRounder";

        assertThrows(IllegalArgumentException.class,
                () -> BookCategory.valueOf(category.toUpperCase()).getCode());

        String name = "나를 위한 연습장";
        LocalDate date = LocalDate.now();
        String categoryCode = "err";

        var exception = assertThrows(IllegalArgumentException.class,
                () -> new Book(categoryCode, name, date));
        assertThat(exception.getMessage(), is(String.format("해당 코드 (%s) 에 해당하는 category는 존재하지 않습니다.", categoryCode)));
    }
}