package jpdgoncalves.iotdatasim.emitters;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

import jpdgoncalves.iotdatasim.base.DataEmitter;
import jpdgoncalves.iotdatasim.base.SensorSimulator;
import jpdgoncalves.iotdatasim.base.Serializer;

/**
 * Default data emitter that uses a simple socket server
 * mechanism. The server is instantiated and then await
 * for client connections. Once a connection is received
 * it will write one data measure for each client per
 * second
 * 
 * @param <T> The type of data this server emits
 */
public class DefaultSocketServerEmitter<T> extends DataEmitter<T> {

    private static final int ACCEPT_TIMEOUT = 1000 * 10;

    private int port;
    private CopyOnWriteArrayList<ClientConnection> clientConnections = new CopyOnWriteArrayList<>();

    /**
     * Instantiate a default data emitter which
     * will listen for connections at the specified
     * port and produce the specified data that sourced
     * from the provided sensor.
     * 
     * @param port       The port to listen to client connections.
     * @param sensor     The sensor that produces the data.
     * @param serializer The serializer that encodes the data.
     */
    public DefaultSocketServerEmitter(int port, SensorSimulator<T> sensor, Serializer<T> serializer)
            throws IllegalArgumentException {
        super(sensor, serializer);
        if (0 > port || port > 65535)
            throw new IllegalArgumentException("Port number must be bigger than 0 and lower or equal to 65535");
        this.port = port;
    }

    /**
     * Start the socket server data emitter.
     */
    @Override
    public void run() {
        ServerSocket server = null;
        Thread dataEmitter = new Thread(this::emitDataPeriodically);

        // Start the socket server
        try {
            server = new ServerSocket(port);
            server.setSoTimeout(ACCEPT_TIMEOUT);
        } catch (IOException e) {
            System.err.println(e);
        } catch (SecurityException e) {
            System.err.println(e);
        }

        // Configure and start data emitter
        dataEmitter.setDaemon(true);
        dataEmitter.start();

        // Start accepting and cleaning up connections
        while (!Thread.interrupted()) {
            Socket conn = null;
            try {
                conn = server.accept();
            } catch (IOException e) {
                // Fatal error, break out of the loop
                System.err.println(e);
                break;
            }
            // Add the next connection.
            clientConnections.add(new ClientConnection(conn));
            // Close and clean up all the invalid connections.
            clientConnections.removeIf((ClientConnection e) -> {
                if (!e.isValid) {
                    try {
                        e.conn.close();
                    } catch (IOException except) {
                        // Log this just in case.
                        System.err.println(except);
                    }
                }

                return !e.isValid;
            });
        }

        // Stop the data emitter thread.
        try {
            dataEmitter.interrupt();
            dataEmitter.wait(2000);
        } catch (Exception e) {
            /* eat the exception. The thread is daemonic regardless */ }

        // Close client connections
        for (ClientConnection client : clientConnections) {
            try {
                client.conn.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }

        // Close server
        try {
            if (server != null)
                server.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }


    private void emitDataPeriodically() {
        SensorSimulator<T> sensor = getSensor();
        Serializer<T> serializer = getSerializer();

        try {
            while (!Thread.interrupted()) {
                Thread.sleep(1000);
                byte[] data = serializer.serialize(sensor.generateNextValue());

                for (ClientConnection client : clientConnections) {
                    // The emitter thread could be interrupted while sending data
                    if (Thread.interrupted())
                        break;
                    // Try send data to this client.
                    try {
                        OutputStream out = client.conn.getOutputStream();
                        out.write(data);
                    } catch (IOException e) {
                        // Socket was closed or is in an invalid state.
                        // Not a fatal error. Just mark this connection
                        // as invalid so the parent can clean it up.
                    }
                }
            }
        } catch (InterruptedException e) {
            // Thread was interrupted. Stop running.
            System.err.println(e);
            return;
        }
    }

    private class ClientConnection {
        public volatile boolean isValid = true;
        public final Socket conn;

        public ClientConnection(Socket conn) {
            this.conn = conn;
        }
    }
}
