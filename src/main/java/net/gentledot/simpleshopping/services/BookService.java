package net.gentledot.simpleshopping.services;

import net.gentledot.simpleshopping.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    List<Book> getBooks();
    Optional<Book> getBook(Integer id);

}
