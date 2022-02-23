package com.msj.springlibrary.api.resource;

import com.msj.springlibrary.api.dto.BookDTO;
import com.msj.springlibrary.model.Book;
import com.msj.springlibrary.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO createBook(@RequestBody BookDTO bookDTO) {

        Book bookEntity = Book.builder()
                .author(bookDTO.getAuthor())
                .title(bookDTO.getTitle())
                .isbn(bookDTO.getIsbn())
                .build();

        bookEntity = bookService.save(bookEntity);

        return BookDTO.builder()
                .id(bookEntity.getId())
                .author(bookEntity.getAuthor())
                .title(bookEntity.getTitle())
                .isbn(bookEntity.getIsbn())
                .build();
    }
}
