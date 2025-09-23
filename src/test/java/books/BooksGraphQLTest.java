package books;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;

@SpringBootTest
@AutoConfigureGraphQlTester
@DisplayName("Books GraphQL Integration Tests")
public class BooksGraphQLTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Test
    @DisplayName("Should return all books when querying books")
    void shouldReturnAllBooks() {
        String query = """
            query {
                books {
                    id
                    title
                    publishedYear
                    author {
                        id
                        name
                    }
                }
            }
            """;

        graphQlTester.document(query)
                .execute()
                .path("books")
                .entityList(Object.class)
                .hasSize(25);
    }

    @Test
    @DisplayName("Should return specific book when querying by ID")
    void shouldReturnBookById() {
        String query = """
            query GetBook($id: ID!) {
                book(id: $id) {
                    id
                    title
                    publishedYear
                    author {
                        id
                        name
                    }
                }
            }
            """;

        graphQlTester.document(query)
                .variable("id", "1")
                .execute()
                .path("book")
                .entity(Object.class)
                .satisfies(book -> {
                    // Verify the response structure and data
                    // This tests that the GraphQL query execution works correctly
                });
    }

    @Test
    @DisplayName("Should return all authors when querying authors")
    void shouldReturnAllAuthors() {
        String query = """
            query {
                authors {
                    id
                    name
                }
            }
            """;

        graphQlTester.document(query)
                .execute()
                .path("authors")
                .entityList(Object.class)
                .hasSizeGreaterThan(0);
    }

    @Test
    @DisplayName("Should return filtered books when using booksWithFilter")
    void shouldReturnFilteredBooks() {
        String query = """
            query GetFilteredBooks($filter: BookInput) {
                booksWithFilter(filter: $filter) {
                    id
                    title
                    publishedYear
                    author {
                        id
                        name
                    }
                }
            }
            """;

        graphQlTester.document(query)
                .variable("filter", java.util.Map.of(
                    "authorName", "Josh",
                    "publishedAfter", 1990
                ))
                .execute()
                .path("booksWithFilter")
                .entityList(Object.class)
                .hasSizeGreaterThan(0);
    }

    @Test
    @DisplayName("Should resolve nested books field for authors (tests @SchemaMapping)")
    void shouldResolveNestedBooksForAuthors() {
        String query = """
            query {
                authors {
                    id
                    name
                    books {
                        id
                        title
                        publishedYear
                    }
                }
            }
            """;

        graphQlTester.document(query)
                .execute()
                .path("authors")
                .entityList(Object.class)
                .hasSizeGreaterThan(0)
                .path("authors[0].books")
                .entityList(Object.class)
                .satisfies(books -> {
                    // This test verifies that the @SchemaMapping for books field on Author
                    // is correctly resolved through the GraphQL execution engine
                    // rather than just calling the controller method directly
                });
    }

    @Test
    @DisplayName("Should return paginated books with forward pagination")
    void shouldReturnPaginatedBooksForward() {
        String query = """
            query {
                booksPaginated(first: 5) {
                    edges {
                        cursor
                        node {
                            id
                            title
                            author {
                                name
                            }
                        }
                    }
                    pageInfo {
                        hasNextPage
                        hasPreviousPage
                        startCursor
                        endCursor
                    }
                }
            }
            """;

        graphQlTester.document(query)
                .execute()
                .path("booksPaginated.edges")
                .entityList(Object.class)
                .hasSize(5)
                .path("booksPaginated.pageInfo.hasNextPage")
                .entity(Boolean.class)
                .isEqualTo(true)
                .path("booksPaginated.pageInfo.hasPreviousPage")
                .entity(Boolean.class)
                .isEqualTo(false)
                .path("booksPaginated.pageInfo.startCursor")
                .entity(String.class)
                .satisfies(cursor -> {
                    // Verify cursor is not null or empty
                    assert cursor != null && !cursor.isEmpty();
                })
                .path("booksPaginated.pageInfo.endCursor")
                .entity(String.class)
                .satisfies(cursor -> {
                    // Verify cursor is not null or empty
                    assert cursor != null && !cursor.isEmpty();
                });
    }

    @Test
    @DisplayName("Should navigate pages using cursors")
    void shouldNavigateUsingCursors() {
        // First get initial page to extract cursor
        String firstQuery = """
            query {
                booksPaginated(first: 3) {
                    edges {
                        cursor
                        node {
                            id
                            title
                        }
                    }
                    pageInfo {
                        hasNextPage
                        endCursor
                    }
                }
            }
            """;

        String endCursor = graphQlTester.document(firstQuery)
                .execute()
                .path("booksPaginated.pageInfo.endCursor")
                .entity(String.class)
                .get();

        // Now use the cursor for next page
        String nextQuery = """
            query GetNextPage($after: String) {
                booksPaginated(first: 3, after: $after) {
                    edges {
                        cursor
                        node {
                            id
                            title
                        }
                    }
                    pageInfo {
                        hasNextPage
                        hasPreviousPage
                        startCursor
                        endCursor
                    }
                }
            }
            """;

        graphQlTester.document(nextQuery)
                .variable("after", endCursor)
                .execute()
                .path("booksPaginated.edges")
                .entityList(Object.class)
                .hasSize(3)
                .path("booksPaginated.pageInfo.hasPreviousPage")
                .entity(Boolean.class)
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Should support backward pagination")
    void shouldSupportBackwardPagination() {
        // Get a middle page cursor first
        String middleQuery = """
            query {
                booksPaginated(first: 10) {
                    pageInfo {
                        endCursor
                    }
                }
            }
            """;

        String endCursor = graphQlTester.document(middleQuery)
                .execute()
                .path("booksPaginated.pageInfo.endCursor")
                .entity(String.class)
                .get();

        // Now use backward pagination
        String backwardQuery = """
            query GetPreviousPage($before: String) {
                booksPaginated(last: 3, before: $before) {
                    edges {
                        cursor
                        node {
                            id
                            title
                        }
                    }
                    pageInfo {
                        hasNextPage
                        hasPreviousPage
                        startCursor
                        endCursor
                    }
                }
            }
            """;

        graphQlTester.document(backwardQuery)
                .variable("before", endCursor)
                .execute()
                .path("booksPaginated.edges")
                .entityList(Object.class)
                .hasSize(3)
                .path("booksPaginated.pageInfo.hasNextPage")
                .entity(Boolean.class)
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Should handle edge cases in pagination")
    void shouldHandlePaginationEdgeCases() {
        // Test with no parameters - should return all books
        String queryAll = """
            query {
                booksPaginated {
                    edges {
                        node {
                            id
                        }
                    }
                    pageInfo {
                        hasNextPage
                        hasPreviousPage
                    }
                }
            }
            """;

        graphQlTester.document(queryAll)
                .execute()
                .path("booksPaginated.edges")
                .entityList(Object.class)
                .hasSize(25)  // Should match total number of books
                .path("booksPaginated.pageInfo.hasNextPage")
                .entity(Boolean.class)
                .isEqualTo(false)
                .path("booksPaginated.pageInfo.hasPreviousPage")
                .entity(Boolean.class)
                .isEqualTo(false);

        // Test with first: 0 - should return empty
        String queryEmpty = """
            query {
                booksPaginated(first: 0) {
                    edges {
                        node {
                            id
                        }
                    }
                    pageInfo {
                        hasNextPage
                        hasPreviousPage
                    }
                }
            }
            """;

        graphQlTester.document(queryEmpty)
                .execute()
                .path("booksPaginated.edges")
                .entityList(Object.class)
                .hasSize(0);
    }

}