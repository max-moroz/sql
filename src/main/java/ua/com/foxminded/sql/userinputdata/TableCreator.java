package ua.com.foxminded.sql.userinputdata;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TableCreator {

    private static final String PLUS_SYMBOL = "+";
    private static final String DASH = "-";
    private static final String COLUMN_SEPARATOR = "|";
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String WHITESPACE = " ";


    public String createTable(Map<String, List<?>> tableColumnsSet) {
        StringBuilder table = new StringBuilder();
        int length = 5;
        int columnsNumber = tableColumnsSet.size();

        for (int i = 0; i < length; i++) {
            if (i == 0 || i == 2 || i == length - 1) {
                table.append(createRawSeparators(columnsNumber, tableColumnsSet));
            } else if (i == 1) {
                table.append(createHeader(columnsNumber, tableColumnsSet));
            } else {
                table.append(createTableBody(tableColumnsSet));
            }
        }
        return table.toString();
    }

    private String createRawSeparators(int columnsQuantity, Map<String, List<?>> tableColumnsSet) {
        StringBuilder separator = new StringBuilder();
        List<String> columnsName = new LinkedList<>();

        tableColumnsSet.forEach((k, v) -> columnsName.add(k));

        for (int i = 0; i < columnsQuantity; i++) {
            separator.append(PLUS_SYMBOL).append(createSymbolsSequence(columnsName.get(i).length() + 2));
        }
        separator.append(PLUS_SYMBOL).append(LINE_SEPARATOR);

        return separator.toString();
    }

    private String createSymbolsSequence(int quantity) {
        StringBuilder separator = new StringBuilder();

        for (int i = 0; i < quantity; i++) {
            separator.append(DASH);
        }
        return separator.toString();
    }

    private String addWhitespaces(int columnWidth, String name) {
        StringBuilder spaces = new StringBuilder();
        int length = columnWidth - name.length() + 1;

        for (int i = 0; i < length; i++) {
            spaces.append(WHITESPACE);
        }
        return spaces.toString();
    }

    private String getColumnName(Map<String, List<?>> tableColumnsSet, int columnNumber) {
        List<String> columnName = new LinkedList<>();
        tableColumnsSet.forEach((k, v) -> columnName.add(k));
        return columnName.get(columnNumber - 1);
    }

    private String createHeader(int columnsNumber, Map<String, List<?>> tableColumnsSet) {
        StringBuilder header = new StringBuilder();
        List<String> columnsName = new LinkedList<>();

        tableColumnsSet.forEach((k, v) -> columnsName.add(k));

        for (int i = 0; i < columnsNumber; i++) {
            header.append(COLUMN_SEPARATOR).append(WHITESPACE).append(columnsName.get(i)).append(WHITESPACE);
            if (i == columnsNumber - 1) {
                header.append(COLUMN_SEPARATOR).append(LINE_SEPARATOR);
            }
        }
        return header.toString();
    }

    private String createTableBody(Map<String, List<?>> tableColumnsSet) {
        StringBuilder body = new StringBuilder();
        int columnsNumber = tableColumnsSet.size();

        if (columnsNumber == 2) {
            List<String> groupName = (List<String>) tableColumnsSet.get("group_name");
            List<Integer> count = (List<Integer>) tableColumnsSet.get("count");

            for (int i = 0; i < groupName.size(); i++) {
                body.append(COLUMN_SEPARATOR).append(WHITESPACE).append(groupName.get(i)).append(addWhitespaces(getColumnName(tableColumnsSet, 1).length(), groupName.get(i)))
                        .append(COLUMN_SEPARATOR).append(WHITESPACE).append(count.get(i)).append(addWhitespaces(getColumnName(tableColumnsSet, 2).length(), count.get(i).toString()))
                        .append(COLUMN_SEPARATOR).append(LINE_SEPARATOR);

            }
        } else {
            List<Integer> studentId = (List<Integer>) tableColumnsSet.get("student_id");
            List<String> studentFirstName = (List<String>) tableColumnsSet.get("student_first_name");
            List<String> studentLastName = (List<String>) tableColumnsSet.get("student_last_name");
            List<String> courseName = (List<String>) tableColumnsSet.get("course_name");

            for (int i = 0; i < studentId.size(); i++) {
                body.append(COLUMN_SEPARATOR).append(WHITESPACE).append(studentId.get(i)).append(addWhitespaces(getColumnName(tableColumnsSet, 1).length(), studentId.get(i).toString()))
                        .append(COLUMN_SEPARATOR).append(WHITESPACE).append(studentFirstName.get(i)).append(addWhitespaces(getColumnName(tableColumnsSet, 2).length(), studentFirstName.get(i)))
                        .append(COLUMN_SEPARATOR).append(WHITESPACE).append(studentLastName.get(i)).append(addWhitespaces(getColumnName(tableColumnsSet, 3).length(), studentLastName.get(i)))
                        .append(COLUMN_SEPARATOR).append(WHITESPACE).append(courseName.get(i)).append(addWhitespaces(getColumnName(tableColumnsSet, 4).length(), courseName.get(i)))
                        .append(COLUMN_SEPARATOR).append(LINE_SEPARATOR);
            }
        }
        return body.toString();
    }
}
