import com.google.common.primitives.Bytes;
import commands.abstr.CommandContainer;
import database.UserData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.Callable;

/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 25.03.2023 18:11
 */
public class RequestReader implements Callable<UserData> {
    private final int PACKET_SIZE = 1024;
    private final int DATA_SIZE = PACKET_SIZE - 1;
    private static final Logger rootLogger = LogManager.getRootLogger();

    private final DatagramSocket serverSocket;

    private byte[] byteUPD = new byte[4096];
    private final DatagramPacket dp;

    private SocketAddress addr;

    private CommandContainer commandContainer;

    /**
     * Конструктор класса с аргументом
     * Автоматическое создание DatagramPacket
     * @param serverSocket
     */
    public RequestReader(DatagramSocket serverSocket) {
        this.serverSocket = serverSocket;
        dp = new DatagramPacket(byteUPD, byteUPD.length);
    }
//    @Override
//    public UserData call() throws Exception {
//        var received = false;
//        var result = new byte[0];
//        SocketAddress addr = null;
//        ///
//        var data = new byte[PACKET_SIZE];
//        var dp = new DatagramPacket(data, PACKET_SIZE);
//
//        //
//        while(!received) {
//
//            serverSocket.receive(dp);
//            addr = dp.getSocketAddress();
//            rootLogger.info("Получено \"" + new String(data) + "\" от " + dp.getAddress());
//            rootLogger.info("Последний байт: " + data[data.length - 1]);
//
//            if (data[data.length - 1] == 1) {
//                received = true;
//                rootLogger.info("Получение данных от " + dp.getAddress() +","+dp.getPort() + " окончено");
//            }
//            result = Bytes.concat(result, Arrays.copyOf(data, data.length - 1));
//        }
//        ByteArrayInputStream bais = new ByteArrayInputStream(result);
//        ObjectInputStream ois = new ObjectInputStream(bais);
//
//        UserData userData=(UserData) ois.readObject();
//        userData.setInetAddress(dp.getAddress());
//        rootLogger.info("Порт перед установкой к пользователю равен"+ dp.getPort());
//        userData.setPort(dp.getPort());
//        return userData;
//    }
    @Override
    public UserData call() throws Exception {
        serverSocket.receive(dp);
        byteUPD = dp.getData();


        String str = new String(byteUPD);
        str = str.replace("\0", "");
        byte[] byteArr = str.getBytes(StandardCharsets.UTF_8);

        ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(byteArr));
        ObjectInputStream ois = new ObjectInputStream(bais);

        rootLogger.info("Получен пакет с командой от " + dp.getAddress().getHostAddress() + " " + dp.getPort());

        UserData userData = (UserData) ois.readObject();
        userData.setInetAddress(dp.getAddress());
        userData.setPort(dp.getPort());
        return userData;
    }
    public SocketAddress getSenderAddress() {
        return addr;
    }
    public CommandContainer getCommandContainer(){
        return commandContainer;
    }
}
