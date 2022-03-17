package com.msj.springlibrary.api.resource;

import com.msj.springlibrary.api.dto.BookDTO;
import com.msj.springlibrary.api.exception.ApiErrors;
import com.msj.springlibrary.exception.BusinessException;
import com.msj.springlibrary.model.Book;
import com.msj.springlibrary.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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
    public BookDTO createBook(@RequestBody @Valid BookDTO bookDTO) {

        Book bookEntity = modelMapper.map(bookDTO, Book.class);

        bookEntity = bookService.save(bookEntity);

        return modelMapper.map(bookEntity, BookDTO.class);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationExceptions(MethodArgumentNotValidException exception) {

        BindingResult bindingResult = exception.getBindingResult();

        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleBusinessException(BusinessException exception) {

        return new ApiErrors(exception);
    }

    @GetMapping("{id}")
    public BookDTO get(@PathVariable Long id) {

        return bookService.getById(id)
                .map( book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @GetMapping
    public Page<BookDTO> find(BookDTO bookDTO, Pageable pageRequest) {

        Book filter = modelMapper.map(bookDTO, Book.class);

        Page<Book> result = bookService.find(filter, pageRequest);

        List<BookDTO> list = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, BookDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<BookDTO>(list, pageRequest, result.getTotalElements());
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO update(@PathVariable Long id, BookDTO dto) {

        return bookService.getById(id).map(book -> {

            book.setAuthor(dto.getAuthor());
            book.setTitle(dto.getTitle());

            bookService.update(book);

            return modelMapper.map(book, BookDTO.class);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));


    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {

        Book book = bookService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        bookService.delete(book);

    }


}
