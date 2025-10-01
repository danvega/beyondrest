# Spring Boot 4 & GraphQL Books Demo

This repository demonstrates building a modern GraphQL API with Spring Boot 4, showcasing advanced features like performance optimization, pagination, and virtual thread support.

## Why GraphQL? 

- No more over-fetching
- Multiple Request for multiple resources
- Avoid REST API Explosion of endpoints
- Strongly-typed Schema
  - Self Documenting
  - Developer Tooling
- Avoids API Versioning

## Presentation Agenda

This demo walks through building a complete GraphQL books API, covering the following key areas:

1. **Getting Started** - Project setup with Spring Boot 4 and GraphQL dependencies
2. **Schema First Development** - Defining our GraphQL schema and understanding the contract
3. **Building the Domain Models** - Creating Book and Author entities
4. **Implementing the Repository Layer** - Data access patterns and in-memory storage
5. **Creating GraphQL Controllers** - Query and mutation resolvers with @SchemaMapping
6. **Advanced Querying** - Filters, search functionality, and unions
7. **Performance Optimization** - Solving the N+1 problem with @BatchMapping
8. **Virtual Thread Integration** - Leveraging Project Loom for scalability
9. **Implementing Pagination** - Cursor-based pagination following GraphQL best practices
10. **Testing Strategies** - GraphQL testing with Spring Boot

## Getting Started

### Dependencies

The project uses Spring Boot 4.0.0-M3 with the following key dependencies:
- **Spring Web** - REST API support and web infrastructure
- **Spring for GraphQL** - GraphQL integration with Spring ecosystem
- **Actuator** - Production-ready monitoring and management
- **DevTools** - Development-time features for faster iteration

