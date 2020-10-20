package ua.com.foxminded.sql.entity;

import java.util.Objects;

public class StudentCourse {

    private int studentCourseId;
    private int studentId;
    private String studentFirstName;
    private String studentLastName;
    private int courseId;
    private String courseName;

    public int getStudentCourseId() {
        return studentCourseId;
    }

    public void setStudentCourseId(int studentCourseId) {
        this.studentCourseId = studentCourseId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentFirstName() {
        return studentFirstName;
    }

    public void setStudentFirstName(String studentFirstName) {
        this.studentFirstName = studentFirstName;
    }

    public String getStudentLastName() {
        return studentLastName;
    }

    public void setStudentLastName(String studentLastName) {
        this.studentLastName = studentLastName;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentCourse that = (StudentCourse) o;
        return studentCourseId == that.studentCourseId &&
                studentId == that.studentId &&
                courseId == that.courseId &&
                studentFirstName.equals(that.studentFirstName) &&
                studentLastName.equals(that.studentLastName) &&
                courseName.equals(that.courseName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentCourseId, studentId, studentFirstName, studentLastName, courseId, courseName);
    }

    @Override
    public String toString() {
        return "StudentCourse{" +
                "studentCourseId=" + studentCourseId +
                ", studentId=" + studentId +
                ", studentFirstName='" + studentFirstName + '\'' +
                ", studentLastName='" + studentLastName + '\'' +
                ", courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                '}';
    }
}
