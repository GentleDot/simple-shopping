package net.gentledot.simpleshopping.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller(value = "bookController")
@RequestMapping("/api/books")
public class BookController {

    @GetMapping
    public String index(){
        return "index";
    }
}
