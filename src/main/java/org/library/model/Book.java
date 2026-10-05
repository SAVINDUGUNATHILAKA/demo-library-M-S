package org.library.model;

import java.util.Objects;

/**
 * Model representing a Book in the Library.
 */
public class Book {
    private String id;
    private String title;
    private String author;
    private String category;
    private int publishedYear;
    private int quantity;
    private int availableQuantity;

    public Book() {
    }

    public Book(String id, String title, String author, String category, int publishedYear, int quantity) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.publishedYear = publishedYear;
        this.quantity = quantity;
        this.availableQuantity = quantity;
    }

    public Book(String id, String title, String author, String category, int publishedYear, int quantity, int availableQuantity) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.publishedYear = publishedYear;
        this.quantity = quantity;
        this.availableQuantity = availableQuantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(int publishedYear) {
        this.publishedYear = publishedYear;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%d avail)", id, title, availableQuantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
