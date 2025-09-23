package books.book;

public record Book(
        Long id,
        String title,
        Author author,
        Integer publishedYear
) {}
