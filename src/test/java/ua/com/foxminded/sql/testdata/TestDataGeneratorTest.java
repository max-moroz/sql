package ua.com.foxminded.sql.testdata;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ua.com.foxminded.sql.context.Context;
import ua.com.foxminded.sql.entity.Course;
import ua.com.foxminded.sql.entity.Student;
import ua.com.foxminded.sql.service.*;
import ua.com.foxminded.sql.tools.DBAccess;
import ua.com.foxminded.sql.tools.LogConfigurator;
import ua.com.foxminded.sql.tools.ScriptReader;

import javax.sql.DataSource;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestDataGeneratorTest {

    private static final Logger LOGGER = Logger.getLogger(CourseServiceTest.class.getName());
    private static DataSource dataSource;
    private static LogConfigurator logConfigurator;

    @BeforeAll
    static void setUp() throws NoSuchAlgorithmException {
        Context context = new Context();
        ScriptReader scriptReader = context.getObject(ScriptReader.class);
        logConfigurator = context.getObject(LogConfigurator.class);

        String sqlQuery = scriptReader.getQuery("/scripts/tables.sql");

        try (Connection connection = DBAccess.getDataSource().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> "Cannot create schema");
        }

        dataSource = DBAccess.getDataSource();
        TestDataGenerator dataGenerator = new TestDataGenerator(dataSource, logConfigurator);

        dataGenerator.create10Groups();
        dataGenerator.create10Courses();
        dataGenerator.create200Students();
        dataGenerator.assignStudentsToGroups();
        dataGenerator.createStudentCourseTable();
    }


    @Test
    void create10Groups_ShouldCreate10Groups_WhenInvoked() {
        GroupService groupService = new GroupService(dataSource, logConfigurator);

        assertEquals(10, groupService.getAllGroups().size());
    }

    @Test
    void create10Courses_ShouldCreate10Courses_WhenInvoked() {
        CourseService courseService = new CourseService(dataSource, logConfigurator);

        assertEquals(10, courseService.getAllCourses().size());
    }

    @Test
    void create200Students_ShouldCreate200Students_WhenInvoked() {
        StudentService studentService = new StudentService(dataSource, logConfigurator);

        assertEquals(200, studentService.getAllStudents().size());
    }

    @Test
    void assignStudentsToGroups_ShouldNotCreateGroupsWithLessThen10Students_WhenInvoked() {
        GroupService groupService = new GroupService(dataSource, logConfigurator);

        int minStudentsNumber = 0;
        for (int i = 0; i < groupService.getAllGroups().size(); i++) {
            if (groupService.getStudentsNumberInGroup(i) < 10 && groupService.getStudentsNumberInGroup(i) != 0) {
                minStudentsNumber = groupService.getStudentsNumberInGroup(i);
            }
        }

        assertEquals(0, minStudentsNumber);
    }

    @Test
    void assignStudentsToGroups_ShouldNotCreateGroupsWithMoreThen30Students_WhenInvoked() {
        GroupService groupService = new GroupService(dataSource, logConfigurator);

        int maxStudentsNumber = 30;
        for (int i = 0; i < groupService.getAllGroups().size(); i++) {
            if (groupService.getStudentsNumberInGroup(i) > 30) {
                maxStudentsNumber = groupService.getStudentsNumberInGroup(i);
            }
        }

        assertEquals(30, maxStudentsNumber);
    }

    @Test
    void createStudentCourseTable_ShouldCreateTable_WhenInvoked() {
        StudentCourseService studentCourse = new StudentCourseService(dataSource, logConfigurator);

        CourseService courseService = new CourseService(dataSource, logConfigurator);
        Course course = courseService.getAllCourses().get(0);

        assertNotNull(studentCourse.getAllStudentsAtCourse(course));
    }

    @Test
    void createStudentCourseTable_ShouldAssignMoreThen0CourseToEachStudent_WhenInvoked() {
        StudentCourseService studentCourse = new StudentCourseService(dataSource, logConfigurator);

        StudentService studentService = new StudentService(dataSource, logConfigurator);
        List<Student> students = studentService.getAllStudents();

        int min = 1;
        for (Student s : students) {
            if (studentCourse.getAllStudentCourses(s).size() < min) {
                min = studentCourse.getAllStudentCourses(s).size();
            }
        }

        assertEquals(1, min);
    }

    @Test
    void createStudentCourseTable_ShouldAssignNotMoreThen3CoursesToEachStudent_WhenInvoked() {
        StudentCourseService studentCourse = new StudentCourseService(dataSource, logConfigurator);

        StudentService studentService = new StudentService(dataSource, logConfigurator);
        List<Student> students = studentService.getAllStudents();

        int max = 3;
        for (Student s : students) {
            if (studentCourse.getAllStudentCourses(s).size() > max) {
                max = studentCourse.getAllStudentCourses(s).size();
            }
        }

        assertEquals(3, max);
    }
}
