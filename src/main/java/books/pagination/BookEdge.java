package books.pagination;

import books.book.Book;

public record BookEdge(
    String cursor,
    Book node
) {}