package net.gentledot.simpleshopping.models.book;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static net.gentledot.simpleshopping.util.checkArgumentUtil.checkExpression;
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
        if (StringUtils.isNotBlank(description)) {
            checkExpression(description.getBytes(StandardCharsets.UTF_8).length <= 360,
                    "상품 설명은 360bytes를 넘을 수 없습니다.");
        }
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

    public String getCategory() {
        return categoryCode;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    private static String createId(String categoryCode, LocalDate publishDate, String name) {
        StringBuffer stringBuffer = new StringBuffer();

        return stringBuffer
                .append(categoryCode)
                .append(publishDate.getYear())
                .append(String.format("%02d", publishDate.getMonthValue()))
                .append(name.replaceAll("\\s", ""))
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

        public Builder name(String name) {
            this.name = name;
            this.id = createId(this.categoryCode, this.publishDate, this.name);
            return this;
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
