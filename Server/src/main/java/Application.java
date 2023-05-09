import collection.CollectionManager;
import collection.LabWork;
import commands.CommandInvoker;
import dao.LabWorkDAO;
import dao.LocationDAO;
import dao.PersonDAO;
import dao.UserDAO;
import database.CollectionDatabaseHandler;
import database.UserDatabaseHandler;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.io.*;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 25.03.2023 18:11
 */
public class Application {
    private static SessionFactory sessionFactory;
    private static ServiceRegistry serviceRegistry;
    private static final String RELATIVE_PATH_TO_CREDENTIALS = "data/credentials.txt";
    private static final String RELATIVE_PATH_TO_HIBERNATE_CONFIG = "hibernate.cfg.xml";
    private static final Logger rootLogger = LogManager.getRootLogger();

    public void start(int port){
        this.createDatabaseConnection();
        UserDatabaseHandler udh=new UserDatabaseHandler(sessionFactory);
        //UserDatabaseHandler udh=new UserDatabaseHandler(dbConnection);
        CollectionDatabaseHandler cdh=new CollectionDatabaseHandler(sessionFactory);
        try {
            LabWork[] labWorks = cdh.loadInMemory();
            CollectionManager collectionManager = new CollectionManager(labWorks);
            rootLogger.info("Коллекция была загружена из бд.");

            ReadWriteLock locker = new ReentrantReadWriteLock();
            CommandInvoker commandInvoker = new CommandInvoker(collectionManager, cdh, locker);
            rootLogger.info("Класс Application готов.");

            ServerConnection serverConnection = new ServerConnection();//здесь хранится datagramSocket сервера.
            serverConnection.createFromPort(port);

            RequestReader requestReader = new RequestReader(serverConnection.getServerSocket());
            ResponseSender responseSender = new ResponseSender(serverConnection.getServerSocket());
            CommandProcessor commandProcessor = new CommandProcessor(udh, cdh, commandInvoker);

            Server server = new Server(requestReader, responseSender, commandProcessor);
            rootLogger.info("Start the main server");
            server.startWork();
        }
        catch (SQLException ex) {
            rootLogger.error("Ошибка при загрузке коллекции в память. Завершение работы сервера.");
            System.exit(-10);
        }
    }

    private void createDatabaseConnection(){
        Scanner scanner=new Scanner(System.in);
        rootLogger.info("Введите данные для входа. Логин и Пароль");
        String jdbcHeliosURL="jdbc:postgresql://localhost:5432/studs";
        String login="s367268";
        String password="N7N7tRi80ue5EgUk";
//        try {
//           // scanner=new Scanner(new FileReader(getClass().getClassLoader().getResource(RELATIVE_PATH_TO_CREDENTIALS).getFile()));
//            InputStream in = getClass().getResourceAsStream(RELATIVE_PATH_TO_CREDENTIALS);
//            File file = File.createTempFile("stream2file", ".tmp");
//            file.deleteOnExit();
//            try (FileOutputStream out = new FileOutputStream(file)) {
//                IOUtils.copy(in, out);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            scanner=new Scanner(file);
//
//        }catch (IOException ex){
//            rootLogger.error(ex.getMessage());
//            rootLogger.error("Не найден файл credentials.txt с данными для входа. Завершение работы");
//            System.exit(-1);
//        }
//        try{
//            login=scanner.nextLine().trim();
//            password=scanner.nextLine().trim();
//        }catch (NoSuchElementException ex){
//            rootLogger.error("Не найдены данные для входа. Завершение работы.");
//            System.exit(-1);
//        }
        rootLogger.info("Login and password were uploaded from resources");
        try {
            Configuration cfg=new Configuration().configure("hibernate.cfg.xml");
            cfg.addAnnotatedClass(LabWorkDAO.class);
            cfg.addAnnotatedClass(LocationDAO.class);
            cfg.addAnnotatedClass(PersonDAO.class);
            cfg.addAnnotatedClass(UserDAO.class);
            sessionFactory=cfg.buildSessionFactory();
            rootLogger.info("Соединение с бд установлено");
        }catch (Exception ex){
            rootLogger.error("Соединение с бд не установлено. Завершение работы сервера");
            rootLogger.error(ex.getMessage());
            System.exit(-1);
        }
    }
}
