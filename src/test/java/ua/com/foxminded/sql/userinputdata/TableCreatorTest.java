package ua.com.foxminded.sql.userinputdata;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TableCreatorTest {

    private static final String LINE_SEPARATOR = System.lineSeparator();

    TableCreator tableCreator = new TableCreator();

    @Test
    void createTable_ShouldCreateTable_WhenInvoked() {
        Map<String, List<?>> data = new LinkedHashMap<>();
        List<Integer> studentId = Arrays.asList(10164, 10152);
        List<String> studentFirstName = Arrays.asList("Leon", "Jack");
        List<String> studentLastName = Arrays.asList("Giggs", "York");
        List<String> courseName = Arrays.asList("Music", "Music");
        data.put("student_id", studentId);
        data.put("student_first_name", studentFirstName);
        data.put("student_last_name", studentLastName);
        data.put("course_name", courseName);


        assertEquals("+------------+--------------------+-------------------+-------------+" + LINE_SEPARATOR +
                "| student_id | student_first_name | student_last_name | course_name |" + LINE_SEPARATOR +
                "+------------+--------------------+-------------------+-------------+" + LINE_SEPARATOR +
                "| 10164      | Leon               | Giggs             | Music       |" + LINE_SEPARATOR +
                "| 10152      | Jack               | York              | Music       |" + LINE_SEPARATOR +
                "+------------+--------------------+-------------------+-------------+" + LINE_SEPARATOR, tableCreator.createTable(data)
        );

    }
}
