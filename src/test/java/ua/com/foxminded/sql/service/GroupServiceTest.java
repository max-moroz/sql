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
import java.util.*;
import java.util.logging.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class GroupServiceTest {

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
    void addWithGroupInstanceAsParameter_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();
        GroupService groupService = new GroupService(dataSource, mockedLogConfigurator);

        Group group = new Group();

        try {
            groupService.add(group);
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void addWithGroupInstanceAsParameter_ShouldAddGroup_WhenInvoked() {
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();

        group.setGroupId(1);
        group.setGroupName("SR-15");
        groupService.add(group);

        assertEquals(1, groupService.getAllGroups().size());
    }

    @Test
    void addWithGroupInstanceAsParameter_ShouldNotAddGroup_WhenInvokedWithWrongParameters() {
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();

        assertFalse(groupService.add(group));
    }

    @Test
    void addWithGroupNameAsParameter_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();
        GroupService groupService = new GroupService(dataSource, mockedLogConfigurator);

        try {
            groupService.add("group");
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void addWithGroupNameAsParameter_ShouldAddGroup_WhenInvoked() {
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        groupService.add("group");

        assertEquals(1, groupService.getAllGroups().size());
    }

    @Test
    void addWithGroupNameAsParameter_ShouldNotAddGroup_WhenInputIsNull() {
        GroupService groupService = new GroupService(dataSource, logConfigurator);

        String name = null;

        assertFalse(groupService.add(name));
    }

    @Test
    void getAllGroups_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();
        GroupService groupService = new GroupService(dataSource, mockedLogConfigurator);

        try {
            groupService.getAllGroups();
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void getAllGroups_ShouldReturnListOfAllGroups_WhenInvoked() {
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        groupService.add("group_1");
        groupService.add("group_2");

        groupService.getAllGroups();
        assertEquals(2, groupService.getAllGroups().size());
    }

    @Test
    void getAllGroups_ShouldThrowException_WhenPrStatementIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        GroupService groupService = new GroupService(dataSource, logConfigurator);

        assertThrows(DAOException.class, groupService::getAllGroups);
    }

    @Test
    void getAllGroups_ShouldThrowException_WhenRsSetIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);
        PreparedStatement mockedPrStatement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenReturn(mockedPrStatement);
        when(mockedPrStatement.executeQuery()).thenThrow(new SQLException());

        GroupService groupService = new GroupService(dataSource, logConfigurator);

        assertThrows(DAOException.class, groupService::getAllGroups);
    }

    @Test
    void getGroupById_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();
        GroupService groupService = new GroupService(dataSource, mockedLogConfigurator);

        Group group = new Group();
        group.setGroupId(1);
        group.setGroupName("group");

        groupService.add(group);

        try {
            groupService.getGroupById(1);
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void getGroupById_ShouldReturnListOfAllGroups_WhenInvoked() {
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();
        group.setGroupId(1);
        group.setGroupName("group");

        groupService.add(group);

        assertEquals(group, groupService.getGroupById(1));
    }

    @Test
    void getGroupById_ShouldThrowException_WhenPrStatementIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        GroupService groupService = new GroupService(dataSource, logConfigurator);

        assertThrows(DAOException.class, () -> groupService.getGroupById(1));
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

        assertThrows(DAOException.class, () -> groupService.getGroupById(1));
    }


    @Test
    void getMaxGroupId_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();
        GroupService groupService = new GroupService(dataSource, mockedLogConfigurator);

        try {
            groupService.getMaxGroupId();
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void getMaxGroupId_ShouldReturnMaxGroupId_WhenInvoked() {
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();
        group.setGroupId(1);
        group.setGroupName("group");

        Group group_5 = new Group();
        group.setGroupId(5);
        group.setGroupName("group_5");

        groupService.add(group);
        groupService.add(group_5);

        assertEquals(5, groupService.getMaxGroupId());
    }

    @Test
    void getMaxGroupId_ShouldThrowException_WhenPrStatementIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        GroupService groupService = new GroupService(dataSource, logConfigurator);

        assertThrows(DAOException.class, groupService::getMaxGroupId);
    }

    @Test
    void getMaxGroupId_ShouldThrowException_WhenRsSetIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);
        PreparedStatement mockedPrStatement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenReturn(mockedPrStatement);
        when(mockedPrStatement.executeQuery()).thenThrow(new SQLException());

        GroupService groupService = new GroupService(dataSource, logConfigurator);

        assertThrows(DAOException.class, groupService::getMaxGroupId);
    }

    @Test
    void getStudentsNumberInGroup_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();
        GroupService groupService = new GroupService(dataSource, mockedLogConfigurator);

        try {
            groupService.getStudentsNumberInGroup(1);
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void getStudentsNumberInGroup_ShouldReturnStudentsNumberInGroup_WhenInvoked() {
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();
        group.setGroupId(1);
        group.setGroupName("group");

        groupService.add(group);

        StudentService studentService = new StudentService(dataSource, logConfigurator);
        Student student = new Student();
        student.setStudentId(100);
        student.setFirstName("Max");
        student.setLastName("Messi");
        student.setGroupId(1);

        studentService.add(student);

        Student student_2 = new Student();
        student_2.setStudentId(200);
        student_2.setFirstName("Bob");
        student_2.setLastName("Messi");
        student_2.setGroupId(1);

        studentService.add(student_2);

        assertEquals(2, groupService.getStudentsNumberInGroup(1));
    }

    @Test
    void getStudentsNumberInGroup_ShouldThrowException_WhenPrStatementIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        GroupService groupService = new GroupService(dataSource, logConfigurator);

        assertThrows(DAOException.class, () -> groupService.getStudentsNumberInGroup(1));
    }

    @Test
    void getStudentsNumberInGroup_ShouldThrowException_WhenRsSetIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);
        PreparedStatement mockedPrStatement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenReturn(mockedPrStatement);
        when(mockedPrStatement.executeQuery()).thenThrow(new SQLException());

        GroupService groupService = new GroupService(dataSource, logConfigurator);

        assertThrows(DAOException.class, () -> groupService.getStudentsNumberInGroup(1));
    }

    @Test
    void getStudentsNumberInSmallGroups_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();
        GroupService groupService = new GroupService(dataSource, mockedLogConfigurator);

        try {
            groupService.getStudentsNumberInSmallGroups();
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void getStudentsNumberInSmallGroups_ShouldReturnStudentsNumberInLessThen10Groups_WhenInvoked() {
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();
        group.setGroupId(1);
        group.setGroupName("group");

        groupService.add(group);

        StudentService studentService = new StudentService(dataSource, logConfigurator);
        Student student = new Student();
        student.setStudentId(100);
        student.setFirstName("Max");
        student.setLastName("Messi");
        student.setGroupId(1);

        studentService.add(student);

        Student student_2 = new Student();
        student_2.setStudentId(200);
        student_2.setFirstName("Bob");
        student_2.setLastName("Messi");
        student_2.setGroupId(1);

        studentService.add(student_2);

        assertEquals(2, groupService.getStudentsNumberInSmallGroups());
    }

    @Test
    void getStudentsNumberInSmallGroups_ShouldReturnZeroIfNoSmallGroups_WhenInvoked() {
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();
        group.setGroupId(1);
        group.setGroupName("group");

        groupService.add(group);

        StudentService studentService = new StudentService(dataSource, logConfigurator);

        for (int i = 0; i <= 15; i++) {
            Student student = new Student();

            student.setStudentId(i + 100);
            student.setFirstName("Max");
            student.setLastName("Messi");
            student.setGroupId(1);

            studentService.add(student);
        }

        assertEquals(0, groupService.getStudentsNumberInSmallGroups());
    }

    @Test
    void getStudentsNumberInSmallGroups_ShouldThrowException_WhenPrStatementIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        GroupService groupService = new GroupService(dataSource, logConfigurator);

        assertThrows(DAOException.class, groupService::getStudentsNumberInSmallGroups);
    }

    @Test
    void getStudentsNumberInSmallGroups_ShouldThrowException_WhenRsSetIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);
        PreparedStatement mockedPrStatement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenReturn(mockedPrStatement);
        when(mockedPrStatement.executeQuery()).thenThrow(new SQLException());

        GroupService groupService = new GroupService(dataSource, logConfigurator);

        assertThrows(DAOException.class, groupService::getStudentsNumberInSmallGroups);
    }

