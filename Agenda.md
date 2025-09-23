# Presentation Agenda

- Create the Application
  - start.spring.io
    - web,graphql,devtools
  - Review the Application
    - Spring Boot 4 + Spring Framework 7
    - Review Dependencies
- BookRepository
  - Book Record
  - Author Record
- BookRestController
  - No more over-fetching
  - Multiple Request for multiple resources
  - Avoid REST API Explosion of endpoints
  - Avoid API Versioning
    - API Versioning in Spring Framework 7+ 
    - `WebConfig implements WebMvcConfigurer` or through properties
  - Strongly-typed Schema
  - Self Documenting
  - Developer Tooling
- Schema First Design
  - create `schema.graphqls`
  - Add Book & Author Object Types
  - Add Query Mappings (Operation Types)
  - Run Application → Inspect Schema Mapping Inspection Report
- GraphiQL
  - `application.properties`
  - `spring.graphql.graphiql.enabled=true` We don't actually need this because of devtools
  - Explore GraphiQL
- Mutation Mapping
  - Create Book
  - Delete book
- Data Integration
  - Query Filtering
  - [Query by Example](https://docs.spring.io/spring-graphql/reference/data.html#data.querybyexample)
  - [@GraphQLRepository](https://docs.spring.io/spring-graphql/reference/data.html#data.querybyexample.registration)
- Performance Improvements
  - Virt[BookRestControllerTest.java](src/test/java/dev/danvega/sb4books/BookRestControllerTest.java)ual Threads
  - Batch Mapping
    - n+1 Problem & Solution
- Union Type
  - Query `search(text:String) : [SearchItem]!`
  - Union
  - SearchController
- Pagination
  - `booksPaginated(first:Int, after:String, last:Int, before:String): BookConnection`
  - BookController / BookRepository
- Client
- Tests
  - @WebMvcTest (`BookRestControllerTest.java`)
  - RestTestClient (`BookControllerTest.java`)
  - GraphQlTester (`BooksGraphQLTest.java`)



## Resources 

GraphQL Books
https://github.com/danvega/graphql-books 

SpringOne Books
https://github.com/danvega/spring-one-books 

RestTestClient
https://github.com/danvega/sb4/blob/master/src/test/java/dev/danvega/sb4/http_interface_clients/TodoControllerTest.java 


Query By Example Blog Post
https://www.danvega.dev/blog/spring-boot-graphql-query-by-example


## Notes

We will need this at some point 

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-restclient</artifactId>
</dependency>
```

## Live Templates

- gql-pagination-book-connection (Book Connection Record)
- gqlb-api-versioning
- gqlb-author (Author Record)
- gqlb-book (Book Record)
- gqlb-book-filter (BookFilter Record)
- gqlb-book-rest-controller (Book Rest Controller API Versioning)
- gqlb-books-with-filter (Books Controller - Find All Books with Filtering)
- gqlb-client-app (Client App Example)
- gqlb-mutation-create-book (BookController::createBook)
- gqlb-mutation-delete-book (BookController::deleteBook)
- gqlb-n1-solution (Books by Author n+1 Solution)
- gqlb-pagination-book-edge (Book Edge Record)
- gqlb-pagination-books-paginated (Books Paginated Schema Query)
- gqlb-pagination-controller (Book Controller Pagination Query)
- gqlb-pagination-page-info (Page Info Record)
- gqlb-pagination-repository (Book Repository Pagination Methods)
- gqlb-repository (Book Repository)
- gqlb-search-controller (Search Controller)
- gqlb-test-book-controller (Book Controller Test)
- gqlb-test-book-rest-controller (Book Rest Controller Test)
- gqlb-test-books-graphql-test (Books GraphQL Test)
- gqlb-threads (Find Books by Author - Threads Demo)