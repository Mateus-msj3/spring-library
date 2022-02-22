package com.msj.springlibrary.api.resource;

import com.msj.springlibrary.api.dto.BookDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO createBook() {

        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(1l);
        bookDTO.setAuthor("author");
        bookDTO.setTitle("My Book");
        bookDTO.setIsbn("CCO-885");

        return bookDTO;
    }
}
