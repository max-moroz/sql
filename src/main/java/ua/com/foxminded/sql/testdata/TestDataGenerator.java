package ua.com.foxminded.sql.testdata;

import ua.com.foxminded.sql.entity.Course;
import ua.com.foxminded.sql.entity.Group;
import ua.com.foxminded.sql.entity.Student;
import ua.com.foxminded.sql.service.CourseService;
import ua.com.foxminded.sql.service.GroupService;
import ua.com.foxminded.sql.service.StudentCourseService;
import ua.com.foxminded.sql.service.StudentService;
import ua.com.foxminded.sql.tools.LogConfigurator;

import javax.sql.DataSource;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


public class TestDataGenerator {

    private final Random random = SecureRandom.getInstanceStrong();

    private final DataSource dataSource;
    private final LogConfigurator logConfigurator;

    public TestDataGenerator(DataSource dataSource, LogConfigurator logConfigurator) throws NoSuchAlgorithmException {
        this.dataSource = dataSource;
        this.logConfigurator = logConfigurator;
    }


    private String generateGroupName() {
        return generateString().concat("-").concat(Integer.toString(generateNumber()));
    }

    private int generateNumber() {
        return ThreadLocalRandom.current().nextInt(10, 100);
    }

    private String generateString() {
        int firstSymbol = 65; // A
        int lastSymbol = 90;  // Z
        int length = 2;

        return this.random.ints(firstSymbol, lastSymbol + 1).limit(length).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }

    public void create10Groups() {
        GroupService group = new GroupService(dataSource, logConfigurator);
        for (int i = 0; i < 10; i++) {
            group.add(generateGroupName());
        }
    }

    public void create10Courses() {
        List<String> courseName = Arrays.asList("Math", "Biology", "Philosophy", "Physics", "Economy", "Literature", "Finance", "Society", "Science", "Music");
        CourseService course = new CourseService(dataSource, logConfigurator);

        for (String c : courseName) {
            course.add(c);
        }
    }

    public void create200Students() {
        List<String> name = Arrays.asList("Bob", "Jack", "Saul", "Tony", "Alex", "Max", "Darren", "Vasyl", "Volodymyr", "Peter",
                "Mike", "Josh", "Leon", "Jo", "Steve", "Rob", "Connor", "Donald", "Chris", "Jason");
        List<String> surname = Arrays.asList("Cole", "Trump", "Duck", "Totti", "York", "Giggs", "Forlan", "Messi", "Scoles", "Zidane",
                "Conte", "Ronaldo", "Dibala", "Popov", "Romanchuk", "McGregor", "Rivaldo", "Terry", "Wise", "Bobo");

        StudentService student = new StudentService(dataSource, logConfigurator);

        for (int i = 0; i < 200; i++) {
            int randomNameIndex = ThreadLocalRandom.current().nextInt(1, 20);
            int randomSurnameIndex = ThreadLocalRandom.current().nextInt(1, 20);

            student.add(name.get(randomNameIndex), surname.get(randomSurnameIndex));
        }
    }

    public void assignStudentsToGroups() {
        StudentService students = new StudentService(dataSource, logConfigurator);
        List<Student> student = students.getAllStudents();
        student.add(null);

        GroupService groups = new GroupService(dataSource, logConfigurator);
        List<Group> group = groups.getAllGroups();
        group.add(null);

        groups.getStudentsNumberInSmallGroups();
        int groupId = 0;

        for (int i = student.size() - 1; i >= 0; i--) {
            groupId = ThreadLocalRandom.current().nextInt(1, groups.getMaxGroupId() + 1);

            int lackFactor = groups.getSmallGroupsNumber() * 10 - groups.getStudentsNumberInSmallGroups() + 10;

            if (lackFactor - i <= 10 && lackFactor - i > 0) { // distribute students among existing groups '<10' and others
                if (!groups.getSmallGroupsId().isEmpty()) {
                    groupId = groups.getSmallGroupsId().get(0);
                }
            } else if (groups.getStudentsNumberInGroup(groupId) == 30) {
                continue;
            }

            if (student.get(i) != null) {
                student.get(i).setGroupId(groupId);
                students.updateGroupId(student.get(i));
            }
        }
    }

    public void createStudentCourseTable() {
        StudentCourseService studentCourse = new StudentCourseService(dataSource, logConfigurator);
        studentCourse.createTable();

        StudentService students = new StudentService(dataSource, logConfigurator);
        List<Student> studentsList = students.getAllStudents();

        CourseService courses = new CourseService(dataSource, logConfigurator);
        List<Course> coursesList = courses.getAllCourses();

        for (Student s : studentsList) {
            int numberOfCourses = ThreadLocalRandom.current().nextInt(1, 4);
            List<Integer> assignedCourses = new ArrayList<>();

            for (int i = 0; i < numberOfCourses; i++) {
                int courseNumber = ThreadLocalRandom.current().nextInt(1, coursesList.size() + 1);
                assignedCourses.add(courseNumber);
            }

            List<Integer> studentCourses = assignedCourses.stream().distinct().collect(Collectors.toList());

            for (int c : studentCourses) {
                studentCourse.add(s, coursesList.get(c - 1));
            }
        }
    }
}


