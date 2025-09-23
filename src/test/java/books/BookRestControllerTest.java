package books;

import books.api.BookRestController;
import books.book.Author;
import books.book.Book;
import books.book.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookRestController.class)
class BookRestControllerTest {

    @MockitoBean
    private BookRepository bookRepository;

    @Autowired
    private MockMvc mockMvc;

    private final String API_VERSION_HEADER = "X-API-Version";

    @Test
    void shouldReturnBooksV1() throws Exception {
        // Given
        when(bookRepository.findAll()).thenReturn(List.of(getTestBook()));

        // When & Then
        mockMvc.perform(get("/api/books/")
                        .header(API_VERSION_HEADER, "1.0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[0].author.name").value("Test Author"))
                .andExpect(jsonPath("$[0].publishedYear").value(2023));
    }

    @Test
    void shouldReturnBooksV13() throws Exception {
        // Given
        when(bookRepository.findAll()).thenReturn(List.of(getTestBook()));

        // When & Then
        mockMvc.perform(get("/api/books/")
                        .header(API_VERSION_HEADER, "1.3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[0].author.name").value("Test Author"))
                .andExpect(jsonPath("$[0].publishedYear").value(2023));
    }

    @Test
    void shouldReturnBooksV2() throws Exception {
        // Given
        when(bookRepository.findAll()).thenReturn(List.of(getTestBook()));

        // When & Then
        mockMvc.perform(get("/api/books/")
                        .header(API_VERSION_HEADER, "2.0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[0].author.name").value("Test Author"))
                .andExpect(jsonPath("$[0].publishedYear").value(2023));
    }

    @Test
    @Description("When no version is set it should fall back to the default version (1.0)")
    void shouldReturnBooksWithoutVersion() throws Exception {
        when(bookRepository.findAll()).thenReturn(List.of(getTestBook()));

        // When & Then
        mockMvc.perform(get("/api/books/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[0].author.name").value("Test Author"))
                .andExpect(jsonPath("$[0].publishedYear").value(2023));
    }

    @Test
    void shouldReturnEmptyListWhenNoBooksExist() throws Exception {
        // Given
        when(bookRepository.findAll()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/books/")
                        .header(API_VERSION_HEADER, "1.0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    private Book getTestBook() {
        var author = new Author(1L, "Test Author");
        return new Book(1L, "Test Book", author, 2023);
    }

}