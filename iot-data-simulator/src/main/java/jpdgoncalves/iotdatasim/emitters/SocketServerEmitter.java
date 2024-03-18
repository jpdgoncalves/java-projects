package jpdgoncalves.iotdatasim.emitters;

import java.io.IOException;
import java.nio.ByteBuffer;

import jpdgoncalves.iotdatasim.base.SensorSimulator;
import jpdgoncalves.iotdatasim.base.Serializer;
import jpdgoncalves.iotdatasim.base.Ticker;
import jpdgoncalves.iotdatasim.internals.SocketServerThread;

/**
 * An emitter that works on sockets.
 * @param <T> The type of data that will be produced
 */
public class SocketServerEmitter<T> {

    private final SocketServerThread socketServerThread;
    private final Ticker tickerThread;
    private final SensorSimulator<T> sensor;
    private final Serializer<T> serializer;

    /**
     * Create an instance of a emitter that periodically
     * sends out data to clients connected to this server
     * through sockets.
     * 
     * @param period     The period in miliseconds.
     * @param port       Port of the socket server.
     * @param sensor     The sensor simulator that produces the values.
     * @param serializer Serializer that encodes
     *                   sensor data into bytes.
     * @throws IOException
     */
    public SocketServerEmitter(long period, int port, SensorSimulator<T> sensor, Serializer<T> serializer)
            throws IOException {
        socketServerThread = new SocketServerThread(port);
        tickerThread = new Ticker(period);
        this.sensor = sensor;
        this.serializer = serializer;

        tickerThread.addTickFn(sensor::tick);
        tickerThread.addTickFn(this::emit);
        tickerThread.start();
        socketServerThread.start();
    }

    /**
     * Stops the emitter from producing any more data.
     */
    public void stop() {
        socketServerThread.interrupt();
        tickerThread.interrupt();

        try {
            socketServerThread.join();
            tickerThread.join();
        } catch (InterruptedException e) {
            /** Ignore the exception. It shouldn't happen. */
        }
    }

    private void emit() {
        byte[] serialized = serializer.serialize(sensor.readValue());
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + serialized.length);
        buffer.putInt(serialized.length);
        buffer.put(serialized);
        socketServerThread.tryBroadcast(buffer.array());
    }
}
