package com.msj.springlibrary.service;

import com.msj.springlibrary.model.Book;

import java.util.Optional;


public interface BookService {

    Book save(Book any);

    Optional<Book> getById(Long id);

    void delete(Book book);

    void update(Book book);
}
