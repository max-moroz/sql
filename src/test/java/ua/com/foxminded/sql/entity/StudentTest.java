package ua.com.foxminded.sql.entity;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class StudentTest {

    Student student = new Student();

    @Test
    void getStudentId_ShouldReturnStudentId_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = student.getClass().getDeclaredField("studentId");
        field.setAccessible(true);
        field.set(student, 12);
        assertEquals(12, student.getStudentId());
    }

    @Test
    void setStudentId_ShouldSetStudentId_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = student.getClass().getDeclaredField("studentId");
        field.setAccessible(true);
        student.setStudentId(12);
        assertEquals(12, field.get(student));
    }

    @Test
    void getGroupId_ShouldReturnCourseId_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = student.getClass().getDeclaredField("groupId");
        field.setAccessible(true);
        field.set(student, 12);
        assertEquals(12, student.getGroupId());
    }

    @Test
    void setGroupId_ShouldSetGroupId_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = student.getClass().getDeclaredField("groupId");
        field.setAccessible(true);
        student.setGroupId(12);
        assertEquals(12, field.get(student));
    }

    @Test
    void getFirstName_ShouldReturnFirstName_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = student.getClass().getDeclaredField("firstName");
        field.setAccessible(true);
        field.set(student, "Alex");
        assertEquals("Alex", student.getFirstName());
    }

    @Test
    void setFirstName_ShouldSetFirstName_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = student.getClass().getDeclaredField("firstName");
        field.setAccessible(true);
        student.setFirstName("Alex");
        assertEquals("Alex", field.get(student));
    }

    @Test
    void getLastName_ShouldReturnLastName_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = student.getClass().getDeclaredField("lastName");
        field.setAccessible(true);
        field.set(student, "Messi");
        assertEquals("Messi", student.getLastName());
    }

    @Test
    void setLastName_ShouldSetLastName_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = student.getClass().getDeclaredField("lastName");
        field.setAccessible(true);
        student.setLastName("Messi");
        assertEquals("Messi", field.get(student));
    }

    //------------------

    @Test
    void equals_ShouldReturnTrue_ForNonNullReferenceValueOfTheSameObject() {
        Student student = new Student();
        student.setStudentId(1);
        student.setFirstName("firstName");
        student.setLastName("lastName");
        student.setGroupId(10);

        assertTrue(student.equals(student));
    }

    @Test
    void equals_ShouldReturnTrue_ForSymmetricObjects() {
        Student student = new Student();
        student.setStudentId(1);
        student.setFirstName("firstName");
        student.setLastName("lastName");
        student.setGroupId(10);

        Student student1 = student;

        assertTrue(student.equals(student1));
    }

    @Test
    void equals_ShouldReturnTrue_ForTransitiveObjects() {
        Student student = new Student();
        student.setStudentId(1);
        student.setFirstName("firstName");
        student.setLastName("lastName");
        student.setGroupId(10);

        Student student1 = student;
        Student student2 = student1;

        assertTrue(student.equals(student2));
    }

    @Test
    void equals_ShouldReturnFalse_ForDifferentObjects() {
        Student student = new Student();
        student.setStudentId(1);
        student.setFirstName("firstName");
        student.setLastName("lastName");
        student.setGroupId(10);

        Student student1 = new Student();

        assertAll(
                () -> assertFalse(student.equals(student1)),
                () -> assertFalse(student.equals(student1)),
                () -> assertFalse(student.equals(student1))
        );
    }

    @Test
    void equals_ShouldReturnFalse_ForNull() {
        Student student = new Student();
        student.setStudentId(1);
        student.setFirstName("firstName");
        student.setLastName("lastName");
        student.setGroupId(10);

        assertFalse(student.equals(null));
    }

    //-----------------

    @Test
    void hasCode_ShallReturnTheSameHashCode_ForTheSameObjects() {
        Student student = new Student();
        student.setStudentId(1);
        student.setFirstName("firstName");
        student.setLastName("lastName");
        student.setGroupId(10);

        Student student1 = student;

        assertEquals(student.hashCode(), student1.hashCode());
    }

    @Test
    void toString_ShallReturnStringRepresentationOfTheObject_WhenInvoked() {
        Student student = new Student();
        student.setStudentId(1);
        student.setFirstName("firstName");
        student.setLastName("lastName");
        student.setGroupId(10);

        assertEquals("Students{studentId=1, groupId=10, firstName='firstName', lastName='lastName'}", student.toString());
    }
}
