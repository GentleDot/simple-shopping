package net.gentledot.simpleshopping.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller(value = "bookController")
public class BookController {

    @GetMapping("/")
    public String index(){
        return "index";
    }
}
