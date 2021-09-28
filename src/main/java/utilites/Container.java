package utilites;

import java.io.Serializable;
import java.net.SocketAddress;

public class Container implements Serializable {
    private final Object object;
    private final SocketAddress address;

    public Container(Object object, SocketAddress address) {
        this.object = object;
        this.address = address;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public Object getObject() {
        return object;
    }
}