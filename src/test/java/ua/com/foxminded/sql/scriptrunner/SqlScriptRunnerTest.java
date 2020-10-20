package ua.com.foxminded.sql.scriptrunner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ua.com.foxminded.sql.tools.LogConfigurator;
import ua.com.foxminded.sql.tools.ScriptReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class SqlScriptRunnerTest {

    @InjectMocks
    SqlScriptRunner scriptRunner;

    @Mock
    ScriptReader scriptReader;

    @Mock
    LogConfigurator logConfigurator;


    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    private Stream<String> getFile(String filePath) {
        InputStream inputStream = this.getClass().getResourceAsStream(filePath);
        InputStreamReader reader = new InputStreamReader(inputStream);
        return new BufferedReader(reader).lines();
    }

    private String getQuery(String filePath) {
        Stream<String> stream = getFile(filePath);
        return stream.collect(Collectors.joining(" "));
    }

    @Test
    void runTablesCreation_ShouldFailToExecuteUpdate_WhenExceptionThrown() {
        when(scriptReader.getQuery(anyString())).thenReturn("123");
        assertFalse(scriptRunner.runTablesCreation());
    }

    @Test
    void runTablesCreation_ShouldCreateTables_WhenInvoked() {
        when(scriptReader.getQuery(anyString())).thenReturn(getQuery("/scripts/tables.sql"));
        assertTrue(scriptRunner.runTablesCreation());
    }

    @Test
    void prepareTables_ShouldFailToExecuteUpdate_WhenExceptionThrown() {
        when(scriptReader.getQuery(anyString())).thenReturn("123");
        assertFalse(scriptRunner.prepareTables());
    }

    @Test
    void prepareTables_ShouldPrepareTables_WhenInvoked() {
        when(scriptReader.getQuery(anyString())).thenReturn(getQuery("/scripts/tables.sql")).thenReturn(getQuery("/scripts/data.sql"));

        scriptRunner.runTablesCreation();
        assertTrue(scriptRunner.prepareTables());
    }

}