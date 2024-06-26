/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 18.03.2023 21:55
 */


import commands.CommandInvoker;
import database.UserData;
import io.UserIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;


/**
 * Класс, через который производится запуск данного приложения
 */
public class Application {
    private static final Logger rootLogger =  LogManager.getRootLogger();
    private static UserIO userIO;
    CommandInvoker commandInvoker;
    private InetSocketAddress inetSocketAddress;
    private UserData userData;
    private InetAddress inetAddress;


    /**
     * Конструктор.
     * @param port номер порта .
     */
    public Application(Integer port){
        try {
            inetSocketAddress = new InetSocketAddress("localhost", port);
        } catch (IllegalArgumentException ex) {
            rootLogger.error("Указано недопустимое значение порта. Значение должно быть в интервале 0-65335.");
            System.exit(-1);
        }
        userIO = new UserIO();
        commandInvoker = new CommandInvoker(userIO);
        rootLogger.info("Конструктор класса Application был загружен.");
    }
    /**
     * Метод, выполняющий запуск программы. Через него происходит работа всей программы
     */
    public void start() {

        try {

            ClientConnection clientConnection = new ClientConnection();//Создаем экзепляр нашего класса
            clientConnection.connect(inetSocketAddress);//Явное подключение

            ResponseSender responseSender = new ResponseSender(clientConnection.getClientChannel());
            RequestReader requestReader = new RequestReader(clientConnection.getClientChannel());

            CommandProcessor commandProcessor = new CommandProcessor(commandInvoker);

            rootLogger.info("Клиент готов ко входу");
            int count = 5;
            while (count > 0) {
                boolean isConnected = authenticate(responseSender, requestReader);
                userData.setIsConnected(isConnected);
                if (userData.getIsConnected()) {
                    rootLogger.info("Авторизация пользователя прошла успешно.");
                    System.out.printf("Welcome to Lab_7, %s!\n",userData.getLogin());
                    System.out.print("Type \"help\" to get a list of commands\n");
                } else {
                    rootLogger.warn("Неверный логин или пароль. Повторите ввод. Осталось попыток:" + --count);
                    continue;
                }
                rootLogger.info("Клиент готов к чтению команд.");
                this.cycle(responseSender, requestReader, commandProcessor);
            }
            if (count == 0) {
                rootLogger.error("Попытки исчерпаны. Завершение работы.");
                System.exit(-1);
            }
            this.cycle(responseSender, requestReader, commandProcessor);

        } catch (NoSuchElementException ex) {
            rootLogger.error("\nАварийное завершение работы.");
        } catch (PortUnreachableException ex) {
            rootLogger.error("Ошибка подключения сокета к порту, или сокет не может быть открыт. port:"
                    + inetSocketAddress.getPort());
        } catch (IOException ex) {
            rootLogger.error("Ошибка ввода-вывода");
            ex.printStackTrace();
        } catch (Exception ex) {
            rootLogger.error("Неизвестная ошибка. Следует починить." + ex);
            ex.printStackTrace();
        }
    }

    private boolean authenticate(ResponseSender responseSender, RequestReader requestReader) throws IOException, InterruptedException, NoSuchElementException {
        Scanner scanner = new Scanner(System.in);
        Boolean isNewUser = null;
        System.out.println("Выберите действие:\n1) Авторизоваться\n2) Создать нового пользователя");

        while (isNewUser == null) {
            try {
                int result = Integer.parseInt(scanner.nextLine().trim());

                switch (result) {
                    case 1:
                        isNewUser = false;
                        break;
                    case 2:
                        isNewUser = true;
                        break;
                    default:
                        rootLogger.info("Действие не распознано. Выберите способ еще раз.");
                        break;
                }
            } catch (NumberFormatException ex) {
                rootLogger.warn("Введите целое число.");
            }
        }
        userData = new UserData(isNewUser);
        String login;
        String password = null;

        System.out.println("Введите логин:");
  //      login = scanner.next().trim();
        login="Andrey";
        System.out.println("Введите пароль:");
    //    Console console = System.console();
        password="12345";
//        while (password == null || password.equals("")) {
//            if (console != null) {
//                char[] arrPass = console.readPassword();
//                if (arrPass == null) {
//                    rootLogger.warn("Пароль не может быть пустым. Повторите попытку");
//                } else password = String.valueOf(arrPass);
//            } else {
//                password = scanner.next().trim();
//            }
//        }
        userData.setLogin(login);
        userData.setPassword(DataEncryptor.sha256(password));

        responseSender.sendUserDataOLD(userData,inetSocketAddress);
        ByteBuffer byteBufferOLD =requestReader.receiveBufferDataOLD();
        ByteArrayInputStream baisOLD = new ByteArrayInputStream(byteBufferOLD.array(), 0, byteBufferOLD.limit());
        ObjectInputStream oisOLD = new ObjectInputStream(baisOLD);
        return oisOLD.readBoolean();
    }
    public void cycle(ResponseSender responseSender, RequestReader requestReader, CommandProcessor commandProcessor) throws IOException, InterruptedException {
        boolean isConnected = true;
        boolean isNeedInput = true;
        boolean isCommandAcceptable = false;

        String line;
        while (isConnected) {
            if (isNeedInput) {
                System.out.println("Введите название команды:");
                userIO.printPreamble();
                line = userIO.readLine();
                isCommandAcceptable = commandProcessor.executeCommand(line, userData);
            }
            try {
                if (isCommandAcceptable) {

                    userData.setCommandContainer(commandInvoker.getLastCommandContainer());
                    responseSender.sendUserDataOLD(userData,inetSocketAddress);

                    rootLogger.info("Данные были отправлены.");
                    ByteBuffer byteBuffer =requestReader.receiveBufferDataOLD();
                    rootLogger.info("Данные были получены.");
                    System.out.println(new String(byteBuffer.array()).trim() + "\n");

                    isNeedInput = true;
                }
            } catch (PortUnreachableException | SocketTimeoutException ex) {
                if (ex instanceof PortUnreachableException) {
                    rootLogger.warn("Порт " + inetSocketAddress.getPort() + " не доступен. Повторить отправку команды? y/n");
                } else {
                    rootLogger.warn("Сервер не отвечает. Повторить отправку команды? y/n");
                }
                String result = userIO.readLine().trim().toLowerCase(Locale.ROOT).split("\\s+")[0];
                if (result.equals("n")) {
                    rootLogger.info("Завершение работы клиента");
                    isConnected = false;
                } else {
                    isNeedInput = false;
                }
            }
        }
    }
}
