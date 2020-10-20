package ua.com.foxminded.sql.entity;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class GroupTest {

    Group group = new Group();

    @Test
    void getGroupId_ShouldReturnCourseId_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = group.getClass().getDeclaredField("groupId");
        field.setAccessible(true);
        field.set(group, 12);
        assertEquals(12, group.getGroupId());
    }

    @Test
    void setGroupId_ShouldSetGroupId_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = group.getClass().getDeclaredField("groupId");
        field.setAccessible(true);
        group.setGroupId(12);
        assertEquals(12, field.get(group));
    }

    @Test
    void getGroupName_ShouldReturnGroupName_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = group.getClass().getDeclaredField("groupName");
        field.setAccessible(true);
        field.set(group, "SR-12");
        assertEquals("SR-12", group.getGroupName());
    }

    @Test
    void setGroupName_ShouldSetGroupName_WhenInvoked() throws NoSuchFieldException, IllegalAccessException {
        final Field field = group.getClass().getDeclaredField("groupName");
        field.setAccessible(true);
        group.setGroupName("SR-12");
        assertEquals("SR-12", field.get(group));
    }

    //------------------

    @Test
    void equals_ShouldReturnTrue_ForNonNullReferenceValueOfTheSameObject() {
        Group group = new Group();
        group.setGroupId(1);
        group.setGroupName("group");

        assertTrue(group.equals(group));
    }

    @Test
    void equals_ShouldReturnTrue_ForSymmetricObjects() {
        Group group = new Group();
        group.setGroupId(1);
        group.setGroupName("group");

        Group group1 = group;

        assertTrue(group.equals(group1));
    }

    @Test
    void equals_ShouldReturnTrue_ForTransitiveObjects() {
        Group group = new Group();
        group.setGroupId(1);
        group.setGroupName("group");

        Group group1 = group;

        Group group2 = group1;

        assertTrue(group.equals(group2));
    }

    @Test
    void equals_ShouldReturnFalse_ForDifferentObjects() {
        Group group = new Group();
        group.setGroupId(1);
        group.setGroupName("group");

        Group group1 = new Group();

        assertAll(
                () -> assertFalse(group.equals(group1)),
                () -> assertFalse(group.equals(group1)),
                () -> assertFalse(group.equals(group1))
        );
    }

    @Test
    void equals_ShouldReturnFalse_ForNull() {
        Group group = new Group();
        group.setGroupId(1);
        group.setGroupName("group");

        assertFalse(group.equals(null));
    }

    //-----------------

    @Test
    void hasCode_ShallReturnTheSameHashCode_ForTheSameObjects() {
        Group group = new Group();
        group.setGroupId(1);
        group.setGroupName("group");

        Group group1 = group;

        assertEquals(group.hashCode(), group1.hashCode());
    }

    @Test
    void toString_ShallReturnStringRepresentationOfTheObject_WhenInvoked() {
        Group group = new Group();
        group.setGroupId(1);
        group.setGroupName("group");

        assertEquals("Groups{groupId=1, groupName='group'}", group.toString());
    }
}
