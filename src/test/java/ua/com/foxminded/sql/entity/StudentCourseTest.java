package ua.com.foxminded.sql.entity;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class StudentCourseTest {

    StudentCourse studentCourse = new StudentCourse();

    @Test
    void getStudentCourseId_ShouldReturnStudentCourseId_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = studentCourse.getClass().getDeclaredField("studentCourseId");
        field.setAccessible(true);
        field.set(studentCourse, 12);
        assertEquals(12, studentCourse.getStudentCourseId());
    }

    @Test
    void setStudentCourseId_ShouldSetStudentCourseId_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = studentCourse.getClass().getDeclaredField("studentCourseId");
        field.setAccessible(true);
        studentCourse.setStudentCourseId(12);
        assertEquals(12, field.get(studentCourse));
    }

    @Test
    void getStudentId_ShouldReturnStudentId_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = studentCourse.getClass().getDeclaredField("studentId");
        field.setAccessible(true);
        field.set(studentCourse, 12);
        assertEquals(12, studentCourse.getStudentId());
    }

    @Test
    void setStudentId_ShouldSetStudentId_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = studentCourse.getClass().getDeclaredField("studentId");
        field.setAccessible(true);
        studentCourse.setStudentId(12);
        assertEquals(12, field.get(studentCourse));
    }

    @Test
    void getStudentFirstName_ShouldReturnStudentFirstName_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = studentCourse.getClass().getDeclaredField("studentFirstName");
        field.setAccessible(true);
        field.set(studentCourse, "Alex");
        assertEquals("Alex", studentCourse.getStudentFirstName());
    }

    @Test
    void setStudentFirstName_ShouldSetStudentFirstName_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = studentCourse.getClass().getDeclaredField("studentFirstName");
        field.setAccessible(true);
        studentCourse.setStudentFirstName("Alex");
        assertEquals("Alex", field.get(studentCourse));
    }

    @Test
    void getStudentLastName_ShouldReturnStudentLastName_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = studentCourse.getClass().getDeclaredField("studentLastName");
        field.setAccessible(true);
        field.set(studentCourse, "Messi");
        assertEquals("Messi", studentCourse.getStudentLastName());
    }

    @Test
    void setStudentLastName_ShouldSetStudentLastName_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = studentCourse.getClass().getDeclaredField("studentLastName");
        field.setAccessible(true);
        studentCourse.setStudentLastName("Messi");
        assertEquals("Messi", field.get(studentCourse));
    }

    @Test
    void getCourseId_ShouldReturnCourseId_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = studentCourse.getClass().getDeclaredField("courseId");
        field.setAccessible(true);
        field.set(studentCourse, 12);
        assertEquals(12, studentCourse.getCourseId());
    }

    @Test
    void setCourseId_ShouldSetCourseId_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = studentCourse.getClass().getDeclaredField("courseId");
        field.setAccessible(true);
        studentCourse.setCourseId(12);
        assertEquals(12, field.get(studentCourse));
    }

    @Test
    void getCourseName_ShouldReturnCourseName_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = studentCourse.getClass().getDeclaredField("courseName");
        field.setAccessible(true);
        field.set(studentCourse, "Math");
        assertEquals("Math", studentCourse.getCourseName());
    }

    @Test
    void setCourseName_ShouldSetCourseName_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = studentCourse.getClass().getDeclaredField("courseName");
        field.setAccessible(true);
        studentCourse.setCourseName("Math");
        assertEquals("Math", field.get(studentCourse));
    }

    //------------------

    @Test
    void equals_ShouldReturnTrue_ForNonNullReferenceValueOfTheSameObject() {
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setStudentId(1);
        studentCourse.setStudentCourseId(1);
        studentCourse.setStudentFirstName("firstName");
        studentCourse.setStudentLastName("lastName");

        assertTrue(studentCourse.equals(studentCourse));
    }

    @Test
    void equals_ShouldReturnTrue_ForSymmetricObjects() {
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setStudentId(1);
        studentCourse.setStudentCourseId(1);
        studentCourse.setStudentFirstName("firstName");
        studentCourse.setStudentLastName("lastName");

        StudentCourse studentCourse1 = studentCourse;

        assertTrue(studentCourse.equals(studentCourse1));
    }

    @Test
    void equals_ShouldReturnTrue_ForTransitiveObjects() {
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setStudentId(1);
        studentCourse.setStudentCourseId(1);
        studentCourse.setStudentFirstName("firstName");
        studentCourse.setStudentLastName("lastName");

        StudentCourse studentCourse1 = studentCourse;
        StudentCourse studentCourse2 = studentCourse1;

        assertTrue(studentCourse.equals(studentCourse2));
    }

    @Test
    void equals_ShouldReturnFalse_ForDifferentObjects() {
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setStudentId(1);
        studentCourse.setStudentCourseId(1);
        studentCourse.setStudentFirstName("firstName");
        studentCourse.setStudentLastName("lastName");

        StudentCourse studentCourse1 = new StudentCourse();

        assertAll(
                () -> assertFalse(studentCourse.equals(studentCourse1)),
                () -> assertFalse(studentCourse.equals(studentCourse1)),
                () -> assertFalse(studentCourse.equals(studentCourse1))
        );
    }

    @Test
    void equals_ShouldReturnFalse_ForNull() {
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setStudentId(1);
        studentCourse.setStudentCourseId(1);
        studentCourse.setStudentFirstName("firstName");
        studentCourse.setStudentLastName("lastName");

        assertFalse(studentCourse.equals(null));
    }

    //-----------------

    @Test
    void hasCode_ShallReturnTheSameHashCode_ForTheSameObjects() {
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setStudentId(1);
        studentCourse.setStudentCourseId(1);
        studentCourse.setStudentFirstName("firstName");
        studentCourse.setStudentLastName("lastName");

        StudentCourse studentCourse1 = studentCourse;

        assertEquals(studentCourse.hashCode(), studentCourse1.hashCode());
    }

    @Test
    void toString_ShallReturnStringRepresentationOfTheObject_WhenInvoked() {
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setStudentId(1);
        studentCourse.setStudentCourseId(1);
        studentCourse.setStudentFirstName("firstName");
        studentCourse.setStudentLastName("lastName");

        assertEquals("StudentCourse{studentCourseId=1, studentId=1, studentFirstName='firstName', studentLastName='lastName', courseId=0, courseName='null'}", studentCourse.toString());
    }
}
