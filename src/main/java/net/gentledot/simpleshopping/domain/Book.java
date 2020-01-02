package net.gentledot.simpleshopping.domain;

import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @EqualsAndHashCode.Include
    private Long bookId;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private BookCategory category;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    private LocalDateTime publishDate;

    private Integer price;

}
