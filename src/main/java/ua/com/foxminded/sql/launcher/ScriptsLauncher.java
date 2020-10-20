package ua.com.foxminded.sql.launcher;

import ua.com.foxminded.sql.entity.Course;
import ua.com.foxminded.sql.scriptrunner.SqlScriptRunner;
import ua.com.foxminded.sql.testdata.TestDataGenerator;
import ua.com.foxminded.sql.tools.LogConfigurator;
import ua.com.foxminded.sql.userinputdata.UserQuery;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScriptsLauncher {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String LOG_START = "occurred during";
    private static final String CLASS_NAME = ScriptsLauncher.class.getName();

    SqlScriptRunner scriptRunner;
    TestDataGenerator dataGenerator;
    UserQuery userQuery;
    LogConfigurator logConfigurator;

    public ScriptsLauncher(SqlScriptRunner scriptRunner, TestDataGenerator dataGenerator, UserQuery userQuery, LogConfigurator logConfigurator) {
        this.scriptRunner = scriptRunner;
        this.dataGenerator = dataGenerator;
        this.userQuery = userQuery;
        this.logConfigurator = logConfigurator;
    }

    public void runSqlScripts() {
        scriptRunner.runTablesCreation();
        scriptRunner.prepareTables();

        dataGenerator.create10Groups();
        dataGenerator.create10Courses();
        dataGenerator.create200Students();
        dataGenerator.assignStudentsToGroups();
        dataGenerator.createStudentCourseTable();
    }

    public void inputQuery() {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }
        Scanner scan = new Scanner(System.in);

        while (true) {
            System.out.printf("%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s", "Please input:",
                    "'a' if you want to Find all groups with less or equals X students count;",
                    "'b' if you want to Find all students related to course with given name;",
                    "'c' if you want to Add new student;",
                    "'d' if you want to Delete student by STUDENT_ID;",
                    "'e' if you want to Add a student to the course (from a list);",
                    "'f' if you want to Remove the student from one of his or her courses;",
                    "-------------------------------------------------------------------------",
                    "Your choice: "
            );

            processInput(scan);

            System.out.print("Do you want to continue working with this application? [y/n] ");
            String answer = scan.nextLine();

            if (!answer.equals("y") && !answer.equals("Y") && !answer.equals("yes") && !answer.equals("YES") && !answer.equals("Yes")) {
                break;
            }
        }
    }

    private void processInput(Scanner scan) {
        String input = scan.nextLine();

        if (input.charAt(0) < 'a' || input.charAt(0) > 'f' || input.length() > 1) {
            System.out.printf("%s%n", "Incorrect input. Please, try again.");
        } else {
            if (input.charAt(0) == 'a') {
                System.out.printf("%s", "Please input number of Students: ");
                int number = Integer.parseInt(scan.nextLine());

                System.out.print(userQuery.findGroupsWithLessOrEqualsStudents(number));

            } else if (input.charAt(0) == 'b') {
                System.out.printf("%s", "Please input Course name: ");
                String courseName = scan.nextLine();

                System.out.print(userQuery.findAllStudentsOfTheCourse(courseName));

            } else if (input.charAt(0) == 'c') {
                System.out.printf("%s", "Please input Student's first name: ");
                String firstName = scan.nextLine();
                System.out.printf("%s", "Please input Student's last name: ");
                String lastName = scan.nextLine();

                userQuery.addNewStudent(firstName, lastName);
                System.out.printf("Student %s %s has been successfully added.%n", firstName, lastName);

            } else if (input.charAt(0) == 'd') {
                System.out.printf("%s", "Please input Student's id number: ");
                int id = Integer.parseInt(scan.nextLine());

                userQuery.deleteStudentById(id);
                System.out.printf("Student #%d has been successfully deleted.%n", id);

            } else if (input.charAt(0) == 'e') {
                List<Course> courses = userQuery.getAllCourses();
                System.out.printf("%s%n", "Please assign Student to one of the Course listed below, choosing Course Id from the left column: ");
                courses.stream().map(c -> c.getCourseId() + " -- " + c.getCourseName()).forEach(System.out::println);

                System.out.printf("%s", "Please input Student's id number: ");
                int studentId = Integer.parseInt(scan.nextLine());
                System.out.printf("%s", "Please input Course id: ");
                int courseId = Integer.parseInt(scan.nextLine());

                userQuery.addStudentToCourse(studentId, courseId);
                System.out.printf("Student #%d has been successfully assigned to course #%d.%n", studentId, courseId);

            } else if (input.charAt(0) == 'f') {
                System.out.print("Please input Student id: ");
                int studentId = Integer.parseInt(scan.nextLine());

                System.out.print("Please input Course id: ");
                int courseId = Integer.parseInt(scan.nextLine());
                userQuery.deleteStudentFromCourse(studentId, courseId);
                System.out.printf("Student #%d has been successfully deleted from course #%d.%n", studentId, courseId);
            }
        }
    }
}
