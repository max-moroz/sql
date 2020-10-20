package ua.com.foxminded.sql.context;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Context {

    private static final Logger LOGGER = Logger.getLogger(Context.class.getName());

    public <T> T getObject(Class<T> clazz) {
        T bean = null;

        try {
            bean = clazz.getConstructor().newInstance();
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e, () -> "Exception occurred during Context usage whilst created " + clazz.getName());
        }
        return bean;
    }
}
