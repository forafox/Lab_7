package database;

import collection.*;
import dao.LabWorkDAO;
import dao.LocationDAO;
import dao.PersonDAO;
import dao.UserDAO;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 03.04.2023 23:54
 */
public class CollectionDatabaseHandler {
    private static SessionFactory sessionFactory;

    public CollectionDatabaseHandler(SessionFactory sessionFactory) {
        this.sessionFactory=sessionFactory;
    }
    /**
     *Ready
     * @param labWork
     * @throws SQLException
     */
    public void insertRowNOTid(LabWork labWork) throws SQLException{
        Session session = sessionFactory.openSession();
        UserDAO userDAO =this.findUser(labWork.getOwner());
        PersonDAO personDAO = new PersonDAO(labWork);
        LocationDAO locationDAO = new LocationDAO(labWork);
        LabWorkDAO labWorkDAO=new LabWorkDAO(labWork);
        labWorkDAO.setPersonDAO(personDAO);
        labWorkDAO.setLocationDAO(locationDAO);

        personDAO.setOwner(userDAO);
        personDAO.setLabWorkDAO(labWorkDAO);
        locationDAO.setOwner(userDAO);
        locationDAO.setLabWorkDAO(labWorkDAO);
        labWorkDAO.setOwner(userDAO);

        session.beginTransaction();
        session.persist(personDAO);
        session.persist(locationDAO);
        session.persist(labWorkDAO);
        session.getTransaction().commit();
        session.close();
    }

    /**
     * Ready
     * @param userData
     * @return
     */
    public static UserDAO findUser(UserData userData){
        Session session = sessionFactory.openSession();
        String sql = "From users_test";
        List<UserDAO> users = session.createQuery(sql).list();
        for (Iterator<UserDAO> it = users.iterator(); it.hasNext();) {
            UserDAO user = (UserDAO) it.next();
            if(user.getName().equals(userData.getLogin())){
                session.close();
                return user;
            }
        }
        session.close();
        return null;
    }
    /**
     * Ready
     * @param
     * @return
     */
    public static UserDAO findUserByName(String  name){
        UserDAO userDAO;
        Session session = sessionFactory.openSession();
        String hql=" FROM users_test WHERE name=:name";
        Query query = session.createQuery(hql);
        query.setParameter("name", name);
        List<UserDAO> userDAOS = query.getResultList();
        session.close();
        if(!userDAOS.isEmpty()){
            session.close();
            return userDAOS.get(0);
        }
        session.close();
        return null;
    }

    /**
     * READY
     * @param labWork
     */
    public void replaceRow(LabWork labWork){
        Session session=sessionFactory.openSession();
        LabWorkDAO labWorkDAO=session.get(LabWorkDAO.class, labWork.getId());
        LocationDAO locationDAO=labWorkDAO.getLocationDAO();
        PersonDAO personDAO=labWorkDAO.getPersonDAO();
        UserDAO userDAO=labWorkDAO.getOwner();

        locationDAO.setOwner(userDAO);
        personDAO.setOwner(userDAO);

        labWorkDAO.update(labWork);
        locationDAO.update(labWork);
        personDAO.update(labWork);
        session.beginTransaction();
        session.merge(locationDAO);
        session.merge(personDAO);
        session.merge(labWorkDAO);
        session.getTransaction().commit();
        session.close();
    }

    /**
     *READY
     * @param id
     * @throws SQLException
     */
    public void deleteRowById(Integer id) throws SQLException {
        Session session=sessionFactory.openSession();
          LabWorkDAO labWorkDAO=session.get(LabWorkDAO.class,id);
          session.beginTransaction();
          session.remove(labWorkDAO);
          session.getTransaction().commit();
          session.close();
    }
    /**
     * Ready
     * @param id
     * @param userData
     * @return
     * @throws SQLException
     */
    public boolean isOwner(Integer id, UserData userData) throws SQLException {
        userData.setUserStatus(isAdmin(userData));
        if(userData.getUserStatus().equals(UserStatus.ADMIN)){
            return true;
        }
        Session session=sessionFactory.openSession();
        UserDAO userDAO=findUser(userData);
        for(LabWorkDAO labWorkDAO : userDAO.getLabWorksDAO()){
            if(labWorkDAO.getId()==id){
                session.close();
                return true;
            }
        }
        return false;
    }


