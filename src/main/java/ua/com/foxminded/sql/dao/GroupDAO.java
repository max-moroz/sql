package ua.com.foxminded.sql.dao;

import ua.com.foxminded.sql.entity.Group;

import java.util.List;

public interface GroupDAO {

    //create
    boolean add(Group group);

    boolean add(String groupName);

    //read
    List<Group> getAllGroups();

    Group getGroupById(int id);

    int getMaxGroupId();

    int getStudentsNumberInGroup(int groupId);

    /**
     * Returns total number of students in groups with less then 10 but more then 0 Students
     */
    int getStudentsNumberInSmallGroups();

    /**
     * Returns Number of Groups with less then 10 but more then 0 Students
     */
    int getSmallGroupsNumber();

    /**
     * Returns List of Id of Groups with less then 10 but more then 0 Students
     */
    List<Integer> getSmallGroupsId();


    //update
    boolean update(Group group);

    //delete
    boolean remove(Group group);
}
