/*
 * Copyright (c)  2024 kamillion labs contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.kamillionlabs.hateoflux.dummy;

import de.kamillionlabs.hateoflux.dummy.model.Author;
import de.kamillionlabs.hateoflux.dummy.model.Book;
import de.kamillionlabs.hateoflux.utility.pair.MultiRightPair;
import de.kamillionlabs.hateoflux.utility.pair.MultiRightPairList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Younes El Ouarti
 */
public class TestDataGenerator {

    public enum AuthorName {
        JOSHUA_BLOCH("Joshua Bloch"), //1
        ROBERT_MARTIN("Robert C. Martin"), //1
        BRIAN_GOETZ("Brian Goetz"), //2
        ERICH_GAMMA("Erich Gamma"), //3
        MARTIN_FOWLER("Martin Fowler"), //1
        MICHAEL_FEATHERS("Michael Feathers"), //1
        ANDREW_HUNT("Andrew Hunt"); //1

        private final String name;

        AuthorName(String name) {
            this.name = name;
        }
    }


    public enum BookTitle {
        EFFECTIVE_JAVA("Effective Java"),
        CLEAN_CODE("Clean Code"),
        JAVA_CONCURRENCY_IN_PRACTICE("Java Concurrency in Practice"),
        JAVA_PUZZLERS("Java Puzzlers"),
        DESIGN_PATTERNS("Design Patterns: Elements of Reusable Object-Oriented Software"),
        HEAD_FIRST_DESIGN_PATTERNS("Head First Design Patterns"),
        PATTERN_ORIENTED_SOFTWARE_ARCHITECTURE("Pattern-Oriented Software Architecture Volume 1"),
        REFACTORING("Refactoring: Improving the Design of Existing Code"),
        THE_PRAGMATIC_PROGRAMMER("The Pragmatic Programmer"),
        WORKING_EFFECTIVELY_WITH_LEGACY_CODE("Working Effectively with Legacy Code");

        private final String title;

        BookTitle(String title) {
            this.title = title;
        }
    }

    private final MultiRightPairList<Author, Book> database;


    public Book getBookByTitle(BookTitle bookTitle) {
        return getBookByTitle(bookTitle.title);
    }

    public Book getBookByTitle(String bookTitle) {
        return database.stream()
                .filter(pair -> pair.rights().stream().anyMatch(book -> Objects.equals(book.getTitle(), bookTitle)))
                .findFirst()
                .map(multiPair -> multiPair.rights().get(0))
                .orElseThrow(() -> new RuntimeException("Book title " + bookTitle + " not found"));
    }


    public List<Book> getAllBooksByAuthorName(AuthorName authorName) {
        return getAllBooksByAuthorName(authorName.name);
    }

    public List<Book> getAllBooksByAuthorName(String authorName) {
        return database.stream()
                .filter(pair -> pair.left().getName().strip().equalsIgnoreCase(authorName))
                .map(MultiRightPair::rights)
                .findFirst()
                .orElse(new ArrayList<>());
    }


    public Author getAuthorByName(AuthorName authorName) {
        return getAuthorByName(authorName.name);
    }

    public Author getAuthorByName(String authorName) {
        return database.stream()
                .filter(pair -> pair.left().getName().strip().equalsIgnoreCase(authorName))
                .findFirst()
                .map(MultiRightPair::left)
                .orElseThrow(() -> new RuntimeException("Author " + authorName + " not found"));
    }

    public TestDataGenerator() {
        database = new MultiRightPairList<>();

        Book book1 = Book.builder()
                .title("Effective Java")
                .author("Joshua Bloch")
                .isbn("978-0134685991")
                .publishedDate("2018-01-06")
                .build();

        Author author1 = Author.builder()
                .name("Joshua Bloch")
                .birthDate("1961-08-28")
                .mainGenre("Programming")
                .build();

        database.add(author1, List.of(book1));

        Book book2 = Book.builder()
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("978-0132350884")
                .publishedDate("2008-08-01")
                .build();

        Author author2 = Author.builder()
                .name("Robert C. Martin")
                .birthDate("1952-12-05")
                .mainGenre("Software Engineering")
                .build();

        database.add(author2, List.of(book2));

        Book book3 = Book.builder()
                .title("Java Concurrency in Practice")
                .author("Brian Goetz")
                .isbn("978-0321349606")
                .publishedDate("2006-05-19")
                .build();

        Book book4 = Book.builder()
                .title("Java Puzzlers")
                .author("Brian Goetz")
                .isbn("978-0321336781")
                .publishedDate("2005-07-24")
                .build();

        Author author3 = Author.builder()
                .name("Brian Goetz")
                .birthDate("1969-05-22")
                .mainGenre("Programming Languages")
                .build();

        database.add(author3, List.of(book3, book4));

        Book book5 = Book.builder()
                .title("Design Patterns: Elements of Reusable Object-Oriented Software")
                .author("Erich Gamma")
                .isbn("978-0201633610")
                .publishedDate("1994-10-31")
                .build();

        Book book6 = Book.builder()
                .title("Head First Design Patterns")
                .author("Erich Gamma")
                .isbn("978-0596007126")
                .publishedDate("2004-10-25")
                .build();

        Book book7 = Book.builder()
                .title("Pattern-Oriented Software Architecture Volume 1")
                .author("Erich Gamma")
                .isbn("978-0471958697")
                .publishedDate("1995-10-25")
                .build();

        Author author4 = Author.builder()
                .name("Erich Gamma")
                .birthDate("1961-03-13")
                .mainGenre("Software Architecture")
                .build();

        database.add(author4, List.of(book5, book6, book7));

        Book book8 = Book.builder()
                .title("Refactoring: Improving the Design of Existing Code")
                .author("Martin Fowler")
                .isbn("978-0201485677")
                .publishedDate("1999-07-08")
                .build();

        Author author5 = Author.builder()
                .name("Martin Fowler")
                .birthDate("1963-12-18")
                .mainGenre("Software Engineering")
                .build();

        database.add(author5, List.of(book8));

        Book book9 = Book.builder()
                .title("The Pragmatic Programmer")
                .author("Andrew Hunt")
                .isbn("978-0201616224")
                .publishedDate("1999-10-30")
                .build();

        Author author6 = Author.builder()
                .name("Andrew Hunt")
                .birthDate("1964-06-15")
                .mainGenre("Software Development")
                .build();

        database.add(author6, List.of(book9));


        Book book10 = Book.builder()
                .title("Working Effectively with Legacy Code")
                .author("Michael Feathers")
                .isbn("978-0131177055")
                .publishedDate("2004-09-22")
                .build();

        Author author7 = Author.builder()
                .name("Michael Feathers")
                .birthDate("1966-01-27")
                .mainGenre("Software Maintenance")
                .build();

        database.add(author7, List.of(book10));
    }

}
