package se.kits.gakusei.gakuseiadmin.content;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import se.kits.gakusei.content.model.Quiz;

import java.util.List;

public interface AdminQuizRepository extends CrudRepository<Quiz, Long> {

    List<Quiz> findByName(String name);
    List<Quiz> findAll();

    @Query("SELECT q FROM Quiz q WHERE q.name LIKE CONCAT('%', :name, '%')")
    List<Quiz> findByNameContains(@Param("name") String name, Pageable pageRequest);

    Page<Quiz> findAll(Pageable pageRequest);

}
