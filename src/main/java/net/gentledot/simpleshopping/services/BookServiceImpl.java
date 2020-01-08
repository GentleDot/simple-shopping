package net.gentledot.simpleshopping.services;

import net.gentledot.simpleshopping.common.ObjectMappingUtil;
import net.gentledot.simpleshopping.domain.Book;
import net.gentledot.simpleshopping.domain.BookStatus;
import net.gentledot.simpleshopping.dto.BookDto;
import net.gentledot.simpleshopping.repositories.BookRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Map<String, List<BookDto>> getBooksByStatus(String status) {
        Map<String, List<BookDto>> bookListMap = new HashMap<>();
        BookStatus statusValue = BookStatus.valueOf(status);

        List<Book> bookListByStatus =  bookRepository.findAllByStatus(statusValue);
        List<BookDto> bookListDtoByStatus = ObjectMappingUtil.mapAll(bookListByStatus, BookDto.class);

        bookListMap.put(status, bookListDtoByStatus);

        return bookListMap;
    }
}
