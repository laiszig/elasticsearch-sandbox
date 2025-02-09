package com.laiszig.simpleendpoints;

import java.util.LinkedHashMap;

public class Demo {

}

/*
import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;



class BookManager {

    private final List<Info> infos = new ArrayList<>();
    public record Info(Integer id, String author, String title, Float price, Date releasedOn){}
    private final int CACHE_LIMIT = 5;

    // LRU - LinkedHashMap<> cache implementation - good for small caches
    private final Map<Integer, Info> cache = new LinkedHashMap<>(CACHE_LIMIT, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, Info> eldest) {
            return size() > CACHE_LIMIT;
        }
    };

    public void updateBookInfo(Info bookInfo) {
        for (int i = 0; i < infos.size(); i++) {
            if(infos.get(i).id() == bookInfo.id()) {
                infos.set(i, bookInfo);
                cache.put(bookInfo.id(), bookInfo);
                return;
            }
        }
        infos.add(bookInfo);
        cache.put(bookInfo.id(), bookInfo);
    }

    public Info getBookInfo(int id) {
        if (cache.containsKey(id)) {
                return cache.get(id);
            }

            Info bookInfo = fetchFromDatabase(id);
            cache.put(id, bookInfo);
            return bookInfo;
    }

    private Info fetchFromDatabase(int id) {
        return infos.stream()
                .filter(book -> book.id() == id)
                .findFirst()
                .orElse(null);
    }
}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        BookManager.Info[] infos = {
            new BookManager.Info(1, "João Silva", "A Vida no Campo", 19.99f, new Date(120, 0, 15)),
            new BookManager.Info(2, "Maria Oliveira", "Culinária Caipira", 29.99f, new Date(121, 5, 30)),
            new BookManager.Info(6, "Carla Nunes", "Guia do Pescador", 22.50f, new Date(118, 3, 14)),
            new BookManager.Info(18, "Veronica Almeida", "Economia Doméstica", 21.10f, new Date(121, 11, 30)),
            new BookManager.Info(19, "Marcelo Teixeira", "Saúde e Bem-Estar Rural", 26.75f, new Date(119, 7, 15)),
            new BookManager.Info(20, "Patrícia Fernandes", "História e Cultura Caipira", 32.50f, new Date(122, 10, 8))
        };

        BookManager bookManager = new BookManager();

        for (BookManager.Info bookInfo : infos) {
            bookManager.updateBookInfo(bookInfo);
        }

        var lastBook = bookManager.getBookInfo(20);

        if(lastBook != null && lastBook.id() == 20 && lastBook.price() == 32.50f) {
            bookManager.updateBookInfo(new BookManager.Info(20, "Patrícia Fernandes", "História e Cultura Caipira do Brasil", 12.50f, new Date(122, 10, 8)));

            var result = bookManager.getBookInfo(20);

            bufferedWriter.write(infoToString(result));
            bufferedWriter.newLine();
        }

        bufferedWriter.close();
    }

    static String infoToString(BookManager.Info bookInfo) {
        return bookInfo.id() + " " + bookInfo.author() + " " + bookInfo.title() + " R$" + bookInfo.price();
    }
}
 */
