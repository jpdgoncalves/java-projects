package jpdgoncalves.iotdatasim.emitters;

import java.nio.ByteBuffer;

import jpdgoncalves.iotdatasim.base.Emitter;
import jpdgoncalves.iotdatasim.base.Serializer;
import jpdgoncalves.iotdatasim.internals.SocketServerThread;

/**
 * An emitter that sends data through sockets.
 * It uses the format of ["SIZE OF DATA" BYTES] + ["DATA" BYTES].
 * 
 * @param <T> The type of data that is sent.
 */
public class SocketEmitter<T> implements Emitter<T> {

    private final SocketServerThread serverThread;
    private final Serializer<T> serializer;
    
    /**
     * Create a new Socket Emitter.
     * @param serverThread Internal implementation of the socket server.
     * @param serializer Serializer that converts data to a sequence of bytes.
     */
    public SocketEmitter(SocketServerThread serverThread, Serializer<T> serializer) {
        this.serverThread = serverThread;
        this.serializer = serializer;
    }

    @Override
    public void emit(T data) {
        byte[] serialized = serializer.serialize(data);
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + serialized.length);
        buffer.putInt(serialized.length);
        buffer.put(serialized);
        serverThread.tryBroadcast(buffer.array());
    }

}
