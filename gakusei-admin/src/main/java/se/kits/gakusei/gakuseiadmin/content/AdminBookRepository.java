package se.kits.gakusei.gakuseiadmin.content;

import org.springframework.data.repository.CrudRepository;
import se.kits.gakusei.content.model.Book;

public interface AdminBookRepository extends CrudRepository<Book, Long> {
}
