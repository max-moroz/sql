package ua.com.foxminded.sql.entity;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class CourseTest {

    Course course = new Course();

    @Test
    void getCourseId_ShouldReturnCourseId_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = course.getClass().getDeclaredField("courseId");
        field.setAccessible(true);
        field.set(course, 12);
        assertEquals(12, course.getCourseId());
    }

    @Test
    void setCourseId_ShouldSetCourseId_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = course.getClass().getDeclaredField("courseId");
        field.setAccessible(true);
        course.setCourseId(12);
        assertEquals(12, field.get(course));
    }

    @Test
    void getCourseName_ShouldReturnCourseName_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = course.getClass().getDeclaredField("courseName");
        field.setAccessible(true);
        field.set(course, "Math");
        assertEquals("Math", course.getCourseName());
    }

    @Test
    void setCourseName_ShouldSetCourseName_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = course.getClass().getDeclaredField("courseName");
        field.setAccessible(true);
        course.setCourseName("Math");
        assertEquals("Math", field.get(course));
    }

    @Test
    void getCourseDescription_ShouldReturnCourseDescription_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = course.getClass().getDeclaredField("courseDescription");
        field.setAccessible(true);
        field.set(course, "This is Math course");
        assertEquals("This is Math course", course.getCourseDescription());
    }

    @Test
    void setCourseDescription_ShouldSetCourseDescription_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = course.getClass().getDeclaredField("courseDescription");
        field.setAccessible(true);
        course.setCourseDescription("This is Math course");
        assertEquals("This is Math course", field.get(course));
    }

    //------------------

    @Test
    void equals_ShouldReturnTrue_ForNonNullReferenceValueOfTheSameObject() {
        Course course = new Course();
        course.setCourseId(1);
        course.setCourseName("course");
        course.setCourseDescription("course");

        assertTrue(course.equals(course));
    }

    @Test
    void equals_ShouldReturnTrue_ForSymmetricObjects() {
        Course course = new Course();
        course.setCourseId(1);
        course.setCourseName("course");
        course.setCourseDescription("course");

        Course course1 = new Course();
        course1 = course;

        assertTrue(course.equals(course1));
    }

    @Test
    void equals_ShouldReturnTrue_ForTransitiveObjects() {
        Course course = new Course();
        course.setCourseId(1);
        course.setCourseName("course");
        course.setCourseDescription("course");

        Course course1 = new Course();
        course1 = course;

        Course course2 = course1;

        assertTrue(course.equals(course2));
    }

    @Test
    void equals_ShouldReturnFalse_ForDifferentObjects() {
        Course course = new Course();
        course.setCourseId(1);
        course.setCourseName("course");
        course.setCourseDescription("course");

        Course course1 = new Course();

        assertAll(
                () -> assertFalse(course.equals(course1)),
                () -> assertFalse(course.equals(course1)),
                () -> assertFalse(course.equals(course1))
        );
    }

    @Test
    void equals_ShouldReturnFalse_ForNull() {
        Course course = new Course();
        course.setCourseId(1);
        course.setCourseName("course");
        course.setCourseDescription("course");

        assertFalse(course.equals(null));
    }

    //-----------------

    @Test
    void hasCode_ShallReturnTheSameHashCode_ForTheSameObjects() {
        Course course = new Course();
        course.setCourseId(1);
        course.setCourseName("course");
        course.setCourseDescription("course");

        Course course1 = course;

        assertEquals(course.hashCode(), course1.hashCode());
    }

    @Test
    void toString_ShallReturnStringRepresentationOfTheObject_WhenInvoked() {
        Course course = new Course();
        course.setCourseId(1);
        course.setCourseName("course");
        course.setCourseDescription("course");

        assertEquals("Courses{courseId=1, courseName='course', courseDescription='course'}", course.toString());
    }
}
