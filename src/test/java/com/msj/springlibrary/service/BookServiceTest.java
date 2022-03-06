package com.msj.springlibrary.service;

import com.msj.springlibrary.exception.BusinessException;
import com.msj.springlibrary.model.Book;
import com.msj.springlibrary.repository.BookRepository;
import com.msj.springlibrary.service.Impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService bookService;

    @MockBean
    BookRepository bookRepository;

    //Com esta anotação este metodo é executado antes de cada teste
    @BeforeEach
    public void setUp() {
        this.bookService = new BookServiceImpl(bookRepository);
    }

    @Test
    @DisplayName("Deve criar um livro")
    public void saveBookTest() {

        //cenario
        Book book = createValidBook();
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Mockito.when(bookRepository.save(book))
                .thenReturn(Book.builder()
                        .id(1l)
                        .author("Mateus")
                        .title("Teste JUnit")
                        .isbn("JSA-1")
                        .build());

        //execucao
        Book savedBook = bookService.save(book);

        //verificação
        Assertions.assertThat(savedBook.getId()).isNotNull();
        Assertions.assertThat(savedBook.getAuthor()).isEqualTo("Mateus");
        Assertions.assertThat(savedBook.getTitle()).isEqualTo("Teste JUnit");
        Assertions.assertThat(savedBook.getIsbn()).isEqualTo("JSA-1");
    }

    private Book createValidBook() {
        return Book.builder().isbn("JSA-1").author("Mateus").title("Teste JUnit").build();
    }


    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro já cadastrado")
    public void shouldNotSaveABookWuthDuplicateISBN() {

        //cenário
        Book book = createValidBook();
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //execução
        Throwable exception = Assertions.catchThrowable(() -> bookService.save(book));

        //verificações
        Assertions.assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado");

        Mockito.verify(bookRepository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve obter um livro por id")
    public void getByIdTest() {

        //cenario
        Long id = 1l;
        Book book = createValidBook();
        book.setId(id);
        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        //execução
        Optional<Book> foundBook = bookService.getById(id);

        //verificações
        Assertions.assertThat(foundBook.isPresent()).isTrue();
        Assertions.assertThat(foundBook.get().getId()).isEqualTo(id);
        Assertions.assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        Assertions.assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());

    }


    @Test
    @DisplayName("Deve retornar vazio quandop não encontar o id do livro")
    public void bookNotFoundByIdTest() {

        //cenario
        Long id = 1l;

        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.empty());

        //execução
        Optional<Book> book = bookService.getById(id);

        //verificações
        Assertions.assertThat(book.isPresent()).isFalse();

    }


 }
