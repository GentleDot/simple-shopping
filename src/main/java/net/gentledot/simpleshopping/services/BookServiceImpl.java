package net.gentledot.simpleshopping.services;

import net.gentledot.simpleshopping.domain.Book;
import net.gentledot.simpleshopping.repositories.BookRepository;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Optional<Book> getBook(Integer id) {
        return bookRepository.findById(id);
    }
}
