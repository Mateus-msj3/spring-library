package com.msj.springlibrary.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    public void createBook() throws Exception{

        String json = new ObjectMapper().writeValueAsString(null);

        // Cria a requisição post, setando um json no conteudo da request

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // Assertivas dos testes, passando o que espero receber como resposta

        mvc
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated()) //status da requisição: 201 Created
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty()) // Espero um id não vazio
                .andExpect(MockMvcResultMatchers.jsonPath("title").value("My Book"))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value("author"))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value("CCO-885"));

    }

    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficientes para criação do livro")
    public void createInvalidBook() {

    }
}
