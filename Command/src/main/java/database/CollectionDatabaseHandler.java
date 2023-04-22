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
    /**
     *Работает
     * @param labWork
     * @throws SQLException
     */
    public void insertRowNOTid(LabWork labWork) throws SQLException{
        String sqlSeq="Select nextval('all_item_id')";
        PreparedStatement ps = connection.prepareStatement(sqlSeq);
        ResultSet rs= ps.executeQuery();
        rs.next();
        int curren_id=rs.getInt(1);
        rs.close();
        ps.close();

        String sql = "INSERT INTO LABWORKCOLLECTION (name, coordinate_x, coordinate_y, creation_date, minimalPoint, maximumPoint, " +
                "personalQualitiesMaximum, difficulty,Owner,lab_work_id) Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        ps = connection.prepareStatement(sql);
        ps.setString(1, labWork.getName());
        ps.setLong(2, labWork.getCoordinates().getX());
        ps.setDouble(3, labWork.getCoordinates().getY());
        ps.setTimestamp(4, Timestamp.valueOf(labWork.getCreationDate().toLocalDateTime()));
        ps.setInt(5, labWork.getMinimalPoint());
        ps.setDouble(6, labWork.getMaximumPoint());
        ps.setInt(7, labWork.getPersonalQualitiesMaximum());
        ps.setString(8,labWork.getDifficulty().toString());
        ps.setString(9,labWork.getOwner());
        ps.setInt(10,curren_id);
        ps.executeUpdate();
        ps.close();
        sql="INSERT INTO PERSON_OF_LABWORK (PersonName,PersonHeight,PersonPassportID,person_id,Owner) values (?,?,?,?,?)";
        ps=connection.prepareStatement(sql);
        ps.setString(1, labWork.getPerson().getName());
        ps.setFloat(2, labWork.getPerson().getHeight());
        ps.setString(3, labWork.getPerson().getPassportID());
        ps.setInt(4,curren_id);
        ps.setString(5, labWork.getOwner());
        ps.executeUpdate();
        ps.close();
        sql="INSERT INTO LOCATION_OF_LABWORK(LocationX,LocationY,LocationName,location_id,Owner) values (?,?,?,?,?)";
        ps=connection.prepareStatement(sql);
        ps.setInt(1, labWork.getPerson().getLocation().getX());
        ps.setFloat(2, labWork.getPerson().getLocation().getY());
        ps.setString(3, labWork.getPerson().getLocation().getName());
        ps.setInt(4,curren_id);
        ps.setString(5, labWork.getOwner());
        ps.executeUpdate();
        ps.close();
    }

    /**
     *Работает
     * @param labWork
     * @throws SQLException
     */
    public void replaceRow(LabWork labWork) throws SQLException {
        String sql = "UPDATE LABWORKCOLLECTION SET(name, coordinate_x, coordinate_y, minimalPoint, maximumPoint, " +
                "personalQualitiesMaximum, difficulty)= (?, ?, ?, ?, ?, ?, ?)" +
                "WHERE lab_work_id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, labWork.getName());
        ps.setLong(2, labWork.getCoordinates().getX());
        ps.setDouble(3, labWork.getCoordinates().getY());
        ps.setLong(4, labWork.getMinimalPoint());
        ps.setDouble(5, labWork.getMaximumPoint());
        ps.setInt(6, labWork.getPersonalQualitiesMaximum());
        ps.setString(7,labWork.getDifficulty().toString());
        ps.setInt(8,labWork.getId());
        ps.executeUpdate();
        ps.close();
        sql="UPDATE PERSON_OF_LABWORK SET(PersonName,PersonHeight,PersonPassportID)= (?,?,?)"+"WHERE person_id=?";
        ps = connection.prepareStatement(sql);
        ps.setString(1, labWork.getPerson().getName());
        ps.setFloat(2, labWork.getPerson().getHeight());
        ps.setString(3, labWork.getPerson().getPassportID());
        ps.setInt(4,labWork.getId());
        ps.executeUpdate();
        ps.close();
        sql="UPDATE LOCATION_OF_LABWORK SET(LocationX,LocationY,LocationName) = (?,?,?)"+"WHERE location_id=?";
        ps = connection.prepareStatement(sql);
        ps.setInt(1, labWork.getPerson().getLocation().getX());
        ps.setFloat(2, labWork.getPerson().getLocation().getY());
        ps.setString(3, labWork.getPerson().getLocation().getName());
        ps.setInt(4,labWork.getId());
        ps.executeUpdate();
        ps.close();
    }

    /**
     *Работает
     * @param id
     * @throws SQLException
     */
    public void deleteRowById(Integer id) throws SQLException {
        String sql = "DELETE FROM LABWORKCOLLECTION WHERE lab_work_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        int delRows = ps.executeUpdate();
        ps.close();
        sql = "DELETE FROM PERSON_OF_LABWORK WHERE person_id = ?";
        ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        int delRows2 = ps.executeUpdate();
        ps.close();
        sql = "DELETE FROM LOCATION_OF_LABWORK WHERE location_id = ?";
        ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        int delRows3 = ps.executeUpdate();
        ps.close();

        if (delRows == 1 && delRows2 == 1  && delRows3 == 1) {
            System.out.println("Строка была удалена.");
        } else System.out.println("Не было удалено строк.");

    }
    /**
     * работает
     * @param id
     * @param userData
     * @return
     * @throws SQLException
     */
    public boolean isOwner(Integer id, UserData userData) throws SQLException {
        String sql = "SELECT * FROM LABWORKCOLLECTION WHERE lab_work_id = ? AND OWNER = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.setString(2, userData.getLogin());
        ResultSet rs = ps.executeQuery();
        return rs.isBeforeFirst();
    }

    /**
     * работает
     * @param labWork
     * @return
     * @throws SQLException
     */
    public Integer getIdByLabWork(LabWork labWork) throws SQLException {
        String sql = "SELECT lab_work_id FROM LABWORKCOLLECTION WHERE name = ? AND coordinate_x = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, labWork.getName());
        ps.setLong(2, labWork.getCoordinates().getX());
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            return rs.getInt(1);
        }
        return null;
    }

    /**
     * работает
     * @param id
     * @return
     * @throws SQLException
     */
    public LabWork getLabWorkById(Integer id) throws SQLException {
        String sql = "Select * from LABWORKCOLLECTION " +
                "    join PERSON_OF_LABWORK on LABWORKCOLLECTION.lab_work_id=PERSON_OF_LABWORK.person_id" +
                "    join LOCATION_OF_LABWORK on LABWORKCOLLECTION.lab_work_id=LOCATION_OF_LABWORK.location_id WHERE lab_work_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return this.createLabWorkFromCurrentRow(rs);
        }
        return null;
    }

    /**
     * работает
     * @param userData
     * @throws SQLException
     */
    public void deleteAllOwned(UserData userData) throws SQLException {
        String sql = "DELETE FROM LABWORKCOLLECTION WHERE OWNER = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, userData.getLogin());
        int delRows = ps.executeUpdate();
        ps.close();

         sql = "DELETE FROM PERSON_OF_LABWORK WHERE OWNER = ?";
         ps = connection.prepareStatement(sql);
        ps.setString(1, userData.getLogin());
        int delRows2 = ps.executeUpdate();
        ps.close();

         sql = "DELETE FROM LOCATION_OF_LABWORK WHERE OWNER = ?";
         ps = connection.prepareStatement(sql);
        ps.setString(1, userData.getLogin());
        int delRows3 = ps.executeUpdate();
        ps.close();
        System.out.println("Было удалено " + delRows+delRows2+delRows3 + " строк.");
    }

    /**
     *Работает
     * @return
     * @throws SQLException
     */
    public LabWork[] loadInMemory() throws SQLException {
        TreeMap<Integer, LabWork> treeMap = new TreeMap<>();
        String sql = "Select * from LABWORKCOLLECTION " +
                "    join PERSON_OF_LABWORK on LABWORKCOLLECTION.lab_work_id=PERSON_OF_LABWORK.person_id" +
                "    join LOCATION_OF_LABWORK on LABWORKCOLLECTION.lab_work_id=LOCATION_OF_LABWORK.location_id";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                LabWork labWork = this.createLabWorkFromCurrentRow(rs);
                treeMap.put(labWork.getId(), labWork);
            }
        }
        return treeMap.values().toArray(new LabWork[0]);
    }

    /**
     *Работает
     * @param rs
     * @return
     * @throws SQLException
     */
    private LabWork createLabWorkFromCurrentRow(ResultSet rs) throws SQLException {
        Integer id=rs.getInt(1);
        String name = rs.getString(2);
        Long coordinate_x=rs.getLong(3);
        Double coordinate_y=rs.getDouble(4);
        ZonedDateTime creationDate=rs.getTimestamp(5).toLocalDateTime().atZone(ZoneId.of("UTC+03:00"));
        Integer minimalPoint=rs.getInt(6);
        Double maximumPoint=rs.getDouble(7);
        Integer personalQualitiesMaximum=rs.getInt(8);
        Difficulty difficulty=Difficulty.valueOf(rs.getString(9));
        String personName=rs.getString(12);
        Float personHeight=rs.getFloat(13);
        String personPassportID=rs.getString(14);
        Integer locationX=rs.getInt(17);
        Float locationY=rs.getFloat(18);
        String locationName=rs.getString(19);
        String owner=rs.getString(10);
        return LabWork.createLabWork(id,name,new Coordinates(coordinate_x,coordinate_y),creationDate,minimalPoint,maximumPoint,personalQualitiesMaximum,difficulty,new Person(personName,personHeight,personPassportID,new Location(locationX,locationY,locationName)),owner);

    }

    /**
     * Работает
     * @param userData
     * @return
     * @throws SQLException
     */
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
