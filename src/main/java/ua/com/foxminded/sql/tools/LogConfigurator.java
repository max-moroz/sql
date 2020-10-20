package ua.com.foxminded.sql.tools;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class LogConfigurator {

    private static final String DIRECTORY = "./logs/";
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public void setup() throws IOException {
        createLogsDirectory();

        FileHandler fileHandler = new FileHandler(DIRECTORY + "Logging.log", 100000000, 1, true);

        logger.addHandler(fileHandler);
        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler.setFormatter(formatter);

        logger.setLevel(Level.INFO);

        logger.setUseParentHandlers(false);
    }

    private void createLogsDirectory() {
        File dir = new File(DIRECTORY);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }
}
