package ua.com.foxminded.sql.entity;

import java.util.Objects;

public class Course {

    private int courseId;
    private String courseName;
    private String courseDescription;

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

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course course = (Course) o;
        return getCourseId() == course.getCourseId() &&
                getCourseName().equals(course.getCourseName()) &&
                getCourseDescription().equals(course.getCourseDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCourseId(), getCourseName(), getCourseDescription());
    }


    @Override
    public String toString() {
        return "Courses{" +
                "courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", courseDescription='" + courseDescription + '\'' +
                '}';
    }
}
