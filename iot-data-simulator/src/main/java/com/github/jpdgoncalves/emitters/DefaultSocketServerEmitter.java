package com.github.jpdgoncalves.emitters;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.github.jpdgoncalves.base.DataEmitter;
import com.github.jpdgoncalves.base.SensorSimulator;

public class DefaultSocketServerEmitter extends DataEmitter<Double> {

    private int port;

    public DefaultSocketServerEmitter(int port) {
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
            SensorSimulator<Double> sensor;
            try {
                sensor = getSensor();
            } catch (NullPointerException e) {
                System.err.println(e.getMessage());
                return;
            }

            try {
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(1000);
                    out.writeDouble(sensor.generateNextValue());
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
