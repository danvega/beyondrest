package books;

import books.book.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.restclient.autoconfigure.RestClientAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.client.HttpSyncGraphQlClient;
import org.springframework.web.client.RestClient;

@Import(RestClientAutoConfiguration.class)
public class ClientApp implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ClientApp.class);
    private final HttpSyncGraphQlClient client;

    public ClientApp(RestClient.Builder builder) {
        RestClient restClient = builder
                .baseUrl("http://localhost:8080/graphql")
                .build();
        this.client = HttpSyncGraphQlClient.builder(restClient).build();
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(ClientApp.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Client App: Retrieving Book synchronously");
        var document = """
                query findBookById($id: ID!) {
                    book(id: $id) {
                        id
                        title
                        author {
                            id
                            name
                        }
                        publishedYear
                    }
                }
                """;
        var book = client.document(document)
                .variable("id", 1L)
                .retrieveSync("book") // could use .retrieve() here for sync calls only catch is webflux needs to be on the classpath
                .toEntity(Book.class);

        assert book != null;
        log.info("Book Details: {}", book);
    }
}
