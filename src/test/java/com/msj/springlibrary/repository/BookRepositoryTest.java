package com.msj.springlibrary.repository;

import com.msj.springlibrary.model.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository bookRepository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado")
    public void returnTrueWhenIsbnExistis() {

        //cenario
        String isbn = "123";

        Book book = Book.builder().isbn(isbn).author("Mateus").title("Teste JUnit").build();

        entityManager.persist(book);

        //execução
        boolean existis = bookRepository.existsByIsbn(isbn);

        //verificação
        Assertions.assertThat(existis).isTrue();


    }

    @Test
    @DisplayName("Deve retornar falso quando não existir um livro na base com o isbn informado")
    public void returnFalseWhenIsbnDoesntExistis() {

        //cenario
        String isbn = "123";

        //execução
        boolean existis = bookRepository.existsByIsbn(isbn);

        //verificação
        Assertions.assertThat(existis).isFalse();


    }
}
