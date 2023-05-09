import database.UserData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;

/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 04.04.2023 0:24
 */
public class Server {
    private final String ADMIN_CHECK_PASSWORD="15";
    private static final Logger rootLogger = LogManager.getRootLogger();
    private final RequestReader requestReader;
    private final ResponseSender responseSender;
    private final CommandProcessor commandProcessor;
    private final ArrayList<UserData> requestList = new ArrayList<>();
    private final ArrayList<UserData> commandList = new ArrayList<>();
    private final HashMap<UserData, ByteArrayOutputStream> responseMap = new HashMap<>();
    //Fixed Thread Poop для многопоточной отправки ответов
    ExecutorService serviceResponse = Executors.newFixedThreadPool(2);//Для отправки ответов
    //Fork Join Pool для многопоточного чтения запросов
    private final ForkJoinPool forkJoinPool = new ForkJoinPool(2);
    /**
     * Конструктор класса
     * @param rr
     * @param rs
     * @param cp
     */
    public Server(RequestReader rr, ResponseSender rs, CommandProcessor cp) {
        this.requestReader = rr;
        this.responseSender = rs;
        this.commandProcessor = cp;
    }

    public void startWork() {
        while (true) {
            ForkJoinTask<UserData> request1 = forkJoinPool.submit(requestReader);
                try {
                    requestList.add(request1.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

            UserData[] requestArray = requestList.toArray(new UserData[0]);//массив пользователей
            //здесь мы всех регаем или смотрим подключение
            for (UserData userData : requestArray) {
                this.findNextStep(userData);
            }

            UserData[] commandArray = commandList.toArray(new UserData[0]);
            ArrayList<Thread> commandThread = new ArrayList<>();
            //начало обработки комманд для каждого пользователя
            for (UserData userData : commandArray) {
                commandThread.add(this.startExecution(userData));
            }
            //Последовательная работа всех вызванных потоков для соблюдения порядка
            for (Thread thread : commandThread) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //Подготовка отправки ответов
            UserData[] responseKeys;
            ByteArrayOutputStream[] responseValues;

            responseKeys = responseMap.keySet().toArray(new UserData[0]);
            responseValues = responseMap.values().toArray(new ByteArrayOutputStream[0]);

            //Отправка всех ответов.
            for (int i = 0; i < responseKeys.length; i++) {
                this.sendResponse(responseKeys[i],responseValues[i]);
            }
        }
    }

    /**
     * Метод отвечающий за авторизацию и подключение пользователя, создание нового пользователя.
     * @param userData
     */
    private void findNextStep(UserData userData) {
            requestList.remove(userData);

        if (!userData.getIsConnected()) {
            if (userData.getIsNewUser()) {
                if(userData.getAdminCheckPassword()==null || userData.getAdminCheckPassword().equals(ADMIN_CHECK_PASSWORD)){
                        commandProcessor.getUdh().addUser(userData);
                    }
            }
            if (!userData.getIsConnected()) {
                commandProcessor.getUdh().verifyUser(userData);
            }
            if (!userData.getIsNewUser()) {
                commandProcessor.getUdh().ConnectUser(userData);
            }
        }
            commandList.add(userData);

    }

    private Thread startExecution(UserData userData) {
        rootLogger.info("Обработка запроса для " + userData.getPort() + " началась.");
        commandList.remove(userData);

        CommandExecutor commandExecutor = new CommandExecutor(commandProcessor, userData, responseMap);
        //Новый поток для обработки полученного запроса
        Thread t = new Thread(commandExecutor);
        t.start();
        return t;
    }

    private void sendResponse(UserData userData, ByteArrayOutputStream baos) {
        responseMap.remove(userData, baos);

        rootLogger.info("Отправка для " + userData.getPort() + " началась.");
        //Новый поток для отправки ответа
        serviceResponse.submit(new ResponseSender(responseSender.getServerSocket(), userData.getInetAddress(), userData.getPort(), baos.toByteArray()));
    }
}
