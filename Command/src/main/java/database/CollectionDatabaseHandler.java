package database;

import collection.*;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 03.04.2023 23:54
 */
public class CollectionDatabaseHandler {
    private final Connection connection;

    public CollectionDatabaseHandler(Connection connection) {
        this.connection = connection;
    }

    public void insertRow(LabWork labWork) throws SQLException {
        String sql = "INSERT INTO LABWORKCOLLECTION (name, coordinate_x, coordinate_y, creation_date, minimalPoint, maximumPoint, " +
                "personalQualitiesMaximum, difficulty, PersonName, PersonHeight,PersonPassportID,LocationX," +
                "LocationY,LocationName,id,Owner) Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, labWork.getName());
        ps.setLong(2, labWork.getCoordinates().getX());
        ps.setDouble(3, labWork.getCoordinates().getY());
        ps.setTimestamp(4, Timestamp.valueOf(labWork.getCreationDate().toLocalDateTime()));
        ps.setInt(5, labWork.getMinimalPoint());
        ps.setDouble(6, labWork.getMaximumPoint());
        ps.setInt(7, labWork.getPersonalQualitiesMaximum());
        ps.setString(8,labWork.getDifficulty().toString());
//        if (labWork.getCave().getDepth() != null) {
//            ps.setDouble(9, dragon.getCave().getDepth());
//        } else ps.setNull(9, Types.NULL);
        ps.setString(9, labWork.getPerson().getName());
        ps.setFloat(10, labWork.getPerson().getHeight());
        ps.setString(11, labWork.getPerson().getPassportID());
        ps.setInt(12, labWork.getPerson().getLocation().getX());
        ps.setFloat(13, labWork.getPerson().getLocation().getY());
        ps.setString(14, labWork.getPerson().getLocation().getName());
        ps.setInt(15,labWork.getId());
        ps.setString(16,labWork.getOwner());

        ps.executeUpdate();
        ps.close();
    }

    public void replaceRow(LabWork labWork) throws SQLException {
        String sql = "UPDATE LABWORKCOLLECTION SET(name, coordinate_x, coordinate_y, minimalPoint, maximumPoint, " +
                "personalQualitiesMaximum, difficulty, PersonName, PersonHeight,PersonPassportID,LocationX," +
                "LocationY,LocationName)= (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?)" +
                "WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, labWork.getName());
        ps.setLong(2, labWork.getCoordinates().getX());
        ps.setDouble(3, labWork.getCoordinates().getY());
        ps.setLong(4, labWork.getMinimalPoint());
        ps.setDouble(5, labWork.getMaximumPoint());
        ps.setInt(6, labWork.getPersonalQualitiesMaximum());
        ps.setString(7,labWork.getDifficulty().toString());
//        if (labWork.getCave().getDepth() != null) {
//            ps.setDouble(9, dragon.getCave().getDepth());
//        } else ps.setNull(9, Types.NULL);
        ps.setString(8, labWork.getPerson().getName());
        ps.setFloat(9, labWork.getPerson().getHeight());
        ps.setString(10, labWork.getPerson().getPassportID());
        ps.setInt(11, labWork.getPerson().getLocation().getX());
        ps.setFloat(12, labWork.getPerson().getLocation().getY());
        ps.setString(13, labWork.getPerson().getLocation().getName());
        ps.setInt(14,labWork.getId());

        ps.executeUpdate();
        ps.close();
    }

    public void deleteRowById(Integer id) throws SQLException {
        String sql = "DELETE FROM LABWORKCOLLECTION WHERE ID = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        int delRows = ps.executeUpdate();
        if (delRows == 1) {
            System.out.println("Строка была удалена.");
        } else System.out.println("Не было удалено строк.");

    }

//    public Integer getOwnedRowByColor(Color color, UserData userData) throws SQLException { //if find - return id, else return null
//        String sql = "SELECT ID FROM DRAGONCOLLECTION WHERE color = ? AND OWNER = ?";
//        PreparedStatement ps = connection.prepareStatement(sql);
//        ps.setString(1, color.name());
//        ps.setString(2, userData.getLogin());
//        ResultSet rs = ps.executeQuery();
//        if (rs.next()) {
//            return rs.getInt(1);
//        }
//        return null;
//    }

