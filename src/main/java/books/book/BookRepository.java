package books.book;

import books.pagination.BookConnection;
import books.pagination.BookEdge;
import books.pagination.PageInfo;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class BookRepository {

    private final List<Book> books = new ArrayList<>();
    private final List<Author> authors = new ArrayList<>();
    private final AtomicLong bookIdCounter = new AtomicLong(0);
    private final AtomicLong authorIdCounter = new AtomicLong(0);

    // BOOKS ====================================================================================================

    public List<Book> findAll() {
        return books;
    }

    public Book findById(Long id) {
        return books.stream()
                .filter(book -> book.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Book> findBooksByAuthorIds(List<Long> authorIds) {
        return books.stream()
                .filter(book -> authorIds.contains(book.author().id()))
                .collect(Collectors.toList());
    }

    public Book createBook(String title, Author author, Integer publishedYear) {
        Long id = bookIdCounter.incrementAndGet();
        Book book = new Book(id, title, author, publishedYear);
        books.add(book);
        return book;
    }

    public boolean deleteBookById(Long id) {
        return books.removeIf(book -> book.id().equals(id));
    }

    // AUTHORS ===================================================================================================

    public List<Author> findAllAuthors() {
        return authors;
    }

    public Author findAuthorById(Long id) {
        return authors.stream()
                .filter(author -> author.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Author findAuthorByName(String name) {
        return authors.stream()
                .filter( a -> a.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public Author createAuthor(String name) {
        Long id = authorIdCounter.incrementAndGet();
        Author author = new Author(id, name);
        authors.add(author);
        return author;
    }


    // PAGINATION ================================================================================================

    public BookConnection findBooksPaginated(Integer first, String after, Integer last, String before) {
        List<Book> allBooks = new ArrayList<>(books);

        // Handle cursor-based filtering
        int startIndex = 0;
        int endIndex = allBooks.size();

        if (after != null) {
            Long afterId = decodeCursor(after);
            for (int i = 0; i < allBooks.size(); i++) {
                if (allBooks.get(i).id().equals(afterId)) {
                    startIndex = i + 1;
                    break;
                }
            }
        }

        if (before != null) {
            Long beforeId = decodeCursor(before);
            for (int i = 0; i < allBooks.size(); i++) {
                if (allBooks.get(i).id().equals(beforeId)) {
                    endIndex = i;
                    break;
                }
            }
        }

        // Apply pagination limits
        if (first != null) {
            if (first <= 0) {
                endIndex = startIndex; // Return empty result for first: 0 or negative
            } else {
                endIndex = Math.min(startIndex + first, endIndex);
            }
        }

        if (last != null) {
            if (last <= 0) {
                startIndex = endIndex; // Return empty result for last: 0 or negative
            } else {
                startIndex = Math.max(endIndex - last, startIndex);
            }
        }

        // Get the page slice
        List<Book> pageBooks = allBooks.subList(Math.max(0, startIndex), Math.min(allBooks.size(), endIndex));

        // Create edges
        List<BookEdge> edges = pageBooks.stream()
                .map(book -> new BookEdge(encodeCursor(book.id()), book))
                .collect(Collectors.toList());

        // Calculate page info
        boolean hasNextPage = endIndex < allBooks.size();
        boolean hasPreviousPage = startIndex > 0;
        String startCursor = edges.isEmpty() ? null : edges.get(0).cursor();
        String endCursor = edges.isEmpty() ? null : edges.get(edges.size() - 1).cursor();

        PageInfo pageInfo = new PageInfo(hasNextPage, hasPreviousPage, startCursor, endCursor);

        return new BookConnection(edges, pageInfo);
    }

    private String encodeCursor(Long id) {
        return Base64.getEncoder().encodeToString(id.toString().getBytes());
    }

    private Long decodeCursor(String cursor) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(cursor);
            return Long.parseLong(new String(decodedBytes));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid cursor: " + cursor);
        }
    }

    // DATA ======================================================================================================

    @PostConstruct
    public void init() {

        // Create Me and Nate
        Author nateSchutta = new Author(authorIdCounter.incrementAndGet(), "Nate Schutta");
        Author danVega = new Author(authorIdCounter.incrementAndGet(), "Dan Vega");

        // Core Java Authors
        Author joshuaBloch = new Author(authorIdCounter.incrementAndGet(), "Joshua Bloch");
        Author herbertSchildt = new Author(authorIdCounter.incrementAndGet(), "Herbert Schildt");
        Author raoulgabrielUrma = new Author(authorIdCounter.incrementAndGet(), "Raoul-Gabriel Urma");
        Author brianGoetz = new Author(authorIdCounter.incrementAndGet(), "Brian Goetz");

        // Spring Framework Authors
        Author craigWalls = new Author(authorIdCounter.incrementAndGet(), "Craig Walls");
        Author gregTurnquist = new Author(authorIdCounter.incrementAndGet(), "Greg Turnquist");
        Author markHeckler = new Author(authorIdCounter.incrementAndGet(), "Mark Heckler");
        Author thomasVitale = new Author(authorIdCounter.incrementAndGet(), "Thomas Vitale");
        Author joshLong = new Author(authorIdCounter.incrementAndGet(), "Josh Long");

        // Spring Ecosystem Authors
        Author dineshRajput = new Author(authorIdCounter.incrementAndGet(), "Dinesh Rajput");
        Author johnCarnell = new Author(authorIdCounter.incrementAndGet(), "John Carnell");
        Author laurentiuSpilca = new Author(authorIdCounter.incrementAndGet(), "Laurentiu Spilca");
        Author petriKainulainen = new Author(authorIdCounter.incrementAndGet(), "Petri Kainulainen");

        // Architecture & Design Authors
        Author rodJohnson = new Author(authorIdCounter.incrementAndGet(), "Rod Johnson");
        Author martinFowler = new Author(authorIdCounter.incrementAndGet(), "Martin Fowler");
        Author nealFord = new Author(authorIdCounter.incrementAndGet(), "Neal Ford");

        // Modern Java Authors
        Author kenKousen = new Author(authorIdCounter.incrementAndGet(), "Ken Kousen");
        Author dmitryJemerov = new Author(authorIdCounter.incrementAndGet(), "Dmitry Jemerov");
        Author venkatSubramaniam = new Author(authorIdCounter.incrementAndGet(), "Venkat Subramaniam");

        // Testing & Best Practices Authors
        Author petarTahchiev = new Author(authorIdCounter.incrementAndGet(), "Petar Tahchiev");
        Author robertMartin = new Author(authorIdCounter.incrementAndGet(), "Robert C. Martin");
        Author andrewHunt = new Author(authorIdCounter.incrementAndGet(), "Andrew Hunt");

        // Add all authors to the collection
        authors.addAll(List.of(
                danVega, nateSchutta,
                joshuaBloch, herbertSchildt, raoulgabrielUrma, brianGoetz,
                craigWalls, gregTurnquist, markHeckler, thomasVitale, joshLong,
                dineshRajput, johnCarnell, laurentiuSpilca, petriKainulainen,
                rodJohnson, martinFowler, nealFord,
                kenKousen, dmitryJemerov, venkatSubramaniam,
                petarTahchiev, robertMartin, andrewHunt
        ));

        // Create books
        books.addAll(List.of(
                // Core Java
                new Book(bookIdCounter.incrementAndGet(), "Effective Java", joshuaBloch, 2017),
                new Book(bookIdCounter.incrementAndGet(), "Java: The Complete Reference", herbertSchildt, 2021),
                new Book(bookIdCounter.incrementAndGet(), "Modern Java in Action", raoulgabrielUrma, 2018),
                new Book(bookIdCounter.incrementAndGet(), "Java Concurrency in Practice", brianGoetz, 2006),

                // Spring Framework & Boot
                new Book(bookIdCounter.incrementAndGet(), "Spring in Action", craigWalls, 2020),
                new Book(bookIdCounter.incrementAndGet(), "Spring Boot in Action", craigWalls, 2015),
                new Book(bookIdCounter.incrementAndGet(), "Learning Spring Boot 3.0", gregTurnquist, 2022),
                new Book(bookIdCounter.incrementAndGet(), "Spring Boot: Up and Running", markHeckler, 2021),
                new Book(bookIdCounter.incrementAndGet(), "Cloud Native Spring in Action", thomasVitale, 2021),
                new Book(bookIdCounter.incrementAndGet(), "Reactive Spring", joshLong, 2020),

                // Spring Ecosystem & Microservices
                new Book(bookIdCounter.incrementAndGet(), "Building Microservices with Spring Boot", dineshRajput, 2020),
                new Book(bookIdCounter.incrementAndGet(), "Spring Microservices in Action", johnCarnell, 2021),
                new Book(bookIdCounter.incrementAndGet(), "Spring Security in Action", laurentiuSpilca, 2020),
                new Book(bookIdCounter.incrementAndGet(), "Spring Data JPA", petriKainulainen, 2019),

                // Architecture & Design Patterns
                new Book(bookIdCounter.incrementAndGet(), "Expert One-on-One J2EE Design and Development", rodJohnson, 2002),
                new Book(bookIdCounter.incrementAndGet(), "Patterns of Enterprise Application Architecture", martinFowler, 2002),
                new Book(bookIdCounter.incrementAndGet(), "Refactoring", martinFowler, 2019),
                new Book(bookIdCounter.incrementAndGet(), "Building Evolutionary Architectures", nealFord, 2017),

                // Modern Java Development
                new Book(bookIdCounter.incrementAndGet(), "Modern Java Recipes", kenKousen, 2017),
                new Book(bookIdCounter.incrementAndGet(), "Kotlin in Action", dmitryJemerov, 2017),
                new Book(bookIdCounter.incrementAndGet(), "Java 8 in Action", raoulgabrielUrma, 2014),
                new Book(bookIdCounter.incrementAndGet(), "Functional Programming in Java", venkatSubramaniam, 2014),

                // Testing & Best Practices
                new Book(bookIdCounter.incrementAndGet(), "JUnit in Action", petarTahchiev, 2020),
                new Book(bookIdCounter.incrementAndGet(), "Clean Code", robertMartin, 2008),
                new Book(bookIdCounter.incrementAndGet(), "The Pragmatic Programmer", andrewHunt, 2019)
        ));
    }

}
