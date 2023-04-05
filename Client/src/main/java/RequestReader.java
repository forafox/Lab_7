import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;

import com.google.common.primitives.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 18.03.2023 21:55
 */
public class RequestReader {
    private final int PACKET_SIZE = 1024;
    private final int DATA_SIZE = PACKET_SIZE - 1;
    private final DatagramChannel clientChannel;

    private static final Logger rootLogger = LogManager.getRootLogger();

    public RequestReader(DatagramChannel clientChannel) {
        this.clientChannel = clientChannel;
    }
    public byte[] receiveBufferData() throws IOException {
        int waitingTime = 0;
        var received = false;
        var result = new byte[0];
       // ByteBuffer buffer=ByteBuffer.wrap(result);
            while (!received) {
                var data = receiveBufferData(PACKET_SIZE);
                rootLogger.info("Получено \"" + new String(data) + "\"");
                rootLogger.info("Последний байт: " + data[data.length - 1]);

                if (data[data.length - 1] == 1) {
                    received = true;
                    rootLogger.info("Получение данных окончено");
                }
                result = Bytes.concat(result, Arrays.copyOf(data, data.length - 1));
                // buffer= ByteBuffer.wrap(result);
            }

            return result;
        // return buffer;
    }

    private byte[] receiveBufferData(int bufferSize) throws IOException {
        var buffer = ByteBuffer.allocate(bufferSize);
        int waitingTime = 0;

            SocketAddress address = null;
            while (address == null && waitingTime<1000) {
                address = clientChannel.receive(buffer);
            }
            if(address!=null){
                return buffer.array();
            }else{
                rootLogger.error("Сервер не отвечает. Завершение работы клиента");
                System.exit(0);
                return buffer.array();
            }
        }

    /**
     * Норм штука
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public ByteBuffer receiveBuffer() throws IOException, InterruptedException {

        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        int waitingTime = 0;

        while (waitingTime < 1000) {
            byteBuffer.clear();
            SocketAddress from = clientChannel.receive(byteBuffer);

            if (from != null) {
                byteBuffer.flip();
                return byteBuffer;
            }
            Thread.sleep(500);
            waitingTime++;
        }
        rootLogger.error("Сервер не отвечает. Завершение работы клиента");
        System.exit(0);
        return byteBuffer;
    }

}
