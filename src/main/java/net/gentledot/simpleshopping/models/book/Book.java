package net.gentledot.simpleshopping.models.book;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static net.gentledot.simpleshopping.common.util.argumentHandleUtil.getBookIdFromArguments;
import static net.gentledot.simpleshopping.common.util.argumentHandleUtil.checkExpression;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Entity(name = "book")
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(name = "id", unique = true, length = 220, nullable = false)
    private String id;

    @Column(name = "category", length = 10, nullable = false)
    private String categoryCode;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "description", length = 360)
    private String description;

    @Column(name = "publish_date")
    private LocalDate publishDate;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    protected Book() {
    }

    public Book(String categoryCode, String name, LocalDate publishDate) {
        this(null, null, categoryCode, name, null, publishDate, null);
    }

    public Book(Long seq, String id, String categoryCode, String name, String description, LocalDate publishDate, LocalDateTime createAt) {
        String acceptId = id;

        checkExpression(isNotEmpty(BookCategory.getCategory(categoryCode)), "카테고리는 반드시 존재해야 합니다.");
        checkExpression(StringUtils.isNotBlank(name), "상품명은 반드시 존재해야 합니다.");
        checkExpression(name.getBytes(StandardCharsets.UTF_8).length <= 200,
                "상품명은 200bytes를 넘을 수 없습니다.");
        checkDescriptionLength(description);
        checkExpression(isNotEmpty(publishDate), "상품 발간일은 반드시 존재해야 합니다.");

        if (StringUtils.isNotBlank(acceptId)) {
            checkExpression(acceptId.getBytes(StandardCharsets.UTF_8).length <= 220, "ID는 220bytes를 넘을 수 없습니다.");
        } else {
            acceptId = createId(categoryCode, publishDate, name);
        }

        this.seq = seq == null ? 0 : seq;
        this.id = acceptId;
        this.categoryCode = categoryCode;
        this.name = name;
        this.description = description;
        this.publishDate = publishDate;
        this.createAt = defaultIfNull(createAt, LocalDateTime.now());
    }

    public Long getSeq() {
        return seq;
    }

    public String getId() {
        return id;
    }

    public String getCategoryCode() {
        return categoryCode;
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

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void editDescription(String description) {
        checkDescriptionLength(description);
        this.description = description;
    }

    private String createId(String categoryCode, LocalDate publishDate, String name) {
        return getBookIdFromArguments(categoryCode, publishDate, name);
    }

    private void checkDescriptionLength(String description) {
        if (StringUtils.isNotBlank(description)) {
            checkExpression(description.getBytes(StandardCharsets.UTF_8).length <= 360,
                    "상품 설명은 360bytes를 넘을 수 없습니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        return new EqualsBuilder()
                .append(seq, book.seq)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(seq)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
                .append("seq", seq)
                .append("id", id)
                .append("categoryCode", categoryCode)
                .append("name", name)
                .append("description", description)
                .append("publishDate", publishDate)
                .append("createAt", createAt)
                .toString();
    }

    public static final class Builder {
        private Long seq;
        private String id;
        private String categoryCode;
        private String name;
        private String description;
        private LocalDate publishDate;
        private LocalDateTime createAt;

        public Builder(String categoryCode, String name, LocalDate publishDate) {
            this.categoryCode = categoryCode;
            this.name = name;
            this.publishDate = publishDate;
        }

        public Builder(Book book) {
            this.seq = book.seq;
            this.id = book.id;
            this.categoryCode = book.categoryCode;
            this.name = book.name;
            this.description = book.description;
            this.publishDate = book.publishDate;
            this.createAt = book.createAt;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Book build() {
            return new Book(seq, id, categoryCode, name, description, publishDate, createAt);
        }
    }
}
