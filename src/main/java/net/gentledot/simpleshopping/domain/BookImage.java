package net.gentledot.simpleshopping.domain;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BookImage {
    @EqualsAndHashCode.Include
    private Long imageId;
    private Long bookId;
    private String name;
    private String address;
}
