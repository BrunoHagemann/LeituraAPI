package br.com.ProgetosComplementares.LeituraAPI.service;

import br.com.ProgetosComplementares.LeituraAPI.model.Book;
import br.com.ProgetosComplementares.LeituraAPI.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    // Método para salvar um livro
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    // Busca personalizada usando JPQL
    public List<Book> searchBooks(String query) {
        return bookRepository.findByTitleOrAuthor(query);
    }

    // Busca todos os livros
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
}
