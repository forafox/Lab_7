package database;

import dao.UserDAO;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
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
        Session session = sessionFactory.openSession();
        String hql="SELECT name FROM users_test WHERE name=:username";
        Query query=session.createQuery(hql , String.class);
        query.setParameter("username", userData.getLogin());
        if (query.getResultList().isEmpty()) {
            UserDAO userDAO = new UserDAO();
            userDAO.setName(user_login);
            userDAO.setPasswordDigest(user_pass);
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
        String hql="SELECT name FROM users_test WHERE name=:username and passwordDigest=:password";
        Query query=session.createQuery(hql , String.class);
        query.setParameter("username", userData.getLogin()).setParameter("password",userData.getPassword());
        if (query.getResultList().isEmpty()) {
            userData.setIsNewUser(true);
        } else {
            userData.setIsNewUser(false);
        }
        session.close();
    }
}
