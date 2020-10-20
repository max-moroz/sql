package ua.com.foxminded.sql.scriptrunner;

import ua.com.foxminded.sql.tools.LogConfigurator;
import ua.com.foxminded.sql.tools.ScriptReader;
import ua.com.foxminded.sql.tools.DBAccess;
import ua.com.foxminded.sql.service.StudentService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SqlScriptRunner {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String LOG_START = "occurred during ";
    private static final String CLASS_NAME = StudentService.class.getName();

    private final ScriptReader scriptReader;
    private final LogConfigurator logConfigurator;

    public SqlScriptRunner(ScriptReader scriptReader, LogConfigurator logConfigurator) {
        this.scriptReader = scriptReader;
        this.logConfigurator = logConfigurator;
    }

    public boolean runTablesCreation() {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = scriptReader.getQuery("/scripts/tables.sql");

        try (Connection connection = DBAccess.getDataSource().getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.info("runTablesCreation() - connection established");

            prStatement.executeUpdate();

            LOGGER.info("runTablesCreation() - tables have been successfully created");
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> "occurred during runTablesCreation() method invocation of SqlScriptRunner class");
            return false;
        }
    }

    public boolean prepareTables() {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = scriptReader.getQuery("/scripts/data.sql");

        try (Connection connection = DBAccess.getDataSource().getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.info("prepareTables() - connection established");

            prStatement.executeUpdate();

            LOGGER.info("prepareTables() - tables have been successfully prepared");
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> "occurred during fillTablesWithInitialData() method invocation of SqlScriptRunner class");
            return false;
        }
    }
}
