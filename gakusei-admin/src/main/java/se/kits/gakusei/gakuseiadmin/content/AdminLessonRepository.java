package se.kits.gakusei.gakuseiadmin.content;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import se.kits.gakusei.content.model.Lesson;

public interface AdminLessonRepository extends CrudRepository<Lesson, Long> {

    Page<Lesson> findAll(Pageable pageRequest);
}
