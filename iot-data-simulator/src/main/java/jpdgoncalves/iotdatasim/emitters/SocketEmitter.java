package jpdgoncalves.iotdatasim.emitters;

import jpdgoncalves.iotdatasim.base.Emitter;
import jpdgoncalves.iotdatasim.base.Serializer;
import jpdgoncalves.iotdatasim.internals.SocketServerThread;

public class SocketEmitter<T> implements Emitter<T> {

    private final SocketServerThread serverThread;
    private final Serializer<T> serializer;
    
    public SocketEmitter(SocketServerThread serverThread, Serializer<T> serializer) {
        this.serverThread = serverThread;
        this.serializer = serializer;
    }

    @Override
    public void emit(T data) {
        serverThread.tryBroadcast(serializer.serialize(data));
    }

}