//------------------

    @Test
    void getSmallGroupsNumber_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();
        GroupService groupService = new GroupService(dataSource, mockedLogConfigurator);

        try {
            groupService.getSmallGroupsNumber();
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void getSmallGroupsNumber_ShouldReturnGroupsNumberWithLessThen10Students_WhenInvoked() {
        GroupService groupService = new GroupService(dataSource, logConfigurator);

        for (int i = 1; i <= 5; i++) {
            Group group = new Group();
            group.setGroupId(i);
            group.setGroupName("group" + i);

            groupService.add(group);
        }

        StudentService studentService = new StudentService(dataSource, logConfigurator);

        for (int i = 1; i <= 5; i++) {
            Student student = new Student();

            student.setStudentId(i + 100);
            student.setFirstName("Max");
            student.setLastName("Messi");
            student.setGroupId(i);

            studentService.add(student);
        }

        assertEquals(5, groupService.getSmallGroupsNumber());
    }

    @Test
    void getSmallGroupsNumber_ShouldReturnZeroIfNoSmallGroups_WhenInvoked() {
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();
        group.setGroupId(1);
        group.setGroupName("group");

        groupService.add(group);

        StudentService studentService = new StudentService(dataSource, logConfigurator);

        for (int i = 0; i <= 15; i++) {
            Student student = new Student();

            student.setStudentId(i + 100);
            student.setFirstName("Max");
            student.setLastName("Messi");
            student.setGroupId(1);

            studentService.add(student);
        }

        assertEquals(0, groupService.getSmallGroupsNumber());
    }

    @Test
    void getSmallGroupsNumber_ShouldThrowException_WhenPrStatementIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        GroupService groupService = new GroupService(dataSource, logConfigurator);

        assertThrows(DAOException.class, groupService::getSmallGroupsNumber);
    }

    @Test
    void getSmallGroupsNumber_ShouldThrowException_WhenRsSetIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);
        PreparedStatement mockedPrStatement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenReturn(mockedPrStatement);
        when(mockedPrStatement.executeQuery()).thenThrow(new SQLException());

        GroupService groupService = new GroupService(dataSource, logConfigurator);

        assertThrows(DAOException.class, groupService::getSmallGroupsNumber);
    }

    //------------------------

    @Test
    void getSmallGroupsId_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();
        GroupService groupService = new GroupService(dataSource, mockedLogConfigurator);

        try {
            groupService.getSmallGroupsId();
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void getSmallGroupsId_ShouldReturnListOfGroupsIdWithLessThen10Students_WhenInvoked() {
        GroupService groupService = new GroupService(dataSource, logConfigurator);

        for (int i = 1; i <= 5; i++) {
            Group group = new Group();
            group.setGroupId(i);
            group.setGroupName("group" + i);

            groupService.add(group);
        }

        StudentService studentService = new StudentService(dataSource, logConfigurator);

        for (int i = 1; i <= 5; i++) {
            Student student = new Student();

            student.setStudentId(i + 100);
            student.setFirstName("Max");
            student.setLastName("Messi");
            student.setGroupId(i);

            studentService.add(student);
        }

        List<Integer> groupsId = Arrays.asList(1, 2, 3, 4, 5);

        assertEquals(groupsId, groupService.getSmallGroupsId());
    }

    @Test
    void getSmallGroupsId_ShouldReturnEmptyListIfNoSmallGroups_WhenInvoked() {
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();
        group.setGroupId(1);
        group.setGroupName("group");

        groupService.add(group);

        StudentService studentService = new StudentService(dataSource, logConfigurator);

        for (int i = 0; i <= 15; i++) {
            Student student = new Student();

            student.setStudentId(i + 100);
            student.setFirstName("Max");
            student.setLastName("Messi");
            student.setGroupId(1);

            studentService.add(student);
        }

        List<Integer> groupsId = new ArrayList<>();

        assertEquals(groupsId, groupService.getSmallGroupsId());
    }

    @Test
    void getSmallGroupsId_ShouldThrowException_WhenPrStatementIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        GroupService groupService = new GroupService(dataSource, logConfigurator);

        assertThrows(DAOException.class, groupService::getSmallGroupsId);
    }

    @Test
    void getSmallGroupsId_ShouldThrowException_WhenRsSetIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);
        PreparedStatement mockedPrStatement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenReturn(mockedPrStatement);
        when(mockedPrStatement.executeQuery()).thenThrow(new SQLException());

        GroupService groupService = new GroupService(dataSource, logConfigurator);

        assertThrows(DAOException.class, groupService::getSmallGroupsId);
    }

    //-------------------

    @Test
    void update_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();
        GroupService groupService = new GroupService(dataSource, mockedLogConfigurator);

        Group group = new Group();

        try {
            groupService.update(group);
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void update_ShouldReturnTrue_WhenInvoked() {
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();
        group.setGroupId(1);
        group.setGroupName("initial group");
        groupService.add(group);

        Group updatedGroup = new Group();
        updatedGroup.setGroupId(1);
        updatedGroup.setGroupName("updated group");
        groupService.update(updatedGroup);

        assertEquals(updatedGroup, groupService.getGroupById(1));
    }

    @Test
    void update_ShouldThrowException_WhenPrStatementIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();

        assertFalse(groupService.update(group));
    }

    //-------------------

    @Test
    void remove_ShouldThrowIOException_WhenInvokeBrokenLogger() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        Logger mockedLogger = mock(Logger.class);
        doThrow(new IOException()).when(mockedLogConfigurator).setup();
        GroupService groupService = new GroupService(dataSource, mockedLogConfigurator);

        Group group = new Group();

        try {
            groupService.remove(group);
        } catch (Exception e) {
            verify(mockedLogger, times(1)).log(Level.INFO, anyString());
        }
    }

    @Test
    void remove_ShouldReturnTrue_WhenInvoked() {
        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();
        group.setGroupId(1);
        group.setGroupName("initial group");
        groupService.add(group);

        groupService.remove(group);

        assertEquals(0, groupService.getAllGroups().size());
    }

    @Test
    void remove_ShouldThrowException_WhenPrStatementIsIncorrect() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection mockedConnection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(mockedConnection);
        when(mockedConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        GroupService groupService = new GroupService(dataSource, logConfigurator);
        Group group = new Group();

        assertFalse(groupService.remove(group));
    }
}
