package utilites;

import commands.AbstractCommand;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class RequestProcessingThread extends Thread {
    CommandExecutor executor;
    Container command;
    private InetAddress address;
    private int port;
    private DatagramSocket socket;
    private CollectionManager collectionManager;

    public RequestProcessingThread(CommandExecutor executor, Container command, InetAddress address, int port, DatagramSocket socket, CollectionManager collectionManager) {
        this.address = address;
        this.command = command;
        this.executor = executor;
        this.port = port;
        this.socket = socket;
        this.collectionManager = collectionManager;
    }

    @Override
    public void run() {
        new Replier().send(socket, new Container(executor.executeCommand((AbstractCommand) command.getObject(), collectionManager), command.getAddress()), address, port);

    }

}
