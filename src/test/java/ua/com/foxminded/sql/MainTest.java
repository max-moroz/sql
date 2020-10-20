package ua.com.foxminded.sql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ua.com.foxminded.sql.launcher.ScriptsLauncher;
import ua.com.foxminded.sql.scriptrunner.SqlScriptRunner;
import ua.com.foxminded.sql.testdata.TestDataGenerator;
import ua.com.foxminded.sql.userinputdata.UserQuery;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MainTest {

    @Mock
    SqlScriptRunner scriptRunner;

    @Mock
    TestDataGenerator dataGenerator;

    @Mock
    UserQuery userQuery;

    @Mock
    ScriptsLauncher launcher;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void main_ShouldInvokeScriptsLauncher_WhenInvoked() {
        String[] arguments = {};

        Main.setScriptsLauncher(launcher);
        Main.setSqlScriptRunner(scriptRunner);
        Main.setTestDataGenerator(dataGenerator);
        Main.setUserQuery(userQuery);

        Main.main(arguments);

        verify(launcher, times(1)).runSqlScripts();
        verify(launcher, times(1)).inputQuery();
    }
}
