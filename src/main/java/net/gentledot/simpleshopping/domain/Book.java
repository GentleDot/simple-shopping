package net.gentledot.simpleshopping.domain;

import lombok.*;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Book {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue
    private Integer bookId;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private BookCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookStatus status;

    private LocalDateTime publishDate;

    private Integer price;

}
