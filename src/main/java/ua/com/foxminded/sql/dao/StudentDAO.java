package ua.com.foxminded.sql.dao;

import ua.com.foxminded.sql.entity.Student;

import java.util.List;

public interface StudentDAO {

    //create
    boolean add(Student student);

    boolean add(String name, String surname);

    //read
    List<Student> getAllStudents();

    Student getStudentById(int id);

    Long getMinStudentId();

    Long getMaxStudentId();

    //update
    boolean updateAll(Student student);

    boolean updateGroupId(Student student);

    //delete
    boolean remove(Student student);

}
