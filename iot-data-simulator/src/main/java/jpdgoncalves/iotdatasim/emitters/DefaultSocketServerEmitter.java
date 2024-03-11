package jpdgoncalves.iotdatasim.emitters;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

import jpdgoncalves.iotdatasim.base.SensorSimulator;
import jpdgoncalves.iotdatasim.base.Serializer;

/**
 * Default data emitter that uses a simple socket server
 * mechanism. The server is instantiated and then await
 * for client connections. Once a connection is received
 * it will write one data measure for each client
 * periodically.
 * 
 * @param <T> The type of data this server emits
 */
public class DefaultSocketServerEmitter<T> extends Thread {

    private final SensorSimulator<T> sensor;
    private final Serializer<T> serializer;
    private final long period;
    private final ServerSocket server;
    private final Thread emitterThread;
    private final Thread cleanerThread;
    private final CopyOnWriteArrayList<Socket> connections = new CopyOnWriteArrayList<>();

    /**
     * Instantiate a default data emitter which
     * will listen for connections at the specified
     * port and produce the specified data that sourced
     * from the provided sensor.
     * 
     * @param port       The port to listen to client connections.
     * @param period     How frequently is data sent to the clients.
     * @param sensor     The sensor that produces the data.
     * @param serializer The serializer that encodes the data.
     * @throws IOException
     */
    public DefaultSocketServerEmitter(int port, long period, SensorSimulator<T> sensor, Serializer<T> serializer)
            throws IllegalArgumentException, IOException {
        super();
        if (0 > port || port > 65535)
            throw new IllegalArgumentException("Port number must be bigger than 0 and lower or equal to 65535");
        if (period <= 0)
            throw new IllegalArgumentException("Period must be bigger than 0");
        this.sensor = sensor;
        this.serializer = serializer;
        this.period = period;
        this.server = new ServerSocket(port);

        this.emitterThread = new Thread(this::emitDataPeriodically);
        this.cleanerThread = new Thread(this::cleanClosedConnections);

        setDaemon(true);
        this.emitterThread.setDaemon(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void interrupt() {
        super.interrupt();
        closeAndIgnoreException(server);
    }

    /**
     * Method invoked when the Emitter thread is started.
     */
    @Override
    public void run() {
        // Start the emitter and cleanup thread.
        emitterThread.start();
        cleanerThread.start();

        try {
            // While this thread isn't interrupted
            // Accept connections and clean up old ones.
            while (!Thread.interrupted()) {
                Socket conn = server.accept();
                // Add the connection to the list.
                connections.add(conn);
            }
        } catch (IOException e) {
            /** Regardless of the exception that occured we want to cleanup to we just ignore it */
        } finally {
            cleanUp();
        }
    }

    private void cleanClosedConnections() {
        try {
            while (!Thread.interrupted()) {
                Thread.sleep(5 * 1000);
                connections.removeIf((Socket e) -> e.isClosed());
            }
        } catch (InterruptedException e) {
            /** Ignore this exception. The thread was interrupted. */
        }
    }

    private void emitDataPeriodically() {
        try {
            while (!Thread.interrupted()) {
                Thread.sleep(period);

                T value = sensor.readValue();

                for (Socket conn : connections) {
                    // The emitter thread could be interrupted while sending data
                    if (Thread.interrupted())
                        break;

                    // If the client connection is invalid
                    // just skip it
                    if (conn.isClosed())
                        continue;

                    // Try send data to this client.
                    try {
                        OutputStream out = conn.getOutputStream();
                        serializer.write(out, value);
                    } catch (IOException e) {
                        closeAndIgnoreException(conn);
                    }
                }
            }
        } catch (InterruptedException e) {
            // Thread was interrupted. Stop running.
            return;
        }
    }

    private void cleanUp() {
        // Stop the emitter and cleaner thread.
        emitterThread.interrupt();
        cleanerThread.interrupt();

        // Close client connections
        connections.forEach((Socket e) -> closeAndIgnoreException(e));

        // Close the server
        closeAndIgnoreException(server);
    }

    private void closeAndIgnoreException(Closeable conn) {
        try {
            conn.close();
        } catch (IOException e) {}
    }
}
