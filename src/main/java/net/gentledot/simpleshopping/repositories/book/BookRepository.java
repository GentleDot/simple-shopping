package net.gentledot.simpleshopping.repositories.book;

import net.gentledot.simpleshopping.models.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE book b SET b.id = :#{#book.getId()}, b.name = :#{#book.getName()}, " +
            "b.description = :#{#book.getDescription().orElse(null)} WHERE b.seq = :#{#book.getSeq()}")
    void update(Book book);

    @Query("SELECT b FROM book b WHERE b.id = :bookId")
    Optional<Book> findbyBookId(@Param("bookId") String id);

    @Query("SELECT b FROM book b WHERE b.categoryCode = :code ORDER BY b.publishDate DESC")
    List<Book> findAllByCategoryCode(@Param("code") String categoryCode);
}
