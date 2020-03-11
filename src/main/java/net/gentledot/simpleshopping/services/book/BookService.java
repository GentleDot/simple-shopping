package net.gentledot.simpleshopping.services.book;

import net.gentledot.simpleshopping.models.book.Book;
import net.gentledot.simpleshopping.models.book.BookCategory;
import net.gentledot.simpleshopping.models.response.book.BookResponse;
import net.gentledot.simpleshopping.repositories.book.BookRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static net.gentledot.simpleshopping.util.argumentHandleUtil.getBookIdFromArguments;
import static net.gentledot.simpleshopping.util.argumentHandleUtil.checkExpression;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public BookResponse addBook(String category, String name, LocalDate publishDate, String description) {
        checkExpression(StringUtils.isNotBlank(category), "카테고리는 반드시 존재해야 합니다.");
        checkExpression(StringUtils.isNotBlank(name), "상품명은 반드시 존재해야 합니다.");
        checkExpression(isNotEmpty(publishDate), "상품 발간일은 반드시 존재해야 합니다.");

        String categoryCode = BookCategory.valueOf(category.toUpperCase()).getCode();
        Book book = new Book.Builder(categoryCode, name, publishDate)
                .description(description)
                .build();

        Book savedBook = bookRepository.save(book);

        return new BookResponse.Builder(savedBook).build();
    }

    @Transactional
    public BookResponse editBookDescription(String category, String name, LocalDate publishDate, String description) {
        checkExpression(StringUtils.isNotBlank(category), "카테고리는 반드시 존재해야 합니다.");
        checkExpression(StringUtils.isNotBlank(name), "상품명은 반드시 존재해야 합니다.");
        checkExpression(isNotEmpty(publishDate), "상품 발간일은 반드시 존재해야 합니다.");

        String categoryCode = BookCategory.valueOf(category.toUpperCase()).getCode();
        String id = getBookIdFromArguments(categoryCode, publishDate, name);


        Book changedBook = bookRepository.findbyBookId(id)
                .orElseThrow(() -> new RuntimeException("해당 ID의 상품 정보가 없습니다."));
        changedBook.editDescription(description);

        bookRepository.update(changedBook);

        return new BookResponse.Builder(changedBook).build();
    }

    public BookResponse getBookInfo(String category, String name, LocalDate publishDate) {
        checkExpression(StringUtils.isNotBlank(category), "카테고리는 반드시 존재해야 합니다.");
        checkExpression(StringUtils.isNotBlank(name), "상품명은 반드시 존재해야 합니다.");
        checkExpression(isNotEmpty(publishDate), "상품 발간일은 반드시 존재해야 합니다.");

        String categoryCode = BookCategory.valueOf(category.toUpperCase()).getCode();
        String id = getBookIdFromArguments(categoryCode, publishDate, name);

        Book getBook = bookRepository.findbyBookId(id)
                .orElseThrow(() -> new RuntimeException("해당 ID의 상품 정보가 없습니다."));

        return new BookResponse.Builder(getBook).build();
    }

    public List<BookResponse> getBooksByCategory(String category) {
        checkExpression(StringUtils.isNotBlank(category), "카테고리는 반드시 존재해야 합니다.");

        String categoryCode = BookCategory.valueOf(category.toUpperCase()).getCode();

        List<Book> bookList = bookRepository.findAllByCategoryCode(categoryCode);

        List<BookResponse> responseList = new ArrayList<>();
        bookList.forEach(book -> responseList.add(new BookResponse.Builder(book).build()));

        return responseList;
    }

    @Transactional
    public boolean deleteBook(String category, String name, LocalDate publishDate) {
        checkExpression(StringUtils.isNotBlank(category), "카테고리는 반드시 존재해야 합니다.");
        checkExpression(StringUtils.isNotBlank(name), "상품명은 반드시 존재해야 합니다.");
        checkExpression(isNotEmpty(publishDate), "상품 발간일은 반드시 존재해야 합니다.");

        String categoryCode = BookCategory.valueOf(category.toUpperCase()).getCode();
        String id = getBookIdFromArguments(categoryCode, publishDate, name);

        Book getBook = bookRepository.findbyBookId(id)
                .orElseThrow(() -> new RuntimeException("해당 ID의 상품 정보가 없습니다."));
        bookRepository.delete(getBook);

        return bookRepository.findById(getBook.getSeq()).isEmpty();
    }
}
