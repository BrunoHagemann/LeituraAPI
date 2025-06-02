package br.com.ProgetosComplementares.LeituraAPI.service;

import br.com.ProgetosComplementares.LeituraAPI.model.Book;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class GutendexService {
    private static final String GUTENDEX_API_URL = "https://gutendex.com/books";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GutendexService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<Book> searchBooks(String query) {
        try {
            String url = GUTENDEX_API_URL + "/?search=" + query.replace(" ", "+");
            String jsonResponse = makeHttpRequest(url);
            return parseBooks(jsonResponse);
        } catch (Exception e) {
            System.out.println("Erro na busca: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Book> getPopularBooks(int minDownloads) {
        try {
            String url = GUTENDEX_API_URL + "/?sort=popular";
            String jsonResponse = makeHttpRequest(url);
            List<Book> books = parseBooks(jsonResponse);
            books.removeIf(book -> book.getDownloadCount() == null || book.getDownloadCount() < minDownloads);
            return books;
        } catch (Exception e) {
            System.out.println("Erro nos livros populares: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private String makeHttpRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    private List<Book> parseBooks(String json) throws IOException {
        class BookResponse {
            public List<Book> results;
        }

        BookResponse response = objectMapper.readValue(json, BookResponse.class);
        return response.results != null ? response.results : new ArrayList<>();
    }
}
