package com.github.jpdgoncalves.emitters;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import com.github.jpdgoncalves.base.DataEmitter;
import com.github.jpdgoncalves.base.SensorSimulator;
import com.github.jpdgoncalves.base.Serializer;

/**
 * Default data emitter that uses a simple socket server
 * mechanism. The server is instantiated and then await
 * for client connections. Once a connection is received
 * it will write one data measure for each client per 
 * second
 * @param <T> The type of data this server emits
 */
public class DefaultSocketServerEmitter<T> extends DataEmitter<T> {

    private int port;
    private ReentrantLock lock = new ReentrantLock();

    /**
     * Instantiate a default data emitter which
     * will listen for connections at the specified
     * port and produce the specified data that sourced
     * from the provided sensor.
     * @param port The port to listen to client connections.
     * @param sensor The sensor that produces the data.
     * @param serializer The serializer that encodes the data.
     */
    public DefaultSocketServerEmitter(int port, SensorSimulator<T> sensor, Serializer<T> serializer) {
        super(sensor, serializer);
        this.port = port;
    }

    @Override
    public void run() {
        // Open a new socket server
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }

        // Sensor Data stream block
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                SocketHandler handler = new SocketHandler(socket);
                Thread thread = new Thread(handler::handle);
                thread.setDaemon(true);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        // Clean up
        try {
            serverSocket.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }
    }

    private class SocketHandler {

        private Socket socket;

        public SocketHandler(Socket socket) {
            this.socket = socket;
        }

        public void handle() {
            // Get the sensor simulator
            SensorSimulator<T> sensor = getSensor();
            Serializer<T> serializer = getSerializer();

            try {
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(1000);

                    T data;
                    DefaultSocketServerEmitter.this.lock.lockInterruptibly();
                    data = sensor.generateNextValue();
                    DefaultSocketServerEmitter.this.lock.unlock();
                    
                    out.write(serializer.serialize(data));
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }

            try {
                socket.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
