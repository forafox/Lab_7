import collection.CollectionManager;
import collection.LabWork;
import commands.CommandInvoker;
import commands.abstr.CommandContainer;
import database.CollectionDatabaseHandler;
import database.DatabaseConnection;
import database.UserData;
import database.UserDatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 25.03.2023 18:11
 */
public class Application {
    private static final Logger rootLogger = LogManager.getRootLogger();

    private Connection dbConnection;

    public void start(int port){
        this.createDatabaseConnection();
        UserDatabaseHandler udh=new UserDatabaseHandler(dbConnection);
        CollectionDatabaseHandler cdh=new CollectionDatabaseHandler(dbConnection);
        try {
            LabWork[] labWorks = cdh.loadInMemory();
            CollectionManager collectionManager = new CollectionManager(labWorks);
            rootLogger.info("Коллекция была загружена из бд.");
            Lock locker = new ReentrantLock();
            CommandInvoker commandInvoker = new CommandInvoker(collectionManager, cdh, locker);
            rootLogger.info("Класс Application готов.");

            ServerConnection serverConnection = new ServerConnection();//здесь хранится datagramSocket сервера.
            serverConnection.createFromPort(port);

            RequestReader requestReader = new RequestReader(serverConnection.getServerSocket());
            ResponseSender responseSender = new ResponseSender(serverConnection.getServerSocket());
            CommandProcessor commandProcessor = new CommandProcessor(udh, cdh, commandInvoker);

            Server server = new Server(requestReader, responseSender, commandProcessor);
            rootLogger.info("Старт нового потока");
            new Thread(server).start();
        }
        catch (SQLException ex) {
            System.out.println("Ошибка при загрузке коллекции в память. Завершение работы сервера.");
            System.exit(-10);
        }
//        catch (IOException ex) {
//            rootLogger.error(ex.getClass());
//            ex.printStackTrace();
//            System.exit(-15);
//        }
    }

    private void createDatabaseConnection(){
        Scanner scanner=new Scanner(System.in);
        rootLogger.info("Введите данные для входа. Логин и Пароль");
       // String jdbcHeliosURL="jdbc:postgresql://pg:5444/studs";
        String jdbcHeliosURL="jdbc:postgresql://localhost:5432/studs";
        String jdbcLocalURL="jdbc:postgresql://localhost:5489/studs";
        String login="";
        String password="";
        try {
            scanner=new Scanner(new FileReader("D:\\JavaProject\\Lab_7\\Server\\src\\main\\resources\\credentials.txt"));
        }catch (FileNotFoundException ex){
            rootLogger.error(ex.getMessage());
            rootLogger.error("Не найден файл credentials.txt с данными для входа. Завершение работы");
            System.exit(-1);
        }
        try{
            login=scanner.nextLine().trim();
            password=scanner.nextLine().trim();
            rootLogger.info(login);
            rootLogger.info(password);
        }catch (NoSuchElementException ex){
            rootLogger.error("Не найдены данные для входа. Завершение работы.");
            System.exit(-1);
        }
        DatabaseConnection databaseConnection = new DatabaseConnection(jdbcHeliosURL, login, password);
        try {
            dbConnection = databaseConnection.connectToDatabase();
            rootLogger.info("Соединение с бд установлено");
        }catch (SQLException ex){
            rootLogger.error("Соединение с бд не установлено. Завершение работы сервера");
            rootLogger.error(ex.getMessage());
            rootLogger.error("Завершение туть");
            System.exit(-1);
        }
    }
}
