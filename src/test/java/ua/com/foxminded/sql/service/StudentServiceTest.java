package ua.com.foxminded.sql.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.com.foxminded.sql.context.Context;
import ua.com.foxminded.sql.entity.Group;
import ua.com.foxminded.sql.entity.Student;
import ua.com.foxminded.sql.exception.DAOException;
import ua.com.foxminded.sql.tools.DBAccess;
import ua.com.foxminded.sql.tools.LogConfigurator;
import ua.com.foxminded.sql.tools.ScriptReader;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class StudentServiceTest {

    private static final Logger LOGGER = Logger.getLogger(CourseServiceTest.class.getName());

    DataSource dataSource = DBAccess.getDataSource();

    Context context = new Context();
    ScriptReader scriptReader = context.getObject(ScriptReader.class);
    LogConfigurator logConfigurator = context.getObject(LogConfigurator.class);
    StudentService studentService = new StudentService(dataSource, logConfigurator);


    @BeforeEach
    void init() {
        String sqlQuery = scriptReader.getQuery("/scripts/tables.sql");

        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> "Cannot create schema");
        }
    }

    //-----------------
    @Test
    void addWithStudentInstanceAsParameter_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();

        Student student = new Student();

        try {
            studentService.add(student);
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void addWithStudentInstanceAsParameter_ShouldAddStudent_WhenInvoked() {
        StudentService studentService = new StudentService(dataSource, logConfigurator);
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();

        group.setGroupId(1);
        group.setGroupName("group");

        groupService.add(group);

        Student student = new Student();

        student.setGroupId(1);
        student.setFirstName("max");
        student.setLastName("max");
        studentService.add(student);
        assertEquals(1, studentService.getAllStudents().size());
    }

    @Test
    void addWithStudentInstanceAsParameter_ShouldNotAddStudent_WhenInvokedWithWrongParameters() {
        Student student = new Student();

        assertFalse(studentService.add(student));
    }

    //---------------------

    @Test
    void addWithStudentNameAsParameter_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();

        try {
            studentService.add("firstName", "secondName");
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void addWithStudentNameAsParameter_ShouldAddStudent_WhenInvoked() {
        StudentService studentService = new StudentService(dataSource, logConfigurator);
        studentService.add("firstName", "secondName");

        assertEquals(1, studentService.getAllStudents().size());
    }

    @Test
    void addWithStudentNameAsParameter_ShouldNotAddStudent_WhenInvokedWithWrongParameters() {
        String name = null;
        String surname = null;

        assertFalse(studentService.add(name, surname));
    }

    //----------------------

    @Test
    void getAllStudents_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();

        try {
            studentService.getAllStudents();
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void getAllStudents_ShouldReturnListOfAllStudents_WhenInvoked() {
        StudentService studentService = new StudentService(dataSource, logConfigurator);
        studentService.add("firstName", "secondName");
        studentService.add("firstName", "secondName");

        studentService.getAllStudents();
        assertEquals(2, studentService.getAllStudents().size());
    }

    @Test
    void getAllStudents_ShouldThrowException_WhenPrStatementIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        StudentService studentService1 = new StudentService(dataSource, logConfigurator);

        assertThrows(DAOException.class, studentService1::getAllStudents);
    }

    @Test
    void getAllStudents_ShouldThrowException_WhenRsSetIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);
        PreparedStatement mockedPrStatement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenReturn(mockedPrStatement);
        when(mockedPrStatement.executeQuery()).thenThrow(new SQLException());

        StudentService studentService = new StudentService(dataSource, logConfigurator);

        assertThrows(DAOException.class, studentService::getAllStudents);
    }

    //-------------------------

    @Test
    void getStudentById_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();

        try {
            studentService.getStudentById(1);
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void getStudentById_ShouldReturnStudent_WhenInvoked() {
        StudentService studentService = new StudentService(dataSource, logConfigurator);
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();

        group.setGroupId(1);
        group.setGroupName("group");

        groupService.add(group);

        Student student = new Student();
        student.setStudentId(1);
        student.setGroupId(1);
        student.setFirstName("max");
        student.setLastName("max");

        studentService.add(student);

        assertEquals(student, studentService.getStudentById(1));
    }


    @Test
    void getStudentById_ShouldThrowException_WhenPrStatementIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();

        group.setGroupId(1);
        group.setGroupName("group");

        groupService.add(group);

        StudentService studentService = new StudentService(dataSource, logConfigurator);
        Student student = new Student();
        student.setStudentId(1);
        student.setGroupId(1);
        student.setFirstName("max");
        student.setLastName("max");

        studentService.add(student);

        assertThrows(DAOException.class, () -> studentService.getStudentById(1));
    }

    @Test
    void getGroupById_ShouldThrowException_WhenRsSetIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);
        PreparedStatement mockedPrStatement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenReturn(mockedPrStatement);
        when(mockedPrStatement.executeQuery()).thenThrow(new SQLException());
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();

        group.setGroupId(1);
        group.setGroupName("group");

        groupService.add(group);

        StudentService studentService = new StudentService(dataSource, logConfigurator);
        Student student = new Student();
        student.setStudentId(1);
        student.setGroupId(1);
        student.setFirstName("max");
        student.setLastName("max");

        studentService.add(student);

        assertThrows(DAOException.class, () -> studentService.getStudentById(1));
    }

    //------------------

    @Test
    void getMinStudentId_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();

        try {
            studentService.getMinStudentId();
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void getMinStudentId_ShouldReturnMinStudent_WhenInvoked() {
        StudentService studentService = new StudentService(dataSource, logConfigurator);
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();

        group.setGroupId(1);
        group.setGroupName("group");

        groupService.add(group);

        Student student = new Student();
        student.setStudentId(1);
        student.setGroupId(1);
        student.setFirstName("max");
        student.setLastName("max");

        studentService.add(student);

        Student student2 = new Student();
        student2.setStudentId(2);
        student2.setGroupId(1);
        student2.setFirstName("max");
        student2.setLastName("max");

        studentService.add(student2);

        assertEquals(1, studentService.getMinStudentId());
    }


    @Test
    void getMinStudentId_ShouldThrowException_WhenPrStatementIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();

        group.setGroupId(1);
        group.setGroupName("group");

        groupService.add(group);

        StudentService studentService = new StudentService(dataSource, logConfigurator);
        Student student = new Student();
        student.setStudentId(1);
        student.setGroupId(1);
        student.setFirstName("max");
        student.setLastName("max");

        studentService.add(student);

        assertThrows(DAOException.class, studentService::getMinStudentId);
    }

    @Test
    void getMinStudentId_ShouldThrowException_WhenRsSetIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);
        PreparedStatement mockedPrStatement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenReturn(mockedPrStatement);
        when(mockedPrStatement.executeQuery()).thenThrow(new SQLException());
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();

        group.setGroupId(1);
        group.setGroupName("group");

        groupService.add(group);

        StudentService studentService = new StudentService(dataSource, logConfigurator);
        Student student = new Student();
        student.setStudentId(1);
        student.setGroupId(1);
        student.setFirstName("max");
        student.setLastName("max");

        studentService.add(student);

        assertThrows(DAOException.class, studentService::getMinStudentId);
    }

    //----------------------

    @Test
    void getMaxStudentId_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();

        try {
            studentService.getMaxStudentId();
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void getMaxStudentId_ShouldReturnMinStudent_WhenInvoked() {
        StudentService studentService = new StudentService(dataSource, logConfigurator);
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();

        group.setGroupId(1);
        group.setGroupName("group");

        groupService.add(group);

        Student student = new Student();
        student.setStudentId(1);
        student.setGroupId(1);
        student.setFirstName("max");
        student.setLastName("max");

        studentService.add(student);

        Student student2 = new Student();
        student2.setStudentId(2);
        student2.setGroupId(1);
        student2.setFirstName("max");
        student2.setLastName("max");

        studentService.add(student2);

        assertEquals(2, studentService.getMaxStudentId());
    }


    @Test
    void getMaxStudentId_ShouldThrowException_WhenPrStatementIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();

        group.setGroupId(1);
        group.setGroupName("group");

        groupService.add(group);

        StudentService studentService = new StudentService(dataSource, logConfigurator);
        Student student = new Student();
        student.setStudentId(1);
        student.setGroupId(1);
        student.setFirstName("max");
        student.setLastName("max");

        studentService.add(student);

        assertThrows(DAOException.class, studentService::getMaxStudentId);
    }

    @Test
    void getMaxStudentId_ShouldThrowException_WhenRsSetIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);
        PreparedStatement mockedPrStatement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenReturn(mockedPrStatement);
        when(mockedPrStatement.executeQuery()).thenThrow(new SQLException());
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();

        group.setGroupId(1);
        group.setGroupName("group");

        groupService.add(group);

        StudentService studentService = new StudentService(dataSource, logConfigurator);
        Student student = new Student();
        student.setStudentId(1);
        student.setGroupId(1);
        student.setFirstName("max");
        student.setLastName("max");

        studentService.add(student);

        assertThrows(DAOException.class, studentService::getMaxStudentId);
    }

    //----------------------------

    @Test
    void updateAll_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();

        Student student = new Student();

        try {
            studentService.updateAll(student);
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void updateAll_ShouldUpdateStudent_WhenInvoked() {
        StudentService studentService = new StudentService(dataSource, logConfigurator);
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();

        group.setGroupId(1);
        group.setGroupName("group");

        groupService.add(group);

        Group group5 = new Group();

        group5.setGroupId(5);
        group5.setGroupName("group5");

        groupService.add(group5);

        Student student = new Student();

        student.setStudentId(1);
        student.setGroupId(1);
        student.setFirstName("max");
        student.setLastName("max");

        studentService.add(student);

        Student updatedStudent = new Student();

        updatedStudent.setStudentId(1);
        updatedStudent.setGroupId(5);
        updatedStudent.setFirstName("leo");
        updatedStudent.setLastName("leo");

        studentService.updateAll(updatedStudent);

        assertEquals(updatedStudent, studentService.getStudentById(1));
    }

    @Test
    void updateAll_ShouldNotUpdateStudent_WhenInvokedWithWrongParameters() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);
        PreparedStatement mockedPrStatement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenReturn(mockedPrStatement);
        when(mockedPrStatement.executeUpdate()).thenThrow(new SQLException());

        StudentService studentService = new StudentService(dataSource, logConfigurator);
        Student student = new Student();

        assertFalse(studentService.updateAll(student));
    }

    //-----------------

    @Test
    void updateGroupId_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();

        Student student = new Student();

        try {
            studentService.updateGroupId(student);
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void updateGroupId_ShouldUpdateStudentGroupId_WhenInvoked() {
        StudentService studentService = new StudentService(dataSource, logConfigurator);
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();

        group.setGroupId(1);
        group.setGroupName("group");

        groupService.add(group);

        Group group5 = new Group();

        group5.setGroupId(5);
        group5.setGroupName("group5");

        groupService.add(group5);

        Student student = new Student();

        student.setStudentId(1);
        student.setGroupId(1);
        student.setFirstName("max");
        student.setLastName("max");

        studentService.add(student);

        Student updatedStudent = new Student();

        updatedStudent.setStudentId(1);
        updatedStudent.setGroupId(5);
        updatedStudent.setFirstName("max");
        updatedStudent.setLastName("max");

        studentService.updateGroupId(updatedStudent);

        assertEquals(updatedStudent, studentService.getStudentById(1));
    }

    @Test
    void updateGroupId_ShouldNotUpdateStudentGroupId_WhenInvokedWithWrongParameters() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);
        PreparedStatement mockedPrStatement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenReturn(mockedPrStatement);
        when(mockedPrStatement.executeUpdate()).thenThrow(new SQLException());

        StudentService studentService = new StudentService(dataSource, logConfigurator);
        Student student = new Student();

        assertFalse(studentService.updateGroupId(student));
    }

    //-----------------

    @Test
    void remove_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();

        Student student = new Student();

        try {
            studentService.remove(student);
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void remove_ShouldRemoveStudent_WhenInvoked() {
        StudentService studentService = new StudentService(dataSource, logConfigurator);
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();

        group.setGroupId(1);
        group.setGroupName("group");

        groupService.add(group);

        Student student = new Student();

        student.setStudentId(1);
        student.setGroupId(1);
        student.setFirstName("max");
        student.setLastName("max");

        studentService.add(student);

        studentService.remove(student);

        assertEquals(0, studentService.getAllStudents().size());
    }

    @Test
    void remove_ShouldNotRemove_WhenInvokedWithWrongParameters() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);
        PreparedStatement mockedPrStatement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenReturn(mockedPrStatement);
        when(mockedPrStatement.executeUpdate()).thenThrow(new SQLException());

        StudentService studentService = new StudentService(dataSource, logConfigurator);
        Student student = new Student();

        assertFalse(studentService.remove(student));
    }
}
