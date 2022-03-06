package com.msj.springlibrary.service.Impl;

import com.msj.springlibrary.exception.BusinessException;
import com.msj.springlibrary.model.Book;
import com.msj.springlibrary.repository.BookRepository;
import com.msj.springlibrary.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book book) {

        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("Isbn já cadastrado");
        }

        return bookRepository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return this.bookRepository.findById(id);
    }

    @Override
    public void delete(Book book) {

        if (book == null || book.getId() == null) {
            throw new IllegalArgumentException("Book id cant be null");
        }

        this.bookRepository.delete(book);
    }

    @Override
    public Book update(Book book) {

        if (book == null || book.getId() == null) {
            throw new IllegalArgumentException("Book id cant be null");
        }

        return this.bookRepository.save(book);
    }

}
