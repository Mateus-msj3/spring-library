package com.msj.springlibrary.service;

import com.msj.springlibrary.model.Book;
import org.springframework.stereotype.Service;

@Service
public interface BookService {
    Book save(Book any);
}
