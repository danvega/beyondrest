package books.search;

import books.book.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);
    private final BookRepository bookRepository;

    public SearchController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @QueryMapping
    public List<Object> search(@Argument String text) {
        List<Object> results = new ArrayList<>();

        // Search books by title
        bookRepository.findAll().stream()
                .filter(book -> book.title().toLowerCase().contains(text.toLowerCase()))
                .forEach(results::add);

        // Search authors by name
        bookRepository.findAllAuthors().stream()
                .filter(author -> author.name().toLowerCase().contains(text.toLowerCase()))
                .forEach(results::add);

        return results;
    }

}
