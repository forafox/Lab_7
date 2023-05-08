package database;

import dao.UserDAO;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 03.04.2023 23:54
 */
public class UserDatabaseHandler {
    private SessionFactory sessionFactory;

    public UserDatabaseHandler(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void addUser(UserData userData) {
        String user_login = userData.getLogin();
        String user_pass = userData.getPassword();
        user_pass=DataEncryptor.sha256(user_pass,userData,1);
        user_pass=DataEncryptor.sha256(user_pass+userData.getSalt(),userData,3);
        Session session = sessionFactory.openSession();
        String hql="SELECT name FROM users_test WHERE name=:username";
        Query query=session.createQuery(hql , String.class);
        query.setParameter("username", userData.getLogin());
        if (query.getResultList().isEmpty()) {
            UserDAO userDAO = new UserDAO();
            userDAO.setName(user_login);
            userDAO.setPasswordDigest(user_pass);
            userDAO.setSalt(userData.getSalt());
            session.beginTransaction();
            session.persist(userDAO);
            session.getTransaction().commit();
        } else {
            System.out.println(user_login);
            System.out.println("Пользователь с указанным login уже существует\nДальнейшая ответственность за командой verifyUser");
        }
        session.close();
    }

    public void ConnectUser(UserData userData) {
        userData.setIsConnected(true);
    }

    public void verifyUser(UserData userData) {
        Session session = sessionFactory.openSession();
        String saltValue="SELECT salt FROM users_test WHERE name=:username";
        Query query=session.createQuery(saltValue);
        query.setParameter("username",userData.getLogin());
        List<String> rows = query.getResultList();
        String str=rows.get(0);

        String hql="SELECT name FROM users_test WHERE name=:username and passwordDigest=:password";
        Query query2=session.createQuery(hql , String.class);

        userData.setSalt(str);
        String password=new String(DataEncryptor.sha256(userData.getPassword(),userData,2));
        password=DataEncryptor.sha256(password+userData.getSalt(),userData,3);
        query2.setParameter("username", userData.getLogin()).setParameter("password",password);
        if (query2.getResultList().isEmpty()) {
            userData.setIsNewUser(true);
        } else {
            userData.setIsNewUser(false);
        }
        session.close();
    }
}
