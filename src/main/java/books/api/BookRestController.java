package books.api;

import books.book.Book;
import books.book.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookRestController {

    private static final Logger log = LoggerFactory.getLogger(BookRestController.class);
    private final BookRepository bookRepository;

    public BookRestController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // :::::::::::::: CRUD METHODS ::::::::::::::

    @GetMapping("/")
    public List<Book> books() {
        return bookRepository.findAll();
    }


    // :::::::::::::: AVOID REST API EXPLOSION OF ENDPOINTS  ::::::::::::::

    // SERVICE A: Needs Endpoint that only returns Book title (No more over-fetching)

    // SERVICE B: Needs a single endpoint to return book details and reviews (Multiple Request for multiple resources)

    // SERVICE C: Needs Book with Author Data but only these fields (API Explosion)


    // :::::::::::::: WE NEED TO VERSION OUR APIs (In the past I would have avoided this) ::::::::::::::

    /*
        Request header
        Request parameter
        Path segment
        Media type parameter
     */

    @GetMapping(path = "/", version = "1.0")
    public List<Book> booksV1() {
        log.info("Getting all books using version {}", "1.0");
        return bookRepository.findAll();
    }

    @GetMapping(path = "/", version = "1.2+")
    public List<Book> booksV12() {
        log.info("Getting all books using version {}", "1.2+");
        return bookRepository.findAll();
    }

    @GetMapping(path = "/", version = "2.0")
    public ResponseEntity<List<Book>> booksV2() {
        log.info("Getting all books using version {}", "2.0");
        return ResponseEntity.ok(bookRepository.findAll());
    }

}
