package se.kits.gakusei.gakuseiadmin.content;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import se.kits.gakusei.content.model.Book;
import se.kits.gakusei.content.model.Kanji;

public interface AdminKanjiRepository extends CrudRepository<Kanji, Long> {

    Page<Kanji> findAll(Pageable pageRequest);

    Page<Kanji> findBySwedishContainingIgnoreCase(String swedish, Pageable pageRequest);

    Page<Kanji> findBySwedishContainingIgnoreCaseAndBooksIn(String swedish, Iterable<Book> books, Pageable
            pageRequest);
}
