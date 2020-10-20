package ua.com.foxminded.sql.tools;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.*;

public class DBAccess {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static final BasicDataSource dataSource;
    private static final InputStream inputStream = DBAccess.class.getResourceAsStream("/properties/db.properties");


    static {
        Properties properties = new Properties();

        try {
            properties.load(inputStream);
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> "Not possible to read DB property File");
        }

        dataSource = new BasicDataSource();
        dataSource.setDriverClassName(properties.getProperty("driver.class.name"));
        dataSource.setUrl(properties.getProperty("db.url"));
        dataSource.setUsername(properties.getProperty("db.username"));
        dataSource.setPassword(properties.getProperty("db.password"));

        dataSource.setMinIdle(1);
        dataSource.setMaxIdle(10);
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
