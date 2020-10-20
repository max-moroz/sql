package ua.com.foxminded.sql.exception;

public class NoSuchCourseException extends RuntimeException {

    public NoSuchCourseException(String errMessage) {
        super(errMessage);
    }
}
