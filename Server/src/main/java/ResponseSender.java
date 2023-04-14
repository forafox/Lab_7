import com.google.common.primitives.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 25.03.2023 18:12
 */
public class ResponseSender implements Runnable{
    private static final Logger rootLogger = LogManager.getRootLogger();

    private final int PACKET_SIZE = 1024;
    private final int DATA_SIZE = PACKET_SIZE - 1;
    private final DatagramSocket serverSocket;
    private InetAddress receiverAddress;
    private int receiverPort;
    private String strData;
    private byte[] byteArr;

    /**
     * Конструктор класса
     * @param serverSocket
     */
    public ResponseSender(DatagramSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    public ResponseSender(DatagramSocket serverSocket, InetAddress receiverAddress, int receiverPort, byte[] byteArr ) {
        this.serverSocket = serverSocket;
        this.receiverAddress = receiverAddress;
        this.receiverPort = receiverPort;
        this.byteArr=byteArr;
    }
//    @Override
//    public void run()  {
//
//        byte[] data = strData.getBytes(StandardCharsets.UTF_8);
//        byte[][] ret = new byte[(int)Math.ceil(data.length / (double)DATA_SIZE)][DATA_SIZE];
//
//        int start = 0;
//        for(int i = 0; i < ret.length; i++) {
//            ret[i] = Arrays.copyOfRange(data, start, start + DATA_SIZE);
//            start += DATA_SIZE;
//        }
//
//        rootLogger.info("Отправляется " + ret.length + " чанков...");
//
//        for(int i = 0; i < ret.length; i++) {
//            try {
//                var chunk = ret[i];
//                if (i == ret.length - 1) {
//                    var lastChunk = Bytes.concat(chunk, new byte[]{1});
//                    var dp = new DatagramPacket(lastChunk, PACKET_SIZE, receiverAddress, receiverPort);
//                    serverSocket.send(dp);
//                    rootLogger.info("Последний чанк размером " + chunk.length + " отправлен на сервер.");
//                } else {
//                    var dp = new DatagramPacket(ByteBuffer.allocate(PACKET_SIZE).put(chunk).array(), PACKET_SIZE, receiverAddress, receiverPort);
//                    serverSocket.send(dp);
//                    rootLogger.info("Чанк размером " + chunk.length + " отправлен на сервер.");
//                }
//            }catch (IOException ex){
//                rootLogger.warn("Произошла ошибка при отправке: "+ex.getMessage());
//            }
//        }
//
//        rootLogger.info("Отправка данных завершена");
//    }

    public void sendData(String str, SocketAddress addr) throws IOException {
        byte[] data = str.getBytes(StandardCharsets.UTF_8);

        byte[][] ret = new byte[(int)Math.ceil(data.length / (double)DATA_SIZE)][DATA_SIZE];

        int start = 0;
        for(int i = 0; i < ret.length; i++) {
            ret[i] = Arrays.copyOfRange(data, start, start + DATA_SIZE);
            start += DATA_SIZE;
        }

        rootLogger.info("Отправляется " + ret.length + " чанков...");

        for(int i = 0; i < ret.length; i++) {
            var chunk = ret[i];
            if (i == ret.length - 1) {
                var lastChunk = Bytes.concat(chunk, new byte[]{1});
                var dp = new DatagramPacket(lastChunk, PACKET_SIZE, addr);
                serverSocket.send(dp);
                rootLogger.info("Последний чанк размером " + chunk.length + " отправлен на сервер.");
            } else {
                var dp = new DatagramPacket(ByteBuffer.allocate(PACKET_SIZE).put(chunk).array(), PACKET_SIZE, addr);
                serverSocket.send(dp);
                rootLogger.info("Чанк размером " + chunk.length + " отправлен на сервер.");
            }
        }

        rootLogger.info("Отправка данных завершена");
    }

    @Override
    public void run() {
        if (byteArr.length > 4096) {
            rootLogger.warn("Размер пакета превышает допустимый. Разделить пакеты пока не представляется возможным");
        } else {
            byte[] byteUdp = new byte[byteArr.length];
            System.arraycopy(byteArr, 0, byteUdp, 0, byteUdp.length);
            DatagramPacket dp = new DatagramPacket(byteUdp, byteUdp.length, receiverAddress, receiverPort);
            try {
                serverSocket.send(dp);
            } catch (IOException exception) {
                rootLogger.warn("Произошла ошибка при отправке: " + exception.getMessage());
            }
        }
        //rootLogger.info("ResponseSender say: "+Thread.currentThread().getName() +" Правильно отработал");
    }

    public DatagramSocket getServerSocket() {
        return serverSocket;
    }
}
