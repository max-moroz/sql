package ua.com.foxminded.sql.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.com.foxminded.sql.context.Context;
import ua.com.foxminded.sql.entity.Course;
import ua.com.foxminded.sql.entity.Group;
import ua.com.foxminded.sql.entity.Student;
import ua.com.foxminded.sql.exception.DAOException;
import ua.com.foxminded.sql.testdata.TestDataGenerator;
import ua.com.foxminded.sql.tools.DBAccess;
import ua.com.foxminded.sql.tools.LogConfigurator;
import ua.com.foxminded.sql.tools.ScriptReader;

import javax.sql.DataSource;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class StudentCourseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(CourseServiceTest.class.getName());

    DataSource dataSource = DBAccess.getDataSource();

    Context context = new Context();
    ScriptReader scriptReader = context.getObject(ScriptReader.class);
    LogConfigurator logConfigurator = context.getObject(LogConfigurator.class);
    TestDataGenerator dataGenerator = new TestDataGenerator(dataSource, logConfigurator);
    StudentCourseService studentCourseService = new StudentCourseService(dataSource, logConfigurator);

    public StudentCourseServiceTest() throws NoSuchAlgorithmException {
    }


    @BeforeEach
    void init() {
        String sqlQuery = scriptReader.getQuery("/scripts/tables.sql");

        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> "Cannot create schema");
        }
    }

    //----------------------
    @Test
    void createTable_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();

        try {
            studentCourseService.createTable();
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void createTable_ShouldCreateTable_WhenInvoked() {
        StudentCourseService service = new StudentCourseService(dataSource, logConfigurator);
        assertTrue(studentCourseService.createTable());
    }

    @Test
    void createTable_ShouldNotCreateTable_WhenInvokedWithWrongParameters() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        StudentCourseService studentCourseService = new StudentCourseService(dataSource, logConfigurator);

        assertFalse(studentCourseService.createTable());
    }

    //----------------------
    @Test
    void add_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        Student student = new Student();
        Course course = new Course();
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();

        try {
            studentCourseService.add(student, course);
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void add_ShouldAddStudentAndCourse_WhenInvoked() {
        studentCourseService.createTable();

        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();

        group.setGroupId(1);
        group.setGroupName("group");

        groupService.add(group);

        Student student = new Student();
        StudentService studentService = new StudentService(dataSource, logConfigurator);
        student.setStudentId(1);
        student.setGroupId(1);
        student.setFirstName("name");
        student.setLastName("surname");

        studentService.add(student);

        Course course = new Course();
        CourseService courseService = new CourseService(dataSource, logConfigurator);
        course.setCourseId(1);
        course.setCourseDescription("course");
        course.setCourseName("course");

        courseService.add(course);

        studentCourseService.add(student, course);

        assertTrue(studentCourseService.isStudentAssignedToCourse(1, 1));
    }

    @Test
    void add_ShouldNotAddStudentAndCourse_WhenInvokedWithWrongParameters() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        StudentCourseService studentCourseService = new StudentCourseService(dataSource, logConfigurator);

        Student student = new Student();
        Course course = new Course();
        assertFalse(studentCourseService.add(student, course));
    }

    //----------------------
    @Test
    void getAllStudentsAtCourse_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        Course course = new Course();
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();

        StudentCourseService service = new StudentCourseService(dataSource, mockedLogConfigurator);
        studentCourseService.createTable();
        try {
            service.getAllStudentsAtCourse(course);
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void getAllStudentsAtCourse_ShouldGetAllStudentsAtParticularCourse_WhenInvoked() {
        studentCourseService.createTable();

        Course course = new Course();
        CourseService courseService = new CourseService(dataSource, logConfigurator);
        course.setCourseId(1);
        course.setCourseDescription("course");
        course.setCourseName("course");

        courseService.add(course);

        StudentService studentService = new StudentService(dataSource, logConfigurator);
        for (int i = 1; i <= 10; i++) {
            Student student = new Student();
            student.setStudentId(i);
            student.setFirstName("name");
            student.setLastName("surname");

            studentService.add(student);
            studentCourseService.add(student, course);
        }

        assertEquals(10, studentCourseService.getAllStudentsAtCourse(course).size());
    }

    @Test
    void getAllStudentsAtCourse_ShouldNotAddStudentAndCourse_WhenInvokedWithWrongParameters() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        StudentCourseService studentCourseService = new StudentCourseService(dataSource, logConfigurator);

        Course course = new Course();
        assertThrows(DAOException.class, () -> studentCourseService.getAllStudentsAtCourse(course));
    }

    @Test
    void getAllStudentsAtCourse_ShouldThrowException_WhenRsSetIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);
        PreparedStatement mockedPrStatement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenReturn(mockedPrStatement);
        when(mockedPrStatement.executeQuery()).thenThrow(new SQLException());

        StudentCourseService service = new StudentCourseService(dataSource, logConfigurator);
        Course course = new Course();

        assertThrows(DAOException.class, () -> service.getAllStudentsAtCourse(course));
    }

    //----------------------
    @Test
    void getAllStudentCourses_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        Student student = new Student();
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();

        StudentCourseService service = new StudentCourseService(dataSource, mockedLogConfigurator);
        studentCourseService.createTable();
        try {
            service.getAllStudentCourses(student);
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void getAllStudentCourses_ShouldGetAllStudentCourses_WhenInvoked() {
        studentCourseService.createTable();

        StudentService studentService = new StudentService(dataSource, logConfigurator);

        Student student = new Student();
        student.setStudentId(1);
        student.setFirstName("name");
        student.setLastName("surname");

        studentService.add(student);

        CourseService courseService = new CourseService(dataSource, logConfigurator);

        for (int i = 1; i <= 3; i++) {
            Course course = new Course();

            course.setCourseId(i);
            course.setCourseDescription("course " + i);
            course.setCourseName("course " + i);

            courseService.add(course);
            studentCourseService.add(student, course);
        }

        assertEquals(3, studentCourseService.getAllStudentCourses(student).size());
    }

    @Test
    void getAllStudentCourses_ShouldNotAddStudentAtCourse_WhenInvokedWithWrongParameters() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        StudentCourseService studentCourseService = new StudentCourseService(dataSource, logConfigurator);

        Student student = new Student();
        assertThrows(DAOException.class, () -> studentCourseService.getAllStudentCourses(student));
    }

    @Test
    void getAllStudentCourses_ShouldThrowException_WhenRsSetIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);
        PreparedStatement mockedPrStatement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenReturn(mockedPrStatement);
        when(mockedPrStatement.executeQuery()).thenThrow(new SQLException());

        StudentCourseService service = new StudentCourseService(dataSource, logConfigurator);
        Student student = new Student();

        assertThrows(DAOException.class, () -> service.getAllStudentCourses(student));
    }

    //----------------------
    @Test
    void isStudentAssignedToCourse_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();

        StudentCourseService service = new StudentCourseService(dataSource, mockedLogConfigurator);
        studentCourseService.createTable();
        try {
            service.isStudentAssignedToCourse(1, 1);
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void isStudentAssignedToCourse_ShouldReturnTrue_WhenStudentAssignedToCourse() {
        studentCourseService.createTable();

        StudentService studentService = new StudentService(dataSource, logConfigurator);

        Student student = new Student();
        student.setStudentId(1);
        student.setFirstName("name");
        student.setLastName("surname");

        studentService.add(student);

        CourseService courseService = new CourseService(dataSource, logConfigurator);

        Course course = new Course();

        course.setCourseId(1);
        course.setCourseDescription("course");
        course.setCourseName("course");

        courseService.add(course);
        studentCourseService.add(student, course);


        assertTrue(studentCourseService.isStudentAssignedToCourse(1, 1));
    }

    @Test
    void isStudentAssignedToCourse_ShouldReturnFalse_WhenStudentHasNotBeenAssignedToTheCourse() {
        studentCourseService.createTable();

        StudentService studentService = new StudentService(dataSource, logConfigurator);

        Student student = new Student();
        student.setStudentId(1);
        student.setFirstName("name");
        student.setLastName("surname");

        studentService.add(student);

        CourseService courseService = new CourseService(dataSource, logConfigurator);

        Course course = new Course();

        course.setCourseId(2);
        course.setCourseDescription("course");
        course.setCourseName("course");

        courseService.add(course);
        studentCourseService.add(student, course);


        assertFalse(studentCourseService.isStudentAssignedToCourse(1, 1));
    }

    @Test
    void isStudentAssignedToCourse_ShouldReturnFalse_WhenInvokedWithWrongParameters() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        StudentCourseService studentCourseService = new StudentCourseService(dataSource, logConfigurator);

        assertFalse(studentCourseService.isStudentAssignedToCourse(1, 1));
    }

    @Test
    void isStudentAssignedToCourse_ShouldReturnFalse_WhenRsSetIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);
        PreparedStatement mockedPrStatement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenReturn(mockedPrStatement);
        when(mockedPrStatement.executeQuery()).thenThrow(new SQLException());

        StudentCourseService service = new StudentCourseService(dataSource, logConfigurator);

        assertFalse(service.isStudentAssignedToCourse(1, 1));
    }

    //----------------------
    @Test
    void updateStudentOnCourse_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();

        StudentCourseService service = new StudentCourseService(dataSource, mockedLogConfigurator);
        studentCourseService.createTable();
        Student student = new Student();
        Student student1 = new Student();
        Course course = new Course();

        try {
            service.updateStudentOnCourse(course, student, student1);
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void updateStudentOnCourse_ShouldReturnTrue_WhenStudentAssignedToCourse() {
        studentCourseService.createTable();

        StudentService studentService = new StudentService(dataSource, logConfigurator);

        Student student = new Student();
        student.setStudentId(1);
        student.setFirstName("name");
        student.setLastName("surname");

        studentService.add(student);

        Student student1 = new Student();
        student1.setStudentId(1);
        student1.setFirstName("name1");
        student1.setLastName("surname1");

        studentService.add(student1);

        CourseService courseService = new CourseService(dataSource, logConfigurator);

        Course course = new Course();

        course.setCourseId(1);
        course.setCourseDescription("course");
        course.setCourseName("course");

        courseService.add(course);
        studentCourseService.add(student, course);

        assertTrue(studentCourseService.updateStudentOnCourse(course, student, student1));
    }

    @Test
    void updateStudentOnCourse_ShouldReturnFalse_WhenInvoked() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        StudentCourseService studentCourseService = new StudentCourseService(dataSource, logConfigurator);
        Course course = new Course();
        Student student = new Student();
        Student student1 = new Student();

        assertFalse(studentCourseService.updateStudentOnCourse(course, student, student1));
    }

    //----------------------
    @Test
    void updateStudentsCourse_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();

        StudentCourseService service = new StudentCourseService(dataSource, mockedLogConfigurator);
        studentCourseService.createTable();
        Student student = new Student();
        Course course1 = new Course();
        Course course = new Course();

        try {
            service.updateStudentsCourse(course, course1, student);
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void updateStudentsCourse_ShouldReturnTrue_WhenStudentAssignedToCourse() {
        studentCourseService.createTable();

        StudentService studentService = new StudentService(dataSource, logConfigurator);

        Student student = new Student();
        student.setStudentId(1);
        student.setFirstName("name");
        student.setLastName("surname");

        studentService.add(student);

        CourseService courseService = new CourseService(dataSource, logConfigurator);

        Course course = new Course();

        course.setCourseId(1);
        course.setCourseDescription("course");
        course.setCourseName("course");

        courseService.add(course);
        studentCourseService.add(student, course);

        Course course2 = new Course();

        course.setCourseId(2);
        course.setCourseDescription("course2");
        course.setCourseName("course2");

        courseService.add(course2);

        assertTrue(studentCourseService.updateStudentsCourse(course, course2, student));
    }

    @Test
    void updateStudentsCourse_ShouldReturnFalse_WhenInvoked() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        StudentCourseService studentCourseService = new StudentCourseService(dataSource, logConfigurator);
        Course course = new Course();
        Course course1 = new Course();
        Student student = new Student();

        assertFalse(studentCourseService.updateStudentsCourse(course, course1, student));
    }

    //----------------------
    @Test
    void removeStudent_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();

        StudentCourseService service = new StudentCourseService(dataSource, mockedLogConfigurator);
        studentCourseService.createTable();
        Student student = new Student();


        try {
            service.removeStudent(student);
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void removeStudent_ShouldReturnTrue_WhenStudentRemovedFromACourse() {
        studentCourseService.createTable();

        StudentService studentService = new StudentService(dataSource, logConfigurator);

        Student student = new Student();
        student.setStudentId(1);
        student.setFirstName("name");
        student.setLastName("surname");

        studentService.add(student);

        CourseService courseService = new CourseService(dataSource, logConfigurator);

        Course course = new Course();

        course.setCourseId(1);
        course.setCourseDescription("course");
        course.setCourseName("course");

        courseService.add(course);
        studentCourseService.add(student, course);
        studentCourseService.removeStudent(student);

        assertFalse(studentCourseService.isStudentAssignedToCourse(1, 1));
    }

    @Test
    void removeStudent_ShouldReturnFalse_WhenInvoked() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        StudentCourseService studentCourseService = new StudentCourseService(dataSource, logConfigurator);
        Student student = new Student();

        assertFalse(studentCourseService.removeStudent(student));
    }

    //----------------------
    @Test
    void removeCourse_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();

        StudentCourseService service = new StudentCourseService(dataSource, mockedLogConfigurator);
        studentCourseService.createTable();

        Course course = new Course();

        try {
            service.removeCourse(course);
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void removeCourse_ShouldReturnTrue_WhenStudentAssignedToCourse() {
        studentCourseService.createTable();

        StudentService studentService = new StudentService(dataSource, logConfigurator);

        Student student = new Student();
        student.setStudentId(1);
        student.setFirstName("name");
        student.setLastName("surname");

        studentService.add(student);

        CourseService courseService = new CourseService(dataSource, logConfigurator);

        Course course = new Course();

        course.setCourseId(1);
        course.setCourseDescription("course");
        course.setCourseName("course");

        courseService.add(course);
        studentCourseService.add(student, course);
        studentCourseService.removeCourse(course);

        assertFalse(studentCourseService.isStudentAssignedToCourse(1, 1));
    }

    @Test
    void removeCourse_ShouldReturnFalse_WhenInvoked() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        StudentCourseService studentCourseService = new StudentCourseService(dataSource, logConfigurator);
        Course course = new Course();
        Student student = new Student();

        assertFalse(studentCourseService.removeCourse(course));
    }
}
