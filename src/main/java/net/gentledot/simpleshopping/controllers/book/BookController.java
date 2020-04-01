package net.gentledot.simpleshopping.controllers.book;

import net.gentledot.simpleshopping.controllers.BaseController;
import net.gentledot.simpleshopping.models.request.BookRequest;
import net.gentledot.simpleshopping.models.response.ApiResult;
import net.gentledot.simpleshopping.models.response.book.BookResponse;
import net.gentledot.simpleshopping.services.book.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookController extends BaseController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping(path = "book/regist")
    public ApiResult<BookResponse> registBook(@RequestBody BookRequest request) {
        return ApiResult.ok(bookService.addBook(request.getCategory(),
                request.getName(),
                request.getDate(),
                request.getDescription()));
    }

    @PatchMapping(path = "book/update")
    public ApiResult<BookResponse> updateBookDescription(@RequestBody BookRequest request) {
        return ApiResult.ok(bookService.editBookDescription(request.getCategory(),
                request.getName(),
                request.getDate(),
                request.getDescription()));
    }

    @GetMapping(path = "book/info")
    public ApiResult<BookResponse> getBookInfo(@RequestBody BookRequest request) {
        return ApiResult.ok(bookService.getBookInfo(request.getCategory(),
                request.getName(),
                request.getDate()));
    }

    @GetMapping(path = "book/list/{category}")
    public ApiResult<List<BookResponse>> getBookListByCategory(@PathVariable(value = "category") String category) {
        return ApiResult.ok(bookService.getBooksByCategory(category));
    }

    @DeleteMapping(path = "book/delete")
    public ApiResult<Boolean> deleteBook(@RequestBody BookRequest request) {
        return ApiResult.ok(bookService.deleteBook(request.getCategory(),
                request.getName(),
                request.getDate()));
    }
}
