package net.gentledot.simpleshopping.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.gentledot.simpleshopping.domain.BookCategory;
import net.gentledot.simpleshopping.domain.BookStatus;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private BookCategory category;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    private LocalDateTime publishDate;

    private Integer price;
}
