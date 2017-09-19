package se.kits.gakusei.gakuseiadmin.content;

import org.springframework.data.repository.CrudRepository;
import se.kits.gakusei.content.model.Course;

public interface AdminCourseRepository extends CrudRepository<Course, Long> {



}