[Project Generator Link](https://start.spring.io/#!type=maven-project&language=java&platformVersion=4.0.0-M3&packaging=jar&jvmVersion=25&groupId=dev.danvega&artifactId=sb4books&name=sb4books&description=Demo%20project%20for%20Spring%20Boot&packageName=dev.danvega.sb4books&dependencies=web,graphql,actuator,devtools)

## Schema First Development

We follow a schema-first approach, defining our GraphQL contract before implementation. This ensures a clear API contract and better collaboration between frontend and backend teams.

Our schema defines:
- **Object Types**: Book and Author with their relationships
- **Query Operations**: Find books, filter books, search across types
- **Mutation Operations**: Add and delete books
- **Input Types**: Filters and creation inputs
- **Union Types**: Search results across multiple types

## Building the Domain Models

We create simple record classes for our Book and Author entities, demonstrating modern Java features and clean domain modeling.

## Implementing the Repository Layer

The BookRepository provides in-memory data storage and demonstrates various data access patterns including filtering and relationship management.

## Creating GraphQL Controllers (Data Fetchers)

This section demonstrates how to implement GraphQL resolvers using Spring's @SchemaMapping annotation, showing both query and mutation operations.

### Basic Queries

Find all books with their authors:

```graphql
query {
  books {
    id
    title
    author {
      id
      name
    }
    publishedYear
  }
}
```

### Filtering with Input Types

Using a BookFilter as an input to find books by author name or published year:

```graphql
query {
  booksWithFilter(filter:  {
     authorName: "Dan Vega"
  }) {
    id
    title
  }
}
```

### Mutations

Create a new book:

```graphql
mutation {
  addBook(title: "Fundamentals of Software Engineering", authorName:  "Dan Vega", publishedYear: 2025) {
    id
    title
    author {
      id
      name
    }
    publishedYear
  }
}
```

Delete a book by its ID:

```graphql
mutation {
  deleteBook(id: 26)
}
```

## Advanced Querying - Union Types

Unions allow us to return different types from a single field. Our search functionality returns both Authors and Books:

```graphql
query {
  search(text: "Spring") {
    ... on Author {
      id
      name
      books {
        id
        publishedYear
        title
      }
    }
    ... on Book {
      id
      publishedYear
      title
      author {
        id
        name
      }
    }
  }
}
```

## Performance Optimization

### The N+1 Problem

When fetching authors and their books, we can easily fall into the N+1 query problem. This happens when we make one query to fetch all authors, then N additional queries to fetch books for each author.

#### Demonstrating the Problem

```java
@SchemaMapping
public List<Book> books(Author author) {
    log.info("Loading books for author: {}", author.name());
    return bookRepository.findAll().stream()
            .filter(book -> book.author().id().equals(author.id()))
            .collect(Collectors.toList());
}
```

Query that triggers N+1:
```graphql
{
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
```

This results in multiple individual calls:
```text
Loading books for author: Dan Vega
Loading books for author: Nate Schutta
Loading books for author: Joshua Bloch
Loading books for author: Herbert Schildt
Loading books for author: Raoul-Gabriel Urma
Loading books for author: Brian Goetz
Loading books for author: Craig Walls
Loading books for author: Greg Turnquist
Loading books for author: Mark Heckler
Loading books for author: Thomas Vitale
Loading books for author: Josh Long
Loading books for author: Dinesh Rajput
Loading books for author: John Carnell
Loading books for author: Laurentiu Spilca
Loading books for author: Petri Kainulainen
Loading books for author: Rod Johnson
Loading books for author: Martin Fowler
Loading books for author: Neal Ford
Loading books for author: Ken Kousen
Loading books for author: Dmitry Jemerov
Loading books for author: Venkat Subramaniam
Loading books for author: Petar Tahchiev
Loading books for author: Robert C. Martin
Loading books for author: Andrew Hunt
```

#### Solution: @BatchMapping

Using @BatchMapping, we can batch load data for multiple entities in a single operation:

```java
@BatchMapping
public Map<Author, List<Book>> books(List<Author> authors) {
    System.out.println("🚀 BATCH LOADING books for " + authors.size() + " authors in ONE call!");

    List<Long> authorIds = authors.stream()
            .map(Author::id)
            .toList();

    List<Book> allBooks = bookRepository.findBooksByAuthorIds(authorIds);
    System.out.println("✅ Loaded " + allBooks.size() + " books total");
    Map<Author, List<Book>> booksByAuthor = allBooks.stream()
            .collect(Collectors.groupingBy(Book::author));

    // Ensure every author has an entry, even if empty
    for (Author author : authors) {
        booksByAuthor.putIfAbsent(author, Collections.emptyList());
    }

    return booksByAuthor;
}
```

Result with batch loading:
```text
🚀 BATCH LOADING books for 24 authors in ONE call!
✅ Loaded 25 books total
```

## Virtual Thread Integration (Project Loom)

Spring Boot 4 includes excellent support for virtual threads. We demonstrate how GraphQL operations can benefit from this lightweight concurrency model:

```java
@SchemaMapping
public List<Book> books(Author author) throws InterruptedException {

    log.info("Thread: {}",Thread.currentThread());

    // what if this call was to another service?
    // sleep for 1 second to demonstrate delay
    TimeUnit.SECONDS.sleep(1);

    log.info("Loading books for author: {}", author.name());
    return bookRepository.findAll().stream()
            .filter(book -> book.author().id().equals(author.id()))
            .collect(Collectors.toList());
}
```

### Key Benefits:
* **Performance considerations** - Better resource utilization
* **Solving blocking operations** - No more thread pool exhaustion
* **Database optimization** - Handle more concurrent connections
* **Scalability** - Process thousands of concurrent requests
* **Virtual thread execution** - All operations run on lightweight virtual threads

## Implementing Pagination

We implement cursor-based pagination following GraphQL best practices with the Connection pattern:

### Schema Definition

```graphql
Query {
	books(first:Int, after:String, last:Int, before:String): BookConnection
}

type Book {
	id: ID!
	title: String!
}
```

### Usage Example

```graphql
{
  booksPaginated(first: 3) {
    edges {
      cursor
      node {
        id
        publishedYear
        title
      }
    }
    pageInfo {
      endCursor
      hasNextPage
      hasPreviousPage
      startCursor
    }
  }
}
```

[Pagination Documentation](https://docs.spring.io/spring-graphql/reference/request-execution.html#execution.pagination.types)

## Testing Strategies

The project includes comprehensive testing examples showing how to test GraphQL APIs with Spring Boot, including integration tests and unit tests for resolvers.