package utilites;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Replier {
    private final Logger logger = (Logger) LoggerFactory.getLogger(Replier.class);

    public void send(DatagramSocket socket, Container container, InetAddress address, int port) {
        try {
            byte[] sendBuffer = serialize(container);
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, port);
            logger.debug("Message is wrapped - Replier.send()");
            socket.send(sendPacket);
            logger.info("Message is sent - Replier.send()");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private byte[] serialize(Container container) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(container);
        byte[] buffer = byteArrayOutputStream.toByteArray();
        objectOutputStream.flush();
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
        objectOutputStream.close();
        return buffer;
    }

}
