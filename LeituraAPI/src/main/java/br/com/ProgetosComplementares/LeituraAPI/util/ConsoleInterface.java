package br.com.ProgetosComplementares.LeituraAPI.util;

import br.com.ProgetosComplementares.LeituraAPI.model.Book;
import br.com.ProgetosComplementares.LeituraAPI.service.BookService;
import br.com.ProgetosComplementares.LeituraAPI.service.GutendexService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class ConsoleInterface {
    @Autowired
    private GutendexService gutendexService;

    @Autowired
    private BookService bookService;

    @PostConstruct
    public void init() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== CATÁLOGO DE LIVROS ===");
            System.out.println("1. Buscar livros (título ou autor)");
            System.out.println("2. Listar todos os livros");
            System.out.println("3. Buscar livros populares na API");
            System.out.println("4. Sair");
            System.out.print("Escolha uma opção: ");

            int option = scanner.nextInt();
            scanner.nextLine(); // Consome newline

            switch (option) {
                case 1:
                    searchBooks(scanner);
                    break;
                case 2:
                    listAllBooks();
                    break;
                case 3:
                    searchPopularBooks(scanner);
                    break;
                case 4:
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private void searchBooks(Scanner scanner) {
        System.out.print("Digite o termo de busca (título ou autor): ");
        String query = scanner.nextLine();

        List<Book> localResults = bookService.searchBooks(query);
        List<Book> apiResults = gutendexService.searchBooks(query);

        List<Book> allResults = apiResults.stream()
                .filter(apiBook -> !localResults.stream()
                        .anyMatch(localBook -> localBook.getId().equals(apiBook.getId())))
                .collect(Collectors.toList());

        allResults.addAll(localResults);

        displayBooks(allResults);
    }

    private void listAllBooks() {
        List<Book> books = bookService.getAllBooks();
        displayBooks(books);
    }

    private void searchPopularBooks(Scanner scanner) {
        System.out.print("Digite o mínimo de downloads: ");
        int minDownloads = scanner.nextInt();
        scanner.nextLine();

        List<Book> popularBooks = gutendexService.getPopularBooks(minDownloads);
        displayBooks(popularBooks);
    }

    private void displayBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("Nenhum livro encontrado.");
            return;
        }

        System.out.println("\nResultados (" + books.size() + " livros):");
        System.out.println("----------------------------------------");

        for (Book book : books) {
            System.out.println("ID: " + book.getId());
            System.out.println("Título: " + book.getTitle());
            System.out.println("Autores: " +
                    book.getAuthors().stream()
                            .map(author -> author.getName())
                            .collect(Collectors.joining(", ")));
            System.out.println("Downloads: " +
                    (book.getDownloadCount() != null ? book.getDownloadCount() : "N/A"));
            System.out.println("----------------------------------------");
        }
    }
}
