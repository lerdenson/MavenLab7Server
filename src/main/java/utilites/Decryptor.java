package utilites;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Decryptor {
    private final Logger logger = (Logger) LoggerFactory.getLogger(Decryptor.class);
    DatagramSocket socket;
    InetAddress address;
    Container cmd;
    private int port;

    public Decryptor(int port, DatabaseUserManager databaseUserManager, DatabaseCollectionManager databaseCollectionManager) {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

        try {
            socket = new DatagramSocket(port);
            logger.info("Server established, port is" + socket.getLocalPort() + " - {}", Decryptor.class.getSimpleName());
            CollectionManager collectionManager = new CollectionManager(databaseCollectionManager, databaseUserManager);
            while (true) {
                try {
                    if (!cachedThreadPool.submit(() -> {
                        try {
                            cmd = receive();
                            return true;
                        } catch (ClassNotFoundException | IOException e) {
                            logger.warn("I/O exception occurred - {}", Decryptor.class.getSimpleName());
                            e.printStackTrace();
                        }
                        return false;
                    }).get()) break;
                    CommandExecutor executor = new CommandExecutor();
                    new RequestProcessingThread(executor, cmd, address, this.port, socket, collectionManager).start();
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println("При чтении запроса произошла ошибка многопоточности!");
                }
            }
        } catch (SocketException e) {
            System.out.println("Произошла ошибка при работе с сокетом!");
        }
    }


    private Container receive() throws IOException, ClassNotFoundException {
        byte[] getBuffer = new byte[socket.getReceiveBufferSize()];
        DatagramPacket getPacket = new DatagramPacket(getBuffer, getBuffer.length);
        socket.receive(getPacket);
        address = getPacket.getAddress();
        port = getPacket.getPort();
        return deserialize(getPacket);
    }

    private Container deserialize(DatagramPacket getPacket) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(getPacket.getData());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Container container = (Container) objectInputStream.readObject();
        byteArrayInputStream.close();
        objectInputStream.close();
        return container;
    }


}
