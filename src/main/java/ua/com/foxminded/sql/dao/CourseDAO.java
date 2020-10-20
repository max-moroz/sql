package ua.com.foxminded.sql.dao;

import ua.com.foxminded.sql.entity.Course;

import java.util.List;

public interface CourseDAO {

    //create
    boolean add(Course course);

    boolean add(String courseName);

    //read
    List<Course> getAllCourses();

    Course getCourseById(int id);

    //update
    boolean update(Course course);

    //delete
    boolean remove(Course course);
}
