package com.msj.springlibrary.api.resource;

import com.msj.springlibrary.api.dto.BookDTO;
import com.msj.springlibrary.model.Book;
import com.msj.springlibrary.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private ModelMapper modelMapper;

//    public BookController(BookService bookService, ModelMapper modelMapper) {
//        this.bookService = bookService;
//        this.modelMapper = modelMapper;
//    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO createBook(@RequestBody BookDTO bookDTO) {

        Book bookEntity = modelMapper.map(bookDTO, Book.class);

        bookEntity = bookService.save(bookEntity);

        return modelMapper.map(bookEntity, BookDTO.class);
    }
}
