package ua.com.foxminded.sql.service;

import ua.com.foxminded.sql.exception.DAOException;
import ua.com.foxminded.sql.tools.LogConfigurator;
import ua.com.foxminded.sql.dao.StudentCourseDAO;
import ua.com.foxminded.sql.entity.Course;
import ua.com.foxminded.sql.entity.Student;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentCourseService implements StudentCourseDAO {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String CONNECTION_OPENED = "Connection opened.";
    private static final String LOG_START = "occurred during";
    private static final String CLASS_NAME = CourseService.class.getName();

    private final DataSource dataSource;
    private final LogConfigurator logConfigurator;

    public StudentCourseService(DataSource dataSource, LogConfigurator logConfigurator) {
        this.dataSource = dataSource;
        this.logConfigurator = logConfigurator;
    }

    @Override
    public boolean createTable() {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "CREATE TABLE foxminded.students_courses ( " +
                "sc_id SERIAL PRIMARY KEY, " +
                "student_id INT NOT NULL, " +
                "student_first_name VARCHAR(15) NOT NULL, " +
                "student_last_name VARCHAR(35) NOT NULL, " +
                "course_id INT NOT NULL, " +
                "course_name VARCHAR(35) NOT NULL, " +
                "FOREIGN KEY (student_id) " +
                "REFERENCES foxminded.students(student_id) " +
                "ON DELETE CASCADE, " +
                "FOREIGN KEY (course_id) " +
                "REFERENCES foxminded.courses(course_id) " +
                "ON DELETE CASCADE " +
                ");";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.info(CONNECTION_OPENED);
            prStatement.executeUpdate();
            LOGGER.log(Level.INFO, "Many-to-many table foxminded.students_courses has been successfully created");
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " add method invocation");
            return false;
        }
    }

    @Override
    public boolean add(Student student, Course course) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "INSERT INTO foxminded.students_courses (student_id, student_first_name, student_last_name, course_id, course_name) " +
                "VALUES (?, ?, ?, ?, ?);";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.info(CONNECTION_OPENED);
            prStatement.setInt(1, student.getStudentId());
            prStatement.setString(2, student.getFirstName());
            prStatement.setString(3, student.getLastName());
            prStatement.setInt(4, course.getCourseId());
            prStatement.setString(5, course.getCourseName());

            prStatement.executeUpdate();
            LOGGER.log(Level.INFO, "New course {0} assigned to student {1} {2}", new Object[]{course.getCourseName(), student.getFirstName(), student.getLastName()});
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " add method invocation");
            return false;
        }
    }

    @Override
    public List<Student> getAllStudentsAtCourse(Course course) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "SELECT student_id, student_first_name, student_last_name FROM foxminded.students_courses " +
                "WHERE course_id = ?;";

        List<Student> students = new ArrayList<>();

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.info(CONNECTION_OPENED);
            prStatement.setInt(1, course.getCourseId());

            try (ResultSet resultSet = prStatement.executeQuery()) {
                while (resultSet.next()) {
                    Student student = new Student();
                    student.setStudentId(resultSet.getInt("student_id"));
                    student.setFirstName(resultSet.getString("student_first_name"));
                    student.setLastName(resultSet.getString("student_last_name"));

                    students.add(student);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getAllStudentsAtCourse method invocation (resultSet handling)");
                throw new DAOException("ResultSet is failed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getAllStudentsAtCourse method invocation (preparedStatement handling)");
            throw new DAOException("Connection or PrStatement is failed");
        }

        return students;
    }

    @Override
    public List<Course> getAllStudentCourses(Student student) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "SELECT course_id, course_name FROM foxminded.students_courses " +
                "WHERE student_id = ?;";

        List<Course> courses = new ArrayList<>();

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.info(CONNECTION_OPENED);
            prStatement.setInt(1, student.getStudentId());

            try (ResultSet resultSet = prStatement.executeQuery()) {
                while (resultSet.next()) {
                    Course course = new Course();
                    course.setCourseId(resultSet.getInt("course_id"));
                    course.setCourseName(resultSet.getString("course_name"));

                    courses.add(course);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getAllStudentCourses method invocation (resultSet handling)");
                throw new DAOException("ResultSet is failed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getAllStudentCourses method invocation (preparedStatement handling)");
            throw new DAOException("Connection or PrStatement is failed");
        }
        return courses;
    }

    @Override
    public boolean isStudentAssignedToCourse(int studentId, int courseId) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "SELECT student_id FROM foxminded.students_courses " +
                "WHERE student_id = ? AND course_id = ?;";

        boolean statement = false;
        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.info(CONNECTION_OPENED);
            prStatement.setInt(1, studentId);
            prStatement.setInt(2, courseId);

            try (ResultSet resultSet = prStatement.executeQuery()) {
                if (resultSet.next()) {
                    statement = true;
                }
            } catch (SQLException e) {
                LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " isStudentAssignedToCourse method invocation (resultSet handling)");
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " isStudentAssignedToCourse method invocation (preparedStatement handling)");
            return false;
        }
        return statement;
    }

    @Override
    public boolean updateStudentOnCourse(Course course, Student currentStudent, Student newStudent) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "UPDATE foxminded.students_courses " +
                "SET student_id = ?, student_first_name = ?, student_last_name = ? " +
                "WHERE course_id = ? " +
                "AND student_id = ?;";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.info(CONNECTION_OPENED);

            prStatement.setInt(1, newStudent.getStudentId());
            prStatement.setString(2, newStudent.getFirstName());
            prStatement.setString(3, newStudent.getLastName());
            prStatement.setInt(4, course.getCourseId());
            prStatement.setInt(5, currentStudent.getStudentId());

            prStatement.executeUpdate();
            LOGGER.log(Level.INFO, "Student at course {0} has been successfully updated. New student id is {1}", new Object[]{course.getCourseId(), newStudent.getStudentId()});
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " updateStudentOnCourse method invocation (prepareStatement handling)");
            return false;
        }
    }

    @Override
    public boolean updateStudentsCourse(Course currentCourse, Course newCourse, Student student) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "UPDATE foxminded.students_courses " +
                "SET course_id = ?, course_name = ? " +
                "WHERE course_id = ? " +
                "AND student_id = ?;";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.info(CONNECTION_OPENED);

            prStatement.setInt(1, newCourse.getCourseId());
            prStatement.setString(2, newCourse.getCourseName());
            prStatement.setInt(3, currentCourse.getCourseId());
            prStatement.setInt(4, student.getStudentId());

            prStatement.executeUpdate();
            LOGGER.log(Level.INFO, "Course for student #{0} has been successfully updated", student.getStudentId());
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " updateStudentsCourse method invocation (prepareStatement handling)");
            return false;
        }
    }

    @Override
    public boolean removeStudent(Student student) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "DELETE FROM foxminded.students_courses " +
                "WHERE student_id = ?;";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.info(CONNECTION_OPENED);
            int id = student.getStudentId();

            prStatement.setInt(1, id);
            prStatement.executeUpdate();
            LOGGER.log(Level.INFO, "Student {0} has been successfully removed from courses.", id);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " removeStudent method invocation (prepareStatement handling)");
            return false;
        }
    }

    @Override
    public boolean removeCourse(Course course) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "DELETE FROM foxminded.students_courses " +
                "WHERE course_id = ?;";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.info(CONNECTION_OPENED);
            int id = course.getCourseId();

            prStatement.setInt(1, id);
            prStatement.executeUpdate();
            LOGGER.log(Level.INFO, "Course {0} has been successfully removed from courses.", id);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " removeCourse method invocation (prepareStatement handling)");
            return false;
        }
    }
}
