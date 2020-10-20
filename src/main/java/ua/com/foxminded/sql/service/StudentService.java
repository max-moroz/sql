package ua.com.foxminded.sql.service;

import ua.com.foxminded.sql.exception.DAOException;
import ua.com.foxminded.sql.tools.LogConfigurator;
import ua.com.foxminded.sql.dao.StudentDAO;
import ua.com.foxminded.sql.entity.Student;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentService implements StudentDAO {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String CONNECTION_OPENED = "Connection opened.";
    private static final String GET_RESULT_SET = "Get result set.";
    private static final String LOG_START = "occurred during ";
    private static final String CLASS_NAME = StudentService.class.getName();

    private final DataSource dataSource;
    private final LogConfigurator logConfigurator;

    public StudentService(DataSource dataSource, LogConfigurator logConfigurator) {
        this.dataSource = dataSource;
        this.logConfigurator = logConfigurator;
    }

    @Override
    public boolean add(Student student) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "INSERT INTO foxminded.students (group_id, first_name, last_name) VALUES (?, ?, ?)";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.log(Level.INFO, CONNECTION_OPENED);

            prStatement.setInt(1, student.getGroupId());
            prStatement.setString(2, student.getFirstName());
            prStatement.setString(3, student.getLastName());

            prStatement.executeUpdate();
            LOGGER.log(Level.INFO, "New student: {0} {1} added to students table", new Object[]{student.getFirstName(), student.getLastName()});
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " add method invocation");
            return false;
        }
    }

    @Override
    public boolean add(String name, String surname) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        LOGGER.setLevel(Level.INFO);
        String sqlQuery = "INSERT INTO foxminded.students (first_name, last_name) VALUES (?, ?)";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.log(Level.INFO, CONNECTION_OPENED);

            prStatement.setString(1, name);
            prStatement.setString(2, surname);

            prStatement.executeUpdate();
            LOGGER.log(Level.INFO, "New student: {0} {1} added to students table", new Object[]{name, surname});
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " add method invocation");
            return false;
        }
    }

    @Override
    public List<Student> getAllStudents() {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        List<Student> studentsList = new ArrayList<>();
        String sqlQuery = "SELECT student_id, group_id, first_name, last_name FROM foxminded.students";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.info(CONNECTION_OPENED);

            try (ResultSet resultSet = prStatement.executeQuery()) {
                LOGGER.info(GET_RESULT_SET);

                while (resultSet.next()) {
                    Student student = new Student();
                    student.setStudentId(resultSet.getInt("student_id"));
                    student.setGroupId(resultSet.getInt("group_id"));
                    student.setFirstName(resultSet.getString("first_name"));
                    student.setLastName(resultSet.getString("last_name"));

                    studentsList.add(student);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getAllStudents method invocation (resultSet handling)");
                throw new DAOException("ResultSet is failed");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getAllStudents method invocation (statement handling)");
            throw new DAOException("Connection or PrStatement is failed");
        }

        return studentsList;
    }

    @Override
    public Student getStudentById(int id) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        Student student = new Student();

        String sqlQuery = "SELECT student_id, group_id, first_name, last_name FROM foxminded.students WHERE student_id = ?";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.info(CONNECTION_OPENED);

            prStatement.setInt(1, id);

            try (ResultSet resultSet = prStatement.executeQuery()) {
                while (resultSet.next()) {
                    student.setStudentId(resultSet.getInt("student_id"));
                    student.setGroupId(resultSet.getInt("group_id"));
                    student.setFirstName(resultSet.getString("first_name"));
                    student.setLastName(resultSet.getString("last_name"));
                }
            } catch (SQLException e) {
                LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getStudentById method invocation (resultSet handling)");
                throw new DAOException("ResultSet is failed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getStudentById method invocation (preparedStatement handling)");
            throw new DAOException("Connection or PrStatement is failed");
        }

        return student;
    }

    @Override
    public Long getMinStudentId() {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "SELECT MIN(student_id) FROM foxminded.students;";

        Long minId = 0L;

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            try (ResultSet resultSet = prStatement.executeQuery()) {
                while (resultSet.next()) {
                    minId = resultSet.getLong(1);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getMinStudentId method invocation (resultSet handling)");
                throw new DAOException("ResultSet is failed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getMinStudentId method invocation (preparedStatement handling)");
            throw new DAOException("Connection or PrStatement is failed");
        }
        return minId;
    }

    @Override
    public Long getMaxStudentId() {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "SELECT MAX(student_id) FROM foxminded.students;";
        Long maxId = 0L;

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            try (ResultSet resultSet = prStatement.executeQuery()) {
                while (resultSet.next()) {
                    maxId = resultSet.getLong(1);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getMaxStudentId method invocation (resultSet handling)");
                throw new DAOException("ResultSet is failed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getMaxStudentId method invocation (preparedStatement handling)");
            throw new DAOException("Connection or PrStatement is failed");
        }
        return maxId;
    }

    @Override
    public boolean updateAll(Student student) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "UPDATE foxminded.students SET group_id = ?, first_name = ?, last_name = ? WHERE student_id = ?";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            prStatement.setInt(1, student.getGroupId());
            prStatement.setString(2, student.getFirstName());
            prStatement.setString(3, student.getLastName());
            prStatement.setInt(4, student.getStudentId());

            prStatement.executeUpdate();
            LOGGER.log(Level.INFO, "Student #{0} has been successfully updated", student.getStudentId());
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " updateAll method invocation (preparedStatement handling)");
            return false;
        }
    }

    @Override
    public boolean updateGroupId(Student student) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "UPDATE foxminded.students SET group_id = ? WHERE student_id = ?";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            prStatement.setInt(1, student.getGroupId());
            prStatement.setInt(2, student.getStudentId());

            prStatement.executeUpdate();
            LOGGER.log(Level.INFO, "Student #{0} has been successfully updated", student.getStudentId());
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " updateGroupId method invocation (preparedStatement handling)");
            return false;
        }
    }

    @Override
    public boolean remove(Student student) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "DELETE FROM foxminded.students WHERE student_id = ?";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.log(Level.INFO, CONNECTION_OPENED);
            int id = student.getStudentId();
            prStatement.setInt(1, student.getStudentId());

            prStatement.executeUpdate();
            LOGGER.log(Level.INFO, "Student # {0} has been successfully deleted", id);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " remove method invocation (preparedStatement handling)");
            return false;
        }
    }
}
