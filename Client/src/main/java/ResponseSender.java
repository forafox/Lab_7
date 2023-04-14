
import database.UserData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Base64;

/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 18.03.2023 21:56
 */

public class ResponseSender {
    private static final Logger rootLogger = LogManager.getRootLogger();
    private final int PACKET_SIZE=1024;
    private final int DATA_SIZE=PACKET_SIZE-1;
    private final DatagramChannel clientChannel;
    public ResponseSender(DatagramChannel clientChannel){
        this.clientChannel=clientChannel;
    }

//    public void sendUserDataOLD(UserData userData, InetSocketAddress inetSocketAddress) throws IOException {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();//Конструктор создает буфер в памяти в 32 байта.
//        ObjectOutputStream oos = new ObjectOutputStream(baos);
//        oos.writeObject(userData);
//
//        byte[] data=baos.toByteArray();
//        byte[][] ret = new byte[(int)Math.ceil(data.length / (double)DATA_SIZE)][DATA_SIZE];
//
//        int start = 0;
//        for(int i = 0; i < ret.length; i++) {
//            ret[i] = Arrays.copyOfRange(data, start, start + DATA_SIZE);
//            start += DATA_SIZE;
//        }
//        rootLogger.info("Отправляется " + ret.length + " чанков...");
//
//        for(int i = 0; i < ret.length; i++) {
//            byte[] chunk = ret[i];
//            rootLogger.info("Адрес для отправки: " + inetSocketAddress.toString());
//            if (i == ret.length - 1) {
//                var lastChunk = Bytes.concat(chunk, new byte[]{1});
//                clientChannel.send(ByteBuffer.wrap(lastChunk), inetSocketAddress);
//                rootLogger.info("Последний чанк размером " + lastChunk.length + " отправлен на сервер.");
//            } else {
//                var answer = Bytes.concat(chunk, new byte[]{0});
//                clientChannel.send(ByteBuffer.wrap(answer), inetSocketAddress);
//                rootLogger.info("Чанк размером " + answer.length + " отправлен на сервер.");
//            }
//        }
//
//        rootLogger.info("Отправка данных завершена.");
//    }

    /**
     * Норм штука
     * @param userData
     * @param inetSocketAddress
     * @throws IOException
     */
    public void sendUserData(UserData userData, InetSocketAddress inetSocketAddress) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        byteBuffer.clear();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(userData);

        byteBuffer.put(Base64.getEncoder().withoutPadding().encode(baos.toByteArray()));

        byteBuffer.flip();

        clientChannel.send(byteBuffer, inetSocketAddress);
    }
}
