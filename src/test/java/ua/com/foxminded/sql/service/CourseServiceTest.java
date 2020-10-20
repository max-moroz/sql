package ua.com.foxminded.sql.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.com.foxminded.sql.context.Context;
import ua.com.foxminded.sql.entity.Course;
import ua.com.foxminded.sql.exception.DAOException;
import ua.com.foxminded.sql.exception.NoSuchCourseException;
import ua.com.foxminded.sql.tools.DBAccess;
import ua.com.foxminded.sql.tools.LogConfigurator;
import ua.com.foxminded.sql.tools.ScriptReader;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


public class CourseServiceTest {

    DataSource dataSource = DBAccess.getDataSource();

    private static final Logger LOGGER = Logger.getLogger(CourseServiceTest.class.getName());

    Context context = new Context();
    ScriptReader scriptReader = context.getObject(ScriptReader.class);
    LogConfigurator logConfigurator = context.getObject(LogConfigurator.class);


    @BeforeEach
    void init() {
        String sqlQuery = scriptReader.getQuery("/scripts/tables.sql");

        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> "Cannot create schema");
        }
    }

    @Test
    void addWithCourseInstanceAsParameter_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();
        CourseService courseService = new CourseService(dataSource, mockedLogConfigurator);

        Course course = new Course();

        try {
            courseService.add(course);
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void addWithCourseInstanceAsParameter_ShouldAddCourse_WhenInvoked() {
        CourseService courseService = new CourseService(dataSource, logConfigurator);
        Course course = new Course();

        course.setCourseId(1);
        course.setCourseName("Music");
        course.setCourseDescription("This is Music Course");

        courseService.add(course);
        assertEquals(1, courseService.getAllCourses().size());
    }

    @Test
    void addWithCourseInstanceAsParameter_ShouldNotAddCourse_WhenInvokedWithWrongParameters() {
        CourseService courseService = new CourseService(dataSource, logConfigurator);
        Course course = new Course();

        course.setCourseId(1);
        course.setCourseDescription("ttttttt");
        courseService.add(course);

        assertEquals(0, courseService.getAllCourses().size());
    }


    @Test
    void addWithCourseInstanceAsParameter_ShouldReturnFalse_WhenInputEmptyInstance() {
        CourseService courseService = new CourseService(dataSource, logConfigurator);
        Course course = new Course();

        assertFalse(courseService.add(course));
    }

    @Test
    void addWithCourseNameAsParameter_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();
        CourseService courseService = new CourseService(dataSource, mockedLogConfigurator);

        try {
            courseService.add("course");
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void addWithCourseNameAsParameter_ShouldAddCourse_WhenInvoked() {
        CourseService courseService = new CourseService(dataSource, logConfigurator);
        courseService.add("Music");

        assertEquals(1, courseService.getAllCourses().size());
    }

    @Test
    void addWithCourseNameAsParameter_ShouldReturnFalse_WhenInputIsNull() {
        CourseService courseService = new CourseService(dataSource, logConfigurator);

        String name = null;
        assertFalse(courseService.add(name));
    }

    @Test
    void getAllCourses_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();
        CourseService courseService = new CourseService(dataSource, mockedLogConfigurator);

        try {
            courseService.getAllCourses();
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void getAllCourses_ShouldReturnListOfAllCourses_WhenInvoked() {
        CourseService courseService = new CourseService(dataSource, logConfigurator);
        courseService.add("Math");
        courseService.add("Biology");

        courseService.getAllCourses();
        assertEquals(2, courseService.getAllCourses().size());
    }

    @Test
    void getAllCourses_ShouldThrowException_WhenPrStatementIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        CourseService courseService = new CourseService(dataSource, logConfigurator);

        assertThrows(DAOException.class, courseService::getAllCourses);
    }

    @Test
    void getAllCourses_ShouldThrowException_WhenRsSetIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);
        PreparedStatement mockedPrStatement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenReturn(mockedPrStatement);
        when(mockedPrStatement.executeQuery()).thenThrow(new SQLException());

        CourseService courseService = new CourseService(dataSource, logConfigurator);

        assertThrows(DAOException.class, courseService::getAllCourses);
    }

    @Test
    void getCourseById_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();
        CourseService courseService = new CourseService(dataSource, mockedLogConfigurator);

        Course course = new Course();
        course.setCourseId(15);
        course.setCourseName("Science");
        course.setCourseDescription("This is Science course");

        courseService.add(course);

        try {
            courseService.getCourseById(15);
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void getCourseById_ShouldReturnCourse_WhenCourseIdInput() {
        CourseService courseService = new CourseService(dataSource, logConfigurator);
        Course course = new Course();
        course.setCourseId(15);
        course.setCourseName("Science");
        course.setCourseDescription("This is Science course");

        courseService.add(course);

        assertEquals(course, courseService.getCourseById(15));
    }

    @Test
    void getCourseById_ShouldThrowException_WhenNonExistentCourseIdInput() {
        CourseService courseService = new CourseService(dataSource, logConfigurator);
        Course course = new Course();
        course.setCourseId(15);
        course.setCourseName("Science");
        course.setCourseDescription("This is Science course");

        courseService.add(course);

        assertThrows(NoSuchCourseException.class, () -> courseService.getCourseById(2));
    }

    @Test
    void getCourseById_ShouldThrowException_WhenPrStatementIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        CourseService courseService = new CourseService(dataSource, logConfigurator);

        assertThrows(DAOException.class, () -> courseService.getCourseById(1));
    }

    @Test
    void getCourseById_ShouldThrowException_WhenRsSetIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);
        PreparedStatement mockedPrStatement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenReturn(mockedPrStatement);
        when(mockedPrStatement.executeQuery()).thenThrow(new SQLException());

        CourseService courseService = new CourseService(dataSource, logConfigurator);

        assertThrows(DAOException.class, () -> courseService.getCourseById(1));
    }

    @Test
    void update_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();
        CourseService courseService = new CourseService(dataSource, mockedLogConfigurator);

        Course course = new Course();

        try {
            courseService.update(course);
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void update_ShouldUpdateCourse_WhenInvoked() {
        CourseService courseService = new CourseService(dataSource, logConfigurator);
        Course course = new Course();

        course.setCourseId(1);
        course.setCourseName("Music");
        course.setCourseDescription("This is Music Course");

        courseService.add(course);

        Course updatedCourse = new Course();

        updatedCourse.setCourseId(1);
        updatedCourse.setCourseName("Math");
        updatedCourse.setCourseDescription("This is Math Course");

        courseService.update(updatedCourse);

        Course retrievedCourse = courseService.getCourseById(1);

        assertEquals(updatedCourse, retrievedCourse);
    }

    @Test
    void update_ShouldThrowException_WhenPrStatementIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        CourseService courseService = new CourseService(dataSource, logConfigurator);
        Course course = new Course();

        assertFalse(courseService.update(course));
    }

    @Test
    void remove_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();
        CourseService courseService = new CourseService(dataSource, mockedLogConfigurator);

        Course course = new Course();

        try {
            courseService.remove(course);
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void remove_ShouldRemoveCourse_WhenInputCourseInstance() {
        CourseService courseService = new CourseService(dataSource, logConfigurator);
        Course course = new Course();

        course.setCourseName("Music");

        courseService.add(course);

        assertTrue(courseService.remove(course));
    }

    @Test
    void remove_ShouldThrowException_WhenPrStatementIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        CourseService courseService = new CourseService(dataSource, logConfigurator);
        Course course = new Course();

        assertFalse(courseService.remove(course));
    }

}
