package se.kits.gakusei.gakuseiadmin.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
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
    public void createCourse() throws Exception {
    }

    @Test
    public void updateCourse() throws Exception {
    }

    @Test
    public void deleteCourse() throws Exception {
    }

}