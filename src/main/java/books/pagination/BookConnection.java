package books.pagination;

import java.util.List;

public record BookConnection(
    List<BookEdge> edges,
    PageInfo pageInfo
) {}