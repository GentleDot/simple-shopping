package net.gentledot.simpleshopping.services;

import net.gentledot.simpleshopping.dto.BookDto;

import java.util.List;
import java.util.Map;

public interface BookService {
    Map<String, List<BookDto>> getBooksByStatus(String status);
}
