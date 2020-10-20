package ua.com.foxminded.sql.entity;

import java.util.Objects;

public class Student {

    private int studentId;
    private int groupId;
    private String firstName;
    private String lastName;


    public int getStudentId() {
        return studentId;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student students = (Student) o;
        return studentId == students.studentId &&
                groupId == students.groupId &&
                firstName.equals(students.firstName) &&
                lastName.equals(students.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, groupId, firstName, lastName);
    }

    @Override
    public String toString() {
        return "Students{" +
                "studentId=" + studentId +
                ", groupId=" + groupId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