    public boolean isAnyRowById(Integer id) throws SQLException {
        String sql = "SELECT * FROM LABWORKCOLLECTION WHERE ID = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        return rs.isBeforeFirst();
    }

    public boolean isOwner(Integer id, UserData userData) throws SQLException {
        String sql = "SELECT * FROM LABWORKCOLLECTION WHERE ID = ? AND OWNER = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.setString(2, userData.getLogin());
        ResultSet rs = ps.executeQuery();
        return rs.isBeforeFirst();
    }

    public LabWork getLabWorkById(Integer id) throws SQLException {
        String sql = "SELECT * FROM LABWORKCOLLECTION WHERE ID = ?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return this.createLabWorkFromCurrentRow(rs);
        }
        return null;
    }

    public void deleteAllOwned(UserData userData) throws SQLException { //Возвращает id всех удаленных элементов
        String sql = "DELETE FROM LABWORKCOLLECTION WHERE OWNER = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, userData.getLogin());
        int delRows = ps.executeUpdate();
        System.out.println("Было удалено " + delRows + " строк.");
    }

    public LabWork[] loadInMemory() throws SQLException {
        TreeMap<Integer, LabWork> treeMap = new TreeMap<>();
        String sql = "SELECT * FROM LABWORKCOLLECTION";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                LabWork labWork = this.createLabWorkFromCurrentRow(rs);
                treeMap.put(labWork.getId(), labWork); //if (dragon != null)
            }
        }
        return treeMap.values().toArray(new LabWork[0]);
    }

    private LabWork createLabWorkFromCurrentRow(ResultSet rs) throws SQLException {
//        Integer id = rs.getInt(1);
//        String dragon_name = rs.getString(2);
//        Double coordinate_x = rs.getDouble(3);
//        Integer coordinate_y = rs.getInt(4);
//        ZonedDateTime creationDate = rs.getTimestamp(5).toLocalDateTime().atZone(ZoneId.of("UTC+03:00"));
//        Long age = rs.getLong(6);
//        Color color = Color.valueOf(rs.getString(7));
//        DragonType type = DragonType.valueOf(rs.getString(8));
//        DragonCharacter dragonCharacter = DragonCharacter.valueOf(rs.getString(9));
//        Double depth = rs.getDouble(10);
//        String owner = rs.getString(11);
//
//        return Dragon.createDragon(id, dragon_name, coordinate_x, coordinate_y, age, color, type, dragonCharacter, depth, creationDate, owner);
        Integer id=rs.getInt(1);
        String name = rs.getString(2);
        Long coordinate_x=rs.getLong(3);
        Double coordinate_y=rs.getDouble(4);
        ZonedDateTime creationDate=rs.getTimestamp(5).toLocalDateTime().atZone(ZoneId.of("UTC+03:00"));
        Integer minimalPoint=rs.getInt(6);
        Double maximumPoint=rs.getDouble(7);
        Integer personalQualitiesMaximum=rs.getInt(8);
        Difficulty difficulty=Difficulty.valueOf(rs.getString(9));
        String personName=rs.getString(10);
        Float personHeight=rs.getFloat(11);
        String personPassportID=rs.getString(12);
        Integer locationX=rs.getInt(13);
        Float locationY=rs.getFloat(14);
        String locationName=rs.getString(15);
        String owner=rs.getString(16);
        return LabWork.createLabWork(id,name,new Coordinates(coordinate_x,coordinate_y),creationDate,minimalPoint,maximumPoint,personalQualitiesMaximum,difficulty,new Person(personName,personHeight,personPassportID,new Location(locationX,locationY,locationName)),owner);

    }

    public Integer[] getAllOwner(UserData userData) throws SQLException {
        String sql = "SELECT * FROM LABWORKCOLLECTION WHERE OWNER = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, userData.getLogin());
        ResultSet rs = ps.executeQuery();
        ArrayList<Integer> ids = new ArrayList<>();
        while (rs.next()) {
            ids.add(rs.getInt(1));
        }
        return ids.toArray(new Integer[0]);
    }
}
