package books.book;

import books.pagination.BookConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class BooksController {

    private static final Logger log = LoggerFactory.getLogger(BooksController.class);
    private final BookRepository bookRepository;

    public BooksController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Queries

    @QueryMapping
    public List<Author> authors() {
        return bookRepository.findAllAuthors();
    }

    @SchemaMapping(typeName = "Query", value = "books")
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    @QueryMapping
    public Book book(@Argument Long id) {
        return bookRepository.findById(id);
    }

    @QueryMapping
    public List<Book> booksWithFilter(@Argument BookFilter filter) {
        if (filter == null) {
            return bookRepository.findAll();
        }

        return bookRepository.findAll().stream()
                .filter(book -> matchesFilter(book, filter))
                .collect(Collectors.toList());
    }

    private boolean matchesFilter(Book book, BookFilter filter) {
        if (filter.authorName() != null &&
                !book.author().name().toLowerCase().contains(filter.authorName().toLowerCase())) {
            return false;
        }
        if (filter.publishedAfter() != null &&
                book.publishedYear() < filter.publishedAfter()) {
            return false;
        }
        return true;
    }

    // Mutations

    @MutationMapping
    public Book addBook(@Argument String title, @Argument String authorName, @Argument Integer publishedYear) {
        // get author
        Author authorById = bookRepository.findAuthorByName(authorName);
        return bookRepository.createBook(title, authorById, publishedYear);
    }

    @MutationMapping
    public boolean deleteBook(@Argument Long id) {
        return bookRepository.deleteBookById(id);
    }

    // BATCH MAPPING

    @SchemaMapping
    public List<Book> books(Author author) throws InterruptedException {

        log.info("Thread: {}", Thread.currentThread());

        // what if this call was to another service?
        // sleep for 1 second to demonstrate delay
        TimeUnit.SECONDS.sleep(1);

        log.info("Loading books for author: {}", author.name());
        return bookRepository.findAll().stream()
                .filter(book -> book.author().id().equals(author.id()))
                .collect(Collectors.toList());
    }


    // PAGINATION

    @QueryMapping
    public BookConnection booksPaginated(@Argument Integer first, @Argument String after,
                                         @Argument Integer last, @Argument String before) {
        return bookRepository.findBooksPaginated(first, after, last, before);
    }


}
