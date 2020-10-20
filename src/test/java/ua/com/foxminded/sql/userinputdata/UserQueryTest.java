package ua.com.foxminded.sql.userinputdata;

import org.junit.jupiter.api.*;
import ua.com.foxminded.sql.context.Context;

import ua.com.foxminded.sql.entity.Course;
import ua.com.foxminded.sql.entity.Student;
import ua.com.foxminded.sql.service.CourseService;
import ua.com.foxminded.sql.service.GroupService;
import ua.com.foxminded.sql.service.StudentCourseService;
import ua.com.foxminded.sql.service.StudentService;

import ua.com.foxminded.sql.tools.DBAccess;
import ua.com.foxminded.sql.tools.LogConfigurator;
import ua.com.foxminded.sql.tools.ScriptReader;

import javax.sql.DataSource;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class UserQueryTest {

    private static final Logger LOGGER = Logger.getLogger(UserQueryTest.class.getName());
    private static final String LINE_SEPARATOR = System.lineSeparator();

    DataSource dataSource = DBAccess.getDataSource();

    Context context = new Context();
    ScriptReader scriptReader = context.getObject(ScriptReader.class);
    LogConfigurator logConfigurator = context.getObject(LogConfigurator.class);
    TableCreator tableCreator = context.getObject(TableCreator.class);

    UserQuery userQuery = new UserQuery(tableCreator, scriptReader, dataSource, logConfigurator);


    @BeforeEach
    void init() {
        String sqlQuery = scriptReader.getQuery("/scripts/tables.sql");

        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> "Cannot create schema");
        }
    }


    void create10Groups() {
        GroupService group = new GroupService(dataSource, logConfigurator);
        for (int i = 1; i <= 2; i++) {
            group.add("group_" + i);
        }
    }

    void create40Students() {
        List<String> name = Arrays.asList("Bob", "Jack", "Saul", "Tony", "Alex", "Max", "Darren", "Vasyl", "Volodymyr", "Peter",
                "Mike", "Josh", "Leon", "Jo", "Steve", "Rob", "Connor", "Donald", "Chris", "Jason");
        List<String> surname = Arrays.asList("Cole", "Trump", "Duck", "Totti", "York", "Giggs", "Forlan", "Messi", "Scoles", "Zidane",
                "Conte", "Ronaldo", "Dibala", "Popov", "Romanchuk", "McGregor", "Rivaldo", "Terry", "Wise", "Bobo");

        StudentService student = new StudentService(dataSource, logConfigurator);

        for (int i = 0; i < 40; i++) {
            int randomNameIndex = ThreadLocalRandom.current().nextInt(1, 20);
            int randomSurnameIndex = ThreadLocalRandom.current().nextInt(1, 20);

            student.add(name.get(randomNameIndex), surname.get(randomSurnameIndex));
        }
    }

    public void assignStudentsToGroups() {
        StudentService students = new StudentService(dataSource, logConfigurator);
        List<Student> student = students.getAllStudents();

        for (int i = student.size() - 1; i >= 0; i--) {

            if (i >= 30) {                              // assigns 10 students to 1st group
                student.get(i).setGroupId(1);
                students.updateGroupId(student.get(i));
            } else {                                    // assigns 30 students to 2nd group
                student.get(i).setGroupId(2);
                students.updateGroupId(student.get(i));
            }
        }
    }


    @Test
    void findGroupsWithLessOrEqualsStudents_ShouldSetUpLogger_WhenInvoked() throws IOException {
        create10Groups();
        create40Students();
        assignStudentsToGroups();
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        UserQuery userQuery = new UserQuery(tableCreator, scriptReader, dataSource, mockedLogConfigurator);
        userQuery.findGroupsWithLessOrEqualsStudents(30);
        verify(mockedLogConfigurator, times(1)).setup();
    }

    @Test
    void findGroupsWithLessOrEqualsStudents_ShouldReturnTableWithResult_WhenInvoked() {
        create10Groups();
        create40Students();
        assignStudentsToGroups();
        assertEquals("+------------+-------+" + LINE_SEPARATOR +
                "| group_name | count |" + LINE_SEPARATOR +
                "+------------+-------+" + LINE_SEPARATOR +
                "| group_1    | 10    |" + LINE_SEPARATOR +
                "| group_2    | 30    |" + LINE_SEPARATOR +
                "+------------+-------+" + LINE_SEPARATOR, userQuery.findGroupsWithLessOrEqualsStudents(30)
        );
    }

    @Test
    void findAllStudentsOfTheCourse_ShouldSetUpLogger_WhenInvoked() throws IOException {
        StudentCourseService studentCourse = new StudentCourseService(dataSource, logConfigurator);
        studentCourse.createTable();

        Course course = new Course();
        CourseService courseService = new CourseService(dataSource, logConfigurator);
        course.setCourseId(1);
        course.setCourseDescription("course");
        course.setCourseName("course");

        courseService.add(course);

        StudentService studentService = new StudentService(dataSource, logConfigurator);
        for (int i = 1; i <= 2; i++) {
            Student student = new Student();
            student.setStudentId(i);
            student.setFirstName("name");
            student.setLastName("surname");

            studentService.add(student);
            studentCourse.add(student, course);
        }
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        UserQuery userQuery = new UserQuery(tableCreator, scriptReader, dataSource, mockedLogConfigurator);
        userQuery.findAllStudentsOfTheCourse("course");
        verify(mockedLogConfigurator, times(1)).setup();
    }

    @Test
    void findAllStudentsOfTheCourse_ShouldReturnTableWithResult_WhenInvoked() {
        StudentCourseService studentCourse = new StudentCourseService(dataSource, logConfigurator);
        studentCourse.createTable();

        Course course = new Course();
        CourseService courseService = new CourseService(dataSource, logConfigurator);
        course.setCourseId(1);
        course.setCourseDescription("course");
        course.setCourseName("course");

        courseService.add(course);

        StudentService studentService = new StudentService(dataSource, logConfigurator);
        for (int i = 1; i <= 2; i++) {
            Student student = new Student();
            student.setStudentId(i);
            student.setFirstName("name");
            student.setLastName("surname");

            studentService.add(student);
            studentCourse.add(student, course);
        }
        assertEquals("+------------+--------------------+-------------------+-------------+" + LINE_SEPARATOR +
                "| student_id | student_first_name | student_last_name | course_name |" + LINE_SEPARATOR +
                "+------------+--------------------+-------------------+-------------+" + LINE_SEPARATOR +
                "| 1          | name               | surname           | course      |" + LINE_SEPARATOR +
                "| 2          | name               | surname           | course      |" + LINE_SEPARATOR +
                "+------------+--------------------+-------------------+-------------+" + LINE_SEPARATOR, userQuery.findAllStudentsOfTheCourse("course")
        );
    }

    @Test
    void addNewStudent_ShouldSetUpLogger_WhenInvoked() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        UserQuery userQuery = new UserQuery(tableCreator, scriptReader, dataSource, mockedLogConfigurator);
        userQuery.addNewStudent("firstName", "lastName");
        verify(mockedLogConfigurator, times(1)).setup();
    }

    @Test
    void addNewStudent_ShouldAddNewStudent_WhenInvoked() {
        userQuery.addNewStudent("firstName", "lastName");
        StudentService studentService = new StudentService(dataSource, logConfigurator);

        Student student = new Student();
        student.setFirstName("firstName");
        student.setLastName("lastName");
        student.setStudentId(1);
        assertEquals(student, studentService.getStudentById(1));
    }

    @Test
    void deleteStudentById_ShouldSetUpLogger_WhenInvoked() throws IOException {
        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        UserQuery userQuery = new UserQuery(tableCreator, scriptReader, dataSource, mockedLogConfigurator);
        userQuery.addNewStudent("firstName", "lastName");
        userQuery.deleteStudentById(1);
        verify(mockedLogConfigurator, atLeast(2)).setup();
    }

    @Test
    void deleteStudentById_ShouldAddNewStudent_WhenInvoked() {
        userQuery.addNewStudent("firstName", "lastName");
        StudentService studentService = new StudentService(dataSource, logConfigurator);
        Student student = studentService.getStudentById(1);

        userQuery.deleteStudentById(1);
        assertNotEquals(student, studentService.getStudentById(1));
    }

    @Test
    void addStudentToCourse_ShouldSetUpLogger_WhenInvoked() throws IOException {
        StudentCourseService studentCourse = new StudentCourseService(dataSource, logConfigurator);
        studentCourse.createTable();

        Course course = new Course();
        CourseService courseService = new CourseService(dataSource, logConfigurator);
        course.setCourseId(1);
        course.setCourseDescription("course");
        course.setCourseName("course");

        courseService.add(course);

        StudentService studentService = new StudentService(dataSource, logConfigurator);

        Student student = new Student();
        student.setStudentId(1);
        student.setFirstName("name");
        student.setLastName("surname");

        studentService.add(student);

        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        UserQuery userQuery = new UserQuery(tableCreator, scriptReader, dataSource, mockedLogConfigurator);
        userQuery.addStudentToCourse(1, 1);
        verify(mockedLogConfigurator, atLeastOnce()).setup();
    }

    @Test
    void addStudentToCourse_ShouldAddStudentToCourse_WhenInvoked() {
        StudentCourseService studentCourse = new StudentCourseService(dataSource, logConfigurator);
        studentCourse.createTable();

        Course course = new Course();
        CourseService courseService = new CourseService(dataSource, logConfigurator);
        course.setCourseId(1);
        course.setCourseDescription("course");
        course.setCourseName("course");

        courseService.add(course);

        StudentService studentService = new StudentService(dataSource, logConfigurator);

        Student student = new Student();
        student.setStudentId(1);
        student.setFirstName("name");
        student.setLastName("surname");

        studentService.add(student);

        userQuery.addStudentToCourse(1, 1);
        assertNotNull(studentCourse.getAllStudentCourses(student));
    }

    @Test
    void addStudentToCourse_ShouldNotAddStudentToCourse_WhenTheStudentAlreadyAssignedToTheCourse() {
        StudentCourseService studentCourse = new StudentCourseService(dataSource, logConfigurator);
        studentCourse.createTable();

        Course course = new Course();
        CourseService courseService = new CourseService(dataSource, logConfigurator);
        course.setCourseId(1);
        course.setCourseDescription("course");
        course.setCourseName("course");

        courseService.add(course);

        StudentService studentService = new StudentService(dataSource, logConfigurator);

        Student student = new Student();
        student.setStudentId(1);
        student.setFirstName("name");
        student.setLastName("surname");

        studentService.add(student);

        userQuery.addStudentToCourse(1, 1);
        userQuery.addStudentToCourse(1, 1);

        assertEquals(1, studentCourse.getAllStudentCourses(student).size());
    }

    @Test
    void getAllCourses_ShouldReturnListOfCourses_WhenInvoked() {
        CourseService courseService = new CourseService(dataSource, logConfigurator);

        Course course = new Course();
        course.setCourseId(1);
        course.setCourseDescription("course");
        course.setCourseName("course");
        courseService.add(course);

        Course course2 = new Course();
        course2.setCourseId(2);
        course2.setCourseDescription("course2");
        course2.setCourseName("course2");
        courseService.add(course2);

        assertEquals(2, userQuery.getAllCourses().size());
    }

    @Test
    void deleteStudentFromCourse_ShouldSetUpLogger_WhenInvoked() throws IOException {
        StudentCourseService studentCourse = new StudentCourseService(dataSource, logConfigurator);
        studentCourse.createTable();

        Course course = new Course();
        CourseService courseService = new CourseService(dataSource, logConfigurator);
        course.setCourseId(1);
        course.setCourseDescription("course");
        course.setCourseName("course");

        courseService.add(course);

        StudentService studentService = new StudentService(dataSource, logConfigurator);

        Student student = new Student();
        student.setStudentId(1);
        student.setFirstName("name");
        student.setLastName("surname");

        studentService.add(student);

        LogConfigurator mockedLogConfigurator = mock(LogConfigurator.class);
        UserQuery userQuery = new UserQuery(tableCreator, scriptReader, dataSource, mockedLogConfigurator);
        userQuery.addStudentToCourse(1, 1);
        userQuery.deleteStudentFromCourse(1, 1);

        verify(mockedLogConfigurator, atLeastOnce()).setup();
    }

    @Test
    void deleteStudentFromCourse_ShouldDeleteStudentFromCourse_WhenInvoked() {
        StudentCourseService studentCourse = new StudentCourseService(dataSource, logConfigurator);
        studentCourse.createTable();

        Course course = new Course();
        CourseService courseService = new CourseService(dataSource, logConfigurator);
        course.setCourseId(1);
        course.setCourseDescription("course");
        course.setCourseName("course");

        courseService.add(course);

        StudentService studentService = new StudentService(dataSource, logConfigurator);

        Student student = new Student();
        student.setStudentId(1);
        student.setFirstName("name");
        student.setLastName("surname");

        studentService.add(student);

        userQuery.addStudentToCourse(1, 1);
        userQuery.deleteStudentFromCourse(1, 1);
        List<Course> courses = new ArrayList<>();

        assertEquals(courses, studentCourse.getAllStudentCourses(student));
    }
}
