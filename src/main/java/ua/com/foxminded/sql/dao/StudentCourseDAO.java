package ua.com.foxminded.sql.dao;

import ua.com.foxminded.sql.entity.Course;
import ua.com.foxminded.sql.entity.Student;

import java.util.List;

public interface StudentCourseDAO {

    //create
    boolean createTable();

    boolean add(Student student, Course course);


    //read
    List<Student> getAllStudentsAtCourse(Course course);

    List<Course> getAllStudentCourses(Student student);

    boolean isStudentAssignedToCourse(int studentId, int courseId);


    //update
    boolean updateStudentOnCourse(Course course, Student currentStudent, Student newStudent);

    boolean updateStudentsCourse(Course currentCourse, Course newCourse, Student student);


    //delete
    boolean removeStudent(Student student);

    boolean removeCourse(Course course);
}
