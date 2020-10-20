package ua.com.foxminded.sql.exception;

public class DAOException extends RuntimeException {

    public DAOException(String errMessage) {
        super(errMessage);
    }

}
