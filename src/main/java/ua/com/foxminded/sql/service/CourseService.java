package ua.com.foxminded.sql.service;

import ua.com.foxminded.sql.exception.DAOException;
import ua.com.foxminded.sql.exception.NoSuchCourseException;
import ua.com.foxminded.sql.tools.LogConfigurator;
import ua.com.foxminded.sql.dao.CourseDAO;
import ua.com.foxminded.sql.entity.Course;

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

public class CourseService implements CourseDAO {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String CONNECTION_OPENED = "Connection opened.";
    private static final String LOG_START = "occurred during";
    private static final String CLASS_NAME = CourseService.class.getName();

    private final DataSource dataSource;
    private final LogConfigurator logConfigurator;

    public CourseService(DataSource dataSource, LogConfigurator logConfigurator){
        this.dataSource = dataSource;
        this.logConfigurator = logConfigurator;
    }

    @Override
    public boolean add(Course course) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "INSERT INTO foxminded.courses (course_id, course_name, course_description) VALUES (?, ?, ?)";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.log(Level.INFO, CONNECTION_OPENED);

            prStatement.setInt(1, course.getCourseId());
            prStatement.setString(2, course.getCourseName());
            prStatement.setString(3, course.getCourseDescription());

            prStatement.executeUpdate();
            LOGGER.log(Level.INFO, "New course {0} added to courses table", course.getCourseName());
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " add method invocation (fail to establish connection)");
            return false;
        }
    }

    @Override
    public boolean add(String courseName) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "INSERT INTO foxminded.courses (course_name, course_description) VALUES (?, ?)";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.log(Level.INFO, CONNECTION_OPENED);

            prStatement.setString(1, courseName);
            prStatement.setString(2, "This is " + courseName + " course");

            prStatement.executeUpdate();
            LOGGER.log(Level.INFO, "New course {0} added to courses table", courseName);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " add method invocation");
            return false;
        }
    }

    @Override
    public List<Course> getAllCourses() {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        List<Course> coursesList = new ArrayList<>();
        String sqlQuery = "SELECT course_id, course_name, course_description FROM foxminded.courses";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.log(Level.INFO, CONNECTION_OPENED);

            try (ResultSet resultSet = prStatement.executeQuery()) {
                while (resultSet.next()) {
                    Course course = new Course();

                    course.setCourseId(resultSet.getInt("course_id"));
                    course.setCourseName(resultSet.getString("course_name"));
                    course.setCourseDescription(resultSet.getString("course_description"));

                    coursesList.add(course);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getAllCourses method invocation (resultSet handling)");
                throw new DAOException("ResultSet is failed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getAllCourses method invocation (statement handling)");
            throw new DAOException("PreparedStatement or Connection is failed.");
        }
        return coursesList;
    }

    @Override
    public Course getCourseById(int id) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        Course course = new Course();
        String sqlQuery = "SELECT course_id, course_name, course_description FROM foxminded.courses WHERE course_id = ?";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.log(Level.INFO, CONNECTION_OPENED);

            prStatement.setInt(1, id);

            try (ResultSet resultSet = prStatement.executeQuery()) {
                if (resultSet.next()) {
                    course.setCourseId(resultSet.getInt("course_id"));
                    course.setCourseName(resultSet.getString("course_name"));
                    course.setCourseDescription(resultSet.getString("course_description"));
                } else {
                    throw new NoSuchCourseException("The course with input id number doesn't exist");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getCourseById method invocation (resultSet handling)");
                throw new DAOException("ResultSet is failed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getCourseById method invocation (preparedStatement handling)");
            throw new DAOException("PrStatement or Connection is failed");
        }
        return course;
    }

    @Override
    public boolean update(Course course) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "UPDATE foxminded.courses SET course_name = ?, course_description = ? WHERE course_id = ?";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.log(Level.INFO, CONNECTION_OPENED);

            prStatement.setString(1, course.getCourseName());
            prStatement.setString(2, course.getCourseDescription());
            prStatement.setInt(3, course.getCourseId());

            prStatement.executeUpdate();

            LOGGER.log(Level.INFO, "Course #{0} has been successfully updated", course.getCourseId());
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " update method invocation (preparedStatement handling)");
            return false;
        }

    }

    @Override
    public boolean remove(Course course) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "DELETE FROM foxminded.courses WHERE course_id = ?";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.log(Level.INFO, CONNECTION_OPENED);

            int id = course.getCourseId();

            prStatement.setInt(1, course.getCourseId());

            prStatement.executeUpdate();

            LOGGER.log(Level.INFO, "Course # {0} has been successfully deleted", id);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " remove method invocation (preparedStatement handling)");
            return false;
        }
    }
}
