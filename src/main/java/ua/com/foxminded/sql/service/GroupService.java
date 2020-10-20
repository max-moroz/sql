package ua.com.foxminded.sql.service;

import ua.com.foxminded.sql.exception.DAOException;
import ua.com.foxminded.sql.tools.LogConfigurator;
import ua.com.foxminded.sql.dao.GroupDAO;
import ua.com.foxminded.sql.entity.Group;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GroupService implements GroupDAO {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String CONNECTION_OPENED = "Connection opened.";
    private static final String LOG_START = "occurred during";
    private static final String CLASS_NAME = GroupService.class.getName();

    private final DataSource dataSource;
    private final LogConfigurator logConfigurator;

    public GroupService(DataSource dataSource, LogConfigurator logConfigurator) {
        this.dataSource = dataSource;
        this.logConfigurator = logConfigurator;
    }

    @Override
    public boolean add(Group group) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "INSERT INTO foxminded.groups (group_id, group_name) VALUES (?, ?)";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.log(Level.INFO, CONNECTION_OPENED);

            prStatement.setInt(1, group.getGroupId());
            prStatement.setString(2, group.getGroupName());

            prStatement.executeUpdate();
            LOGGER.log(Level.INFO, "New group {0} added to groups table", group.getGroupName());
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " add method invocation");
            return false;
        }
    }

    @Override
    public boolean add(String groupName) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "INSERT INTO foxminded.groups (group_name) VALUES (?)";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.log(Level.INFO, CONNECTION_OPENED);

            prStatement.setString(1, groupName);

            prStatement.executeUpdate();
            LOGGER.log(Level.INFO, "New group {0} added to groups table", groupName);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " add method invocation");
            return false;
        }
    }

    @Override
    public List<Group> getAllGroups() {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        List<Group> groupsList = new ArrayList<>();
        String sqlQuery = "SELECT group_id, group_name FROM foxminded.groups";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.log(Level.INFO, CONNECTION_OPENED);

            try (ResultSet resultSet = prStatement.executeQuery()) {
                while (resultSet.next()) {
                    Group group = new Group();
                    group.setGroupId(resultSet.getInt("group_id"));
                    group.setGroupName(resultSet.getString("group_name"));

                    groupsList.add(group);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getAllGroups method invocation (resultSet handling)");
                throw new DAOException("ResultSet is failed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getAllGroups method invocation (statement handling)");
            throw new DAOException("PreparedStatement or Connection is failed");
        }
        return groupsList;
    }

    @Override
    public Group getGroupById(int id) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        Group group = new Group();
        String sqlQuery = "SELECT group_id, group_name FROM foxminded.groups WHERE group_id = ?";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.log(Level.INFO, CONNECTION_OPENED);
            prStatement.setInt(1, id);

            try (ResultSet resultSet = prStatement.executeQuery()) {
                while (resultSet.next()) {
                    group.setGroupId(resultSet.getInt("group_id"));
                    group.setGroupName(resultSet.getString("group_name"));
                }
            } catch (SQLException e) {
                LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getGroupById method invocation (resultSet handling)");
                throw new DAOException("ResultSet is failed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getGroupById method invocation (preparedStatement handling)");
            throw new DAOException("PreparedStatement or Connection is failed");
        }
        return group;
    }

    @Override
    public int getMaxGroupId() {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "SELECT MAX(group_id) FROM foxminded.groups;";

        int maxGroupId = 0;
        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {

            try (ResultSet resultSet = prStatement.executeQuery()) {
                while (resultSet.next()) {
                    maxGroupId = resultSet.getInt(1);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getMaxGroupId method invocation (resultSet handling)");
                throw new DAOException("ResultSet is failed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getMaxGroupId method invocation (prepareStatement handling)");
            throw new DAOException("Connection or PrStatement is failed");
        }
        return maxGroupId;
    }

    @Override
    public int getStudentsNumberInGroup(int groupId) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "SELECT g.group_id, COUNT(s.student_id) FROM foxminded.groups AS g " +
                "LEFT JOIN foxminded.students AS s " +
                "ON g.group_id = s.group_id " +
                "WHERE g.group_id = ? " +
                "GROUP BY g.group_id;";

        int studentsNumber = 0;
        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            prStatement.setInt(1, groupId);

            try (ResultSet resultSet = prStatement.executeQuery()) {
                while (resultSet.next()) {
                    studentsNumber = resultSet.getInt(2);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getStudentsNumberInGroup method invocation (resultSet handling)");
                throw new DAOException("ResultSet is failed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getStudentsNumberInGroup method invocation (prepareStatement handling)");
            throw new DAOException("Connection or PrStatement is failed");
        }
        return studentsNumber;
    }

    @Override
    public int getStudentsNumberInSmallGroups() {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "SELECT g.group_id, SUM(COUNT(s.student_id)) OVER() FROM foxminded.groups AS g " +
                "LEFT JOIN foxminded.students AS s " +
                "ON g.group_id = s.group_id " +
                "WHERE s.group_id IS NOT NULL " +
                "GROUP BY g.group_id " +
                "HAVING COUNT(*) < 10;";

        int studentsNumber = 0;
        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {

            try (ResultSet resultSet = prStatement.executeQuery()) {
                if (resultSet.next()) {
                    studentsNumber = resultSet.getInt(2);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getStudentsNumberInSmallGroups method invocation (resultSet handling)");
                throw new DAOException("ResultSet is failed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getStudentsNumberInSmallGroups method invocation (prepareStatement handling)");
            throw new DAOException("Connection or PrStatement is failed");
        }
        return studentsNumber;
    }


    @Override
    public int getSmallGroupsNumber() {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "SELECT g.group_id, COUNT(COUNT(s.student_id)) OVER() FROM foxminded.groups AS g " +
                "LEFT JOIN foxminded.students AS s " +
                "ON g.group_id = s.group_id " +
                "WHERE s.group_id IS NOT NULL " +
                "GROUP BY g.group_id " +
                "HAVING COUNT(*) < 10;";

        int groupsNumber = 0;
        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {

            try (ResultSet resultSet = prStatement.executeQuery()) {
                if (resultSet.next()) {
                    groupsNumber = resultSet.getInt(2);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getSmallGroupsNumber method invocation (resultSet handling)");
                throw new DAOException("ResultSet is failed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getSmallGroupsNumber method invocation (prepareStatement handling)");
            throw new DAOException("Connection or PrStatement is failed");
        }
        return groupsNumber;
    }


    @Override
    public List<Integer> getSmallGroupsId() {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        List<Integer> id = new ArrayList<>();
        String sqlQuery = "SELECT g.group_id FROM foxminded.groups AS g " +
                "LEFT JOIN foxminded.students AS s " +
                "ON g.group_id = s.group_id " +
                "WHERE s.group_id IS NOT NULL " +
                "GROUP BY g.group_id " +
                "HAVING COUNT(*) < 10;";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {

            try (ResultSet resultSet = prStatement.executeQuery()) {
                while (resultSet.next()) {
                    id.add(resultSet.getInt(1));
                }
            } catch (SQLException e) {
                LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getSmallGroupsId method invocation (resultSet handling)");
                throw new DAOException("ResultSet is failed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " getSmallGroupsId method invocation (prepareStatement handling)");
            throw new DAOException("Connection or PrStatement is failed");
        }
        return id;
    }

    @Override
    public boolean update(Group group) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "UPDATE foxminded.groups SET group_name = ? WHERE group_id = ?";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            prStatement.setString(1, group.getGroupName());
            prStatement.setInt(2, group.getGroupId());

            prStatement.executeUpdate();
            LOGGER.log(Level.INFO, "Group #{0} has been successfully updated", group.getGroupId());
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " update method invocation (preparedStatement handling)");
            return false;
        }

    }

    @Override
    public boolean remove(Group group) {
        try {
            logConfigurator.setup();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " LogConfigurator setup");
        }

        String sqlQuery = "DELETE FROM foxminded.groups WHERE group_id = ?";

        try (Connection connection = dataSource.getConnection(); PreparedStatement prStatement = connection.prepareStatement(sqlQuery)) {
            LOGGER.log(Level.INFO, CONNECTION_OPENED);
            int id = group.getGroupId();
            prStatement.setInt(1, group.getGroupId());
            prStatement.executeUpdate();

            LOGGER.log(Level.INFO, "Group # {0} has been successfully deleted", id);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, e, () -> LOG_START + CLASS_NAME + " remove method invocation (preparedStatement handling)");
            return false;
        }
    }
}
