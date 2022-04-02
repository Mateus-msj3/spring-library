package com.msj.springlibrary.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msj.springlibrary.api.dto.BookDTO;
import com.msj.springlibrary.exception.BusinessException;
import com.msj.springlibrary.model.Book;
import com.msj.springlibrary.service.BookService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = {BookController.class})
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    private MockMvc mvc;

    @MockBean
    BookService bookService;

    public BookDTO createNewBook() {
        return BookDTO.builder().author("Mateus").title("Teste em JUnit").isbn("JU5").build();
    }

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    public void createBook() throws Exception{

        //Representa o Json que é passado na requisição post
        BookDTO dto = createNewBook();

        //Simula o retorno do book salvo
        Book book = Book.builder().id(1l).author("Mateus").title("Teste em JUnit").isbn("JU5").build();

        // Save na entidade book e retorna o valor que foi salvo com sucesso.
        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(book);

        String json = new ObjectMapper().writeValueAsString(dto);

        // Cria a requisição post, setando um json no conteudo da request

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // Assertivas dos testes, passando o que espero receber como resposta

        mvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated()) //status da requisição: 201 Created
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(1l)) // Espero um id não vazio
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(dto.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(dto.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(dto.getIsbn()));

    }

    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficientes para criação do livro")
    public void createInvalidBook() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        // Cria a requisição post, setando um json no conteudo da request

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já cadastrado.")
    public void createBookWithDuplicatedIsbn() throws Exception{

        BookDTO dto = createNewBook();

        String json = new ObjectMapper().writeValueAsString(dto);

        String messageError = "Isbn já cadastrado.";

        BDDMockito.given(bookService.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException(messageError));

        // Cria a requisição post, setando um json no conteudo da request

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value(messageError));
    }

    @Test
    @DisplayName("Deve obter informações de um livro")
    public void getBookDetailsTest() throws Exception {

        //cenario
        Long id = 1l;

        Book book = Book.builder()
                .id(id)
                .title(createNewBook().getTitle())
                .isbn(createNewBook().getIsbn())
                .author(createNewBook().getAuthor())
                .build();

        BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(book));

        //execução
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        //Verificação
        mvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(createNewBook().getIsbn()));

    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro procurado não existir")
    public void bookNotFoundTest() throws Exception {

        //cenário
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        //execução
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        //Verificação
        mvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest() throws Exception {

        //cenário
        Long id = 1l;
        String json = new ObjectMapper().writeValueAsString(createNewBook());

        Book updatingBook = Book.builder().id(id)
                .title("some title")
                .author("some author")
                .isbn("JU5")
                .build();

        BDDMockito.given(bookService.getById(id))
                .willReturn(Optional.of(updatingBook));

        Book updatedBook = Book.builder().id(id).author("Mateus").title("Teste em JUnit").isbn("JU5").build();
        BDDMockito.given(bookService.update(updatingBook)).willReturn(updatedBook);
        //execução
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        //Verificação
        mvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value("JU5"));

    }

    @Test
    @DisplayName("Deve retornar resource not found quando não encontrar o livro para atualizar")
    public void updateInexistentBookTest() throws Exception {

        ///cenário
        String json = new ObjectMapper().writeValueAsString(createNewBook());

        BDDMockito.given(bookService.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        //execução
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        //Verificação
        mvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() throws Exception {

        //cenário

        //Verifica se ja existe um livro mocado no servidor
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.of(Book.builder().id(1l).build()));

        //execução
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));

        //Verificação
        mvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNoContent());

    }

    @Test
    @DisplayName("Deve retornar resource not found quando não encontrar o livro para deletar")
    public void deleteInexistentBookTest() throws Exception {

        //cenário

        //Verifica se ja existe um livro mocado no servidor
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        //execução
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));

        //Verificação
        mvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    @DisplayName("Deve filtar livros por parametros")
    public void findBookTest() throws Exception {

        Long id = 1l;

        Book book = Book.builder()
                    .id(id)
                    .title(createNewBook().getTitle())
                    .author(createNewBook().getAuthor())
                    .isbn(createNewBook().getIsbn())
                    .build();

        BDDMockito.given(bookService.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1));

        String queryString = String.format("?title=%s&author=%s&page=0&size=100",
                book.getTitle(), book.getAuthor());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageSize").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0));



    }

}
