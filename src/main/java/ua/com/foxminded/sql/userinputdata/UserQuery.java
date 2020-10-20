package ua.com.foxminded.sql.userinputdata;

import ua.com.foxminded.sql.tools.LogConfigurator;
import ua.com.foxminded.sql.tools.ScriptReader;
import ua.com.foxminded.sql.entity.Course;
import ua.com.foxminded.sql.entity.Student;
import ua.com.foxminded.sql.service.CourseService;
import ua.com.foxminded.sql.service.StudentCourseService;
import ua.com.foxminded.sql.service.StudentService;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UserQuery {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String LOG_START = "occurred during";
    private static final String CLASS_NAME = UserQuery.class.getName();

    private final TableCreator tableCreator;
    private final ScriptReader scriptReader;
    private final DataSource dataSource;
    private final LogConfigurator logConfigurator;


    public UserQuery(TableCreator tableCreator, ScriptReader scriptReader, DataSource dataSource, LogConfigurator logConfigurator) {
        this.tableCreator = tableCreator;
        this.scriptReader = scriptReader;
        this.dataSource = dataSource;
        this.logConfigurator = logConfigurator;
    }


    public String findGroupsWithLessOrEqualsStudents(int number) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = scriptReader.getQuery("/scripts/application-queries/Find_groups_with_less_or_equals_X_students.sql");

        String result = null;
        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            prStatement.setInt(1, number);

            try (ResultSet resultSet = prStatement.executeQuery()) {
                result = tableCreator.createTable(convertResSetToMap(resultSet));

                LOGGER.log(Level.INFO, "Method findGroupsWithLessOrEqualsStudents() successfully returned result.");
            } catch (SQLException e) {
                LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " findGroupsWithLessOrEqualsStudents method invocation (resultSet part)");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " findGroupsWithLessOrEqualsStudents method invocation (prStatement part)");
        }
        return result;
    }

    private Map<String, List<?>> convertResSetToMap(ResultSet resultSet) throws SQLException {
        Map<String, List<?>> tableInformation = new LinkedHashMap<>();

        if (resultSet.getMetaData().getColumnCount() == 2) {
            List<String> groupName = new LinkedList<>();
            List<Integer> count = new LinkedList<>();

            while (resultSet.next()) {
                groupName.add(resultSet.getString(1));
                count.add(resultSet.getInt(2));
            }
            tableInformation.put("group_name", groupName);
            tableInformation.put("count", count);
        } else {
            List<Integer> studentId = new LinkedList<>();
            List<String> studentFirstName = new LinkedList<>();
            List<String> studentLastName = new LinkedList<>();
            List<String> courseName = new LinkedList<>();

            while (resultSet.next()) {
                studentId.add(resultSet.getInt(1));
                studentFirstName.add(resultSet.getString(2));
                studentLastName.add(resultSet.getString(3));
                courseName.add(resultSet.getString(4));
            }
            tableInformation.put("student_id", studentId);
            tableInformation.put("student_first_name", studentFirstName);
            tableInformation.put("student_last_name", studentLastName);
            tableInformation.put("course_name", courseName);
        }

        return tableInformation;
    }

    public String findAllStudentsOfTheCourse(String courseName) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = scriptReader.getQuery("/scripts/application-queries/Find_all_students_related_to_course_with_given_name.sql");
        String result = null;

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            prStatement.setString(1, courseName);

            try (ResultSet resultSet = prStatement.executeQuery()) {
                result = tableCreator.createTable(convertResSetToMap(resultSet));

                LOGGER.log(Level.INFO, "Method findAllStudentsOfTheCourse() successfully returned result.");
            } catch (SQLException e) {
                LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " findAllStudentsOfTheCourse method invocation (resultSet part)");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " findAllStudentsOfTheCourse method invocation (prStatement part)");
        }
        return result;
    }

    public void addNewStudent(String firstName, String lastName) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = scriptReader.getQuery("/scripts/application-queries/Add_new_student.sql");

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            prStatement.setString(1, firstName);
            prStatement.setString(2, lastName);

            prStatement.executeUpdate();

            LOGGER.log(Level.INFO, "Student {0} {1} has been successfully added to the list.", new Object[]{firstName, lastName});
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " addNewStudent method invocation");
        }
    }

    public void deleteStudentById(int id) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = scriptReader.getQuery("/scripts/application-queries/Delete_student_by_student_id.sql");

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            prStatement.setInt(1, id);

            prStatement.executeUpdate();

            LOGGER.log(Level.INFO, "Student #{0} has been successfully deleted from the list.", id);
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " deleteStudentById method invocation");
        }
    }

    public void addStudentToCourse(int studentId, int courseId) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        StudentService studentService = new StudentService(dataSource, logConfigurator);
        Course course;
        StudentCourseService studentCourseService = new StudentCourseService(dataSource, logConfigurator);

        Student student = studentService.getStudentById(studentId);

        CourseService courseService = new CourseService(dataSource, logConfigurator);

        course = courseService.getCourseById(courseId);

        if (studentCourseService.isStudentAssignedToCourse(studentId, courseId)) {
            System.out.printf("%s%n", "This student has already been assigned to the Course. Please chose another Course");
        } else {

            String sqlQuery = scriptReader.getQuery("/scripts/application-queries/Add_a_student_to_the_course_(from_a_list).sql");

            try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
                prStatement.setInt(1, student.getStudentId());
                prStatement.setString(2, student.getFirstName());
                prStatement.setString(3, student.getLastName());
                prStatement.setInt(4, courseId);
                prStatement.setString(5, course.getCourseName());

                prStatement.executeUpdate();
            } catch (SQLException e) {
                LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " addStudentToCourse method invocation");
            }
        }
    }

    public List<Course> getAllCourses() {
        CourseService courseService = new CourseService(dataSource, logConfigurator);
        return courseService.getAllCourses();
    }


    public void deleteStudentFromCourse(int studentId, int courseId) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = scriptReader.getQuery("/scripts/application-queries/Remove_the_student_from_one_of_his_or_her_courses.sql");

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            prStatement.setInt(1, studentId);
            prStatement.setInt(2, courseId);

            prStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " deleteStudentFromCourse method invocation");
        }
    }
}
