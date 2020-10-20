package ua.com.foxminded.sql.launcher;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ua.com.foxminded.sql.scriptrunner.SqlScriptRunner;
import ua.com.foxminded.sql.testdata.TestDataGenerator;
import ua.com.foxminded.sql.tools.LogConfigurator;
import ua.com.foxminded.sql.userinputdata.UserQuery;

import java.io.*;

import static org.mockito.Mockito.*;

public class ScriptsLauncherTest {

    @Mock
    SqlScriptRunner scriptRunner;

    @Mock
    TestDataGenerator dataGenerator;

    @Mock
    UserQuery userQuery;

    @Mock
    LogConfigurator logConfigurator;

    @InjectMocks
    ScriptsLauncher launcher;

    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;

    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);

        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    private void provideInput(String data) {
        testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    private String getOutput() {
        return testOut.toString();
    }

    @AfterEach
    void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(systemOut);
    }

    @Test
    void runSqlScripts_ShouldInvokeTwoSqlScriptRunnerMethods_WhenInvoked() {
        launcher.runSqlScripts();

        verify(scriptRunner, times(1)).runTablesCreation();
        verify(scriptRunner, times(1)).prepareTables();
    }

    @Test
    void runSqlScripts_ShouldInvokeFiveTestDataGeneratorMethods_WhenInvoked() {
        launcher.runSqlScripts();

        verify(dataGenerator, times(1)).assignStudentsToGroups();
        verify(dataGenerator, times(1)).create10Courses();
        verify(dataGenerator, times(1)).create10Groups();
        verify(dataGenerator, times(1)).create200Students();
        verify(dataGenerator, times(1)).createStudentCourseTable();
    }

    //-------------------

    @Test
    void inputQuery_ShouldSetUpLogger_WhenInvoked() throws IOException {
        provideInput("a\n" + "25\n" + "n\n");
        launcher.inputQuery();

        verify(logConfigurator, times(1)).setup();
    }


    @Test
    void inputQuery_ShouldInvokeFindGroupsWithLessOrEqualsStudents_WhenInvoked() {
        provideInput("a\n" + "25\n" + "n\n");
        launcher.inputQuery();

        verify(userQuery, times(1)).findGroupsWithLessOrEqualsStudents(anyInt());
    }

    @Test
    void inputQuery_ShouldInvokeFindAllStudentsOfTheCourse_WhenInvoked() {
        provideInput("b\n" + "course\n" + "n\n");
        launcher.inputQuery();

        verify(userQuery, times(1)).findAllStudentsOfTheCourse(anyString());
    }

    @Test
    void inputQuery_ShouldInvokeAddNewStudent_WhenInvoked() {
        provideInput("c\n" + "name\n" + "lastName\n" + "n\n");
        launcher.inputQuery();

        verify(userQuery, times(1)).addNewStudent(anyString(), anyString());
    }

    @Test
    void inputQuery_ShouldInvokeDeleteStudentById_WhenInvoked() {
        provideInput("d\n" + "1000\n" + "n\n");
        launcher.inputQuery();

        verify(userQuery, times(1)).deleteStudentById(anyInt());
    }

    @Test
    void inputQuery_ShouldInvokeAddStudentToCourse_WhenInvoked() {
        provideInput("e\n" + "1000\n" + "1000\n" + "n\n");
        launcher.inputQuery();

        verify(userQuery, times(1)).addStudentToCourse(anyInt(), anyInt());
    }

    @Test
    void inputQuery_ShouldInvokeDeleteStudentFromCourse_WhenInvoked() {
        provideInput("f\n" + "1000\n" + "1000\n" + "n\n");
        launcher.inputQuery();

        verify(userQuery, times(1)).deleteStudentFromCourse(anyInt(), anyInt());
    }

    @Test
    void inputQuery_ShouldInvokeAllOptions_WhenInvoked() {
        provideInput("a\n" + "25\n" + "y\n" +
                "b\n" + "course\n" + "yes\n" +
                "c\n" + "name\n" + "lastName\n" + "Y\n" +
                "d\n" + "1000\n" + "YES\n" +
                "e\n" + "1000\n" + "1000\n" + "Yes\n" +
                "f\n" + "1000\n" + "1000\n" + "n\n"
        );
        launcher.inputQuery();

        verify(userQuery, times(1)).findGroupsWithLessOrEqualsStudents(anyInt());
        verify(userQuery, times(1)).findAllStudentsOfTheCourse(anyString());
        verify(userQuery, times(1)).addNewStudent(anyString(), anyString());
        verify(userQuery, times(1)).deleteStudentById(anyInt());
        verify(userQuery, times(1)).addStudentToCourse(anyInt(), anyInt());
        verify(userQuery, times(1)).deleteStudentFromCourse(anyInt(), anyInt());
    }
}
