package ua.com.foxminded.sql.tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScriptReader {

    private Stream<String> getFile(String filePath) {
        InputStream inputStream = this.getClass().getResourceAsStream(filePath);
        InputStreamReader reader = new InputStreamReader(inputStream);
        return new BufferedReader(reader).lines();
    }

    public String getQuery(String filePath) {
        Stream<String> stream = getFile(filePath);
        return stream.collect(Collectors.joining(" "));
    }
}
