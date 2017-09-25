package se.kits.gakusei.gakuseiadmin.content;

import org.springframework.data.repository.CrudRepository;
import se.kits.gakusei.content.model.Quiz;
import java.util.List;

public interface AdminQuizRepository extends CrudRepository<Quiz, Long> {

    List<Quiz> findByName(String name);
    List<Quiz> findAll();

}
