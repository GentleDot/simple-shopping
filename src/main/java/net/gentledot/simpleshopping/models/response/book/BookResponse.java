package net.gentledot.simpleshopping.models.response.book;

import net.gentledot.simpleshopping.models.book.Book;
import net.gentledot.simpleshopping.models.book.BookCategory;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.util.Optional;

public class BookResponse {
    @Enumerated(EnumType.STRING)
    private BookCategory category;
    private String name;
    private String description;
    private LocalDate publishDate;

    protected BookResponse(){
    }

    public BookResponse(BookCategory category, String name, String description, LocalDate publishDate) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.publishDate = publishDate;
    }

    public BookCategory getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("category", category)
                .append("name", name)
                .append("description", description)
                .append("publishDate", publishDate)
                .toString();
    }

    public static final class Builder {
        private BookCategory category;
        private String name;
        private String description;
        private LocalDate publishDate;

        public Builder() {
        }

        public Builder(Book book) {
            this.category = BookCategory.getCategory(book.getCategoryCode());
            this.name = book.getName();
            this.description = book.getDescription().orElse(null);
            this.publishDate = book.getPublishDate();
        }

        public Builder category(BookCategory category) {
            this.category = category;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder publishDate(LocalDate publishDate) {
            this.publishDate = publishDate;
            return this;
        }

        public BookResponse build() {
            return new BookResponse(category, name, description, publishDate);
        }
    }
}
