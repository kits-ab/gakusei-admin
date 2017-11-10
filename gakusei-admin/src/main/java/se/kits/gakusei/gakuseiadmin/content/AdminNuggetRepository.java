package se.kits.gakusei.gakuseiadmin.content;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import se.kits.gakusei.content.model.Book;
import se.kits.gakusei.content.model.Nugget;

import java.util.List;

public interface AdminNuggetRepository extends CrudRepository<Nugget, Long> {

    Page<Nugget> findAll(Pageable pageRequest);

    List<Nugget> findBySwedishContainingIgnoreCase(String swedish, Pageable pageRequest);

    List<Nugget> findBySwedishContainingIgnoreCaseAndWordTypeId(String swedish, Long id, Pageable pageRequest);

    List<Nugget> findBySwedishContainingIgnoreCaseAndBooksIn(String swedish, Iterable<Book> books, Pageable pageRequest);

    List<Nugget> findBySwedishContainingIgnoreCaseAndWordTypeIdAndBooksIn(String swedish, Long id,
                                                                          Iterable<Book> books, Pageable pageRequest);
}
