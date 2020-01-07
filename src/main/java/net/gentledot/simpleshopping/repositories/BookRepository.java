package net.gentledot.simpleshopping.repositories;

import net.gentledot.simpleshopping.domain.Book;
import net.gentledot.simpleshopping.domain.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    List<Book> findAllByStatus(BookStatus status);
}
