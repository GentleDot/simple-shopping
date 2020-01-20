package net.gentledot.simpleshopping.controllers;

import net.gentledot.simpleshopping.dto.BookDto;
import net.gentledot.simpleshopping.services.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller(value = "bookController")
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public String index(Model model) {
        String tempStatus = "NEW";
        Map<String, List<BookDto>> bookList = bookService.getBooksByStatus(tempStatus);
        model.addAttribute("books", bookList);
        model.addAttribute("status", tempStatus);

        return "index";
    }

}
