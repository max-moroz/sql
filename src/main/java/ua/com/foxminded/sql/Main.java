package ua.com.foxminded.sql;

import ua.com.foxminded.sql.launcher.ScriptsLauncher;
import ua.com.foxminded.sql.tools.DBAccess;
import ua.com.foxminded.sql.tools.LogConfigurator;
import ua.com.foxminded.sql.tools.ScriptReader;
import ua.com.foxminded.sql.context.Context;
import ua.com.foxminded.sql.scriptrunner.SqlScriptRunner;
import ua.com.foxminded.sql.testdata.TestDataGenerator;
import ua.com.foxminded.sql.userinputdata.TableCreator;
import ua.com.foxminded.sql.userinputdata.UserQuery;

import javax.sql.DataSource;
import java.security.NoSuchAlgorithmException;

public class Main {

    private static final Context context = new Context();
    private static final DataSource dataSource = DBAccess.getDataSource();
    private static final LogConfigurator logConfigurator = context.getObject(LogConfigurator.class);
    private static final ScriptReader scriptReader = context.getObject(ScriptReader.class);
    private static SqlScriptRunner sqlScriptRunner = new SqlScriptRunner(scriptReader, logConfigurator);
    private static TestDataGenerator testDataGenerator;

    static {
        try {
            testDataGenerator = new TestDataGenerator(dataSource, logConfigurator);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static final TableCreator tableCreator = context.getObject(TableCreator.class);
    private static UserQuery userQuery = new UserQuery(tableCreator, scriptReader, dataSource, logConfigurator);
    private static ScriptsLauncher launcher = new ScriptsLauncher(sqlScriptRunner, testDataGenerator, userQuery, logConfigurator);

    public static void main(String[] args) {
        System.out.println("Please wait while application is creating tables and filling data in... ");

        launcher.runSqlScripts();
        launcher.inputQuery();
    }


    static void setSqlScriptRunner(SqlScriptRunner newSqlScriptRunner) {
        sqlScriptRunner = newSqlScriptRunner;
    }

    static void setTestDataGenerator(TestDataGenerator newTestDataGenerator) {
        testDataGenerator = newTestDataGenerator;
    }

    static void setUserQuery(UserQuery newUserQuery) {
        userQuery = newUserQuery;
    }

    static void setScriptsLauncher(ScriptsLauncher newlauncher) {
        launcher = newlauncher;
    }
}
