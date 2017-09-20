package se.kits.gakusei.gakuseiadmin.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import se.kits.gakusei.content.model.Course;
import se.kits.gakusei.content.repository.CourseRepository;
import se.kits.gakusei.gakuseiadmin.tools.AdminTestTools;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AdminCourseControllerTest {

    @InjectMocks
    private AdminCourseController adminCourseController;

    @Mock
    private CourseRepository courseRepository;

    private Course testCourse;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        testCourse = AdminTestTools.createCourse();
    }

    @Test
    public void testCreateCourseOK() throws Exception {
        Mockito.when(courseRepository.exists(testCourse.getId())).thenReturn(false);
        Mockito.when(courseRepository.save(testCourse)).thenReturn(testCourse);

        ResponseEntity<Course> re = adminCourseController.createCourse(testCourse);

        assertEquals(HttpStatus.CREATED, re.getStatusCode());
        assertEquals(testCourse, re.getBody());

    }

    @Test
    public void testCreateCourseBadRequest() {
        Mockito.when(courseRepository.exists(testCourse.getId())).thenReturn(true);

        ResponseEntity<Course> re = adminCourseController.createCourse(testCourse);

        assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
    }

    @Test
    public void testUpdateCourseOK() throws Exception {
        Mockito.when(courseRepository.exists(testCourse.getId())).thenReturn(true);
        testCourse.setName("Updated name");
        Mockito.when(courseRepository.save(testCourse)).thenReturn(testCourse);

        ResponseEntity<Course> re = adminCourseController.updateCourse(testCourse);

        assertEquals(HttpStatus.OK, re.getStatusCode());
        assertEquals(testCourse, re.getBody());

    }

    @Test
    public void testUpdateCourseNotFound() {
        Mockito.when(courseRepository.exists(testCourse.getId())).thenReturn(false);

        ResponseEntity<Course> re = adminCourseController.updateCourse(testCourse);

        assertEquals(HttpStatus.NOT_FOUND, re.getStatusCode());
    }

    @Test
    public void testDeleteCourseOK() throws Exception {
        Mockito.when(courseRepository.exists(testCourse.getId())).thenReturn(true);

        ResponseEntity<Course> re = adminCourseController.deleteCourse(testCourse.getId());

        assertEquals(HttpStatus.OK, re.getStatusCode());
    }

}