    /**
     * Ready
     * @param labWork
     * @return
     * @throws SQLException
     */
    public Integer getIdByLabWork(LabWork labWork) throws SQLException {
        Session session = sessionFactory.openSession();
        String hql="SELECT id FROM labWork_test WHERE name=:name and coordinateX=:coordinateX";
        Query query = session.createQuery(hql);
        query.setParameter("name", labWork.getName()).setParameter("coordinateX",labWork.getCoordinates().getX());
        List<Integer> labWorkDAOS = query.getResultList();
        session.close();
        return labWorkDAOS.get(0);
        }
    /**
     * READY
     * @param userData
     * @throws SQLException
     */
    public void deleteAllOwned(UserData userData){
        UserDAO userDAO=this.findUser(userData);
        Session session=sessionFactory.openSession();
        List<LabWorkDAO> labWorkDAOS=userDAO.getLabWorksDAO();
        session.beginTransaction();
        for(LabWorkDAO labWorkDAO : labWorkDAOS) {
            session.remove(labWorkDAO);
        session.flush();
        }
        session.getTransaction().commit();
        session.close();
    }

    /**
     *
     */
    public UserStatus isAdmin(UserData userData){
        UserDAO userDAO=findUser(userData);
        return userDAO.getUserStatus();
    }

    /**
     * READY
     * @return
     * @throws SQLException
     */
    public LabWork[] loadInMemory() throws SQLException {
        TreeMap<Integer, LabWork> treeMap = new TreeMap<>();
        Session session = sessionFactory.openSession();
        String sql = "select distinct t.id,t.name,t.coordinateX,t.coordinateY,t.creationDate,t.minimalPoint" +
                ",t.maximumPoint,t.personalQualitiesMaximum,t.difficulty,p.name,p.height,p.passportId," +
                "l.locationX,l.locationY,l.name,c.name,c.passwordDigest from labWork_test t " +
                "left join  t.locationDAO l left join t.personDAO p left join t.creator c";
        List<Object[]> rows = session.createQuery(sql).list();

        for(Object[] row : rows) {
            Integer id=Integer.valueOf(row[0].toString());
            String name = (row[1].toString());
            Long coordinate_x=Long.valueOf(row[2].toString());
            Double coordinate_y=Double.valueOf(row[3].toString());

            String pattern = "yyyy-MM-dd'T'HH:mm:ss";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime localDateTime = LocalDateTime.from(formatter.parse(row[4].toString().substring(0,19)));
            ZonedDateTime creationDate = Timestamp.valueOf(localDateTime).toLocalDateTime().atZone(ZoneId.of("UTC+03:00"));

            //ZonedDateTime creationDate=Timestamp.valueOf(row[4].toString()).toLocalDateTime().atZone(ZoneId.of("UTC+03:00"));
            Integer minimalPoint=Integer.valueOf(row[5].toString());
            Double maximumPoint=Double.valueOf(row[6].toString());
            Integer personalQualitiesMaximum=Integer.valueOf(row[7].toString());
            Difficulty difficulty=Difficulty.valueOf(row[8].toString());
            String personName=(row[9].toString());
            Float personHeight=Float.valueOf(row[10].toString());
            String personPassportID=(row[11].toString());
            Integer locationX=Integer.valueOf(row[12].toString());
            Float locationY=Float.valueOf(row[13].toString());
            String locationName=(row[14].toString());
            String ownerName=(row[15].toString());
            String ownerPassword=(row[16].toString());
            LabWork labWork=LabWork.createLabWork(id,name,new Coordinates(coordinate_x,coordinate_y),creationDate,minimalPoint,maximumPoint,personalQualitiesMaximum,difficulty,new Person(personName,personHeight,personPassportID,new Location(locationX,locationY,locationName)),new UserData(ownerName,ownerPassword));
            treeMap.put(labWork.getId(),labWork);
        }
        session.close();
        return treeMap.values().toArray(new LabWork[0]);
    }

    /**
     * READY
     * @param userData
     * @return
     * @throws SQLException
     */
    public Integer[] getAllOwner(UserData userData)  {

        ArrayList<Integer> ids = new ArrayList<>();
        List<LabWorkDAO> labWorkDAOS;
        Session session= sessionFactory.openSession();
        if(isAdmin(userData).equals(UserStatus.SIMPLE_USER)){
        UserDAO userDAO=this.findUser(userData);
        labWorkDAOS = userDAO.getLabWorksDAO();
        }else{
            String sql = "From labWork_test";
            labWorkDAOS = session.createQuery(sql).list();
        }
        for (LabWorkDAO labWorkDAO : labWorkDAOS) {
            ids.add(labWorkDAO.getId());
        }
        session.close();
        return ids.toArray(new Integer[0]);
    }
    /**
     *
     */
    public Integer[] removeUser(String str){
        UserDAO userDAO=findUserByName(str);
        ArrayList<Integer> ids = new ArrayList<>();
        for (LabWorkDAO labWorkDAO : userDAO.getLabWorksDAO()) {
        ids.add(labWorkDAO.getId());
    }
        Session session2=sessionFactory.openSession();
        session2.beginTransaction();
        session2.remove(userDAO);
        session2.getTransaction().commit();
        session2.close();

        return ids.toArray(new Integer[0]);
    }


}
