package jpdgoncalves.iotdatasim.internals;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Internal class that setups a Thread that
 * creates a simple socket server. It handles
 * accepting new requests and cleanups connections
 * that are closed or in an invalid state.
 * 
 * It provides a single method that allows to
 * broadcast a message to all currently connected
 * clients. Any errors that arise are silently
 * handled and therefore it is not guaranted that
 * the message will be actually sent.
 */
public class SocketServerThread extends Thread {

    private final CopyOnWriteArrayList<Socket> connList = new CopyOnWriteArrayList<>();
    private final ServerSocket server;

    public SocketServerThread(int port) throws IOException {
        this.server = new ServerSocket(port);
    }

    @Override
    public void interrupt() {
        try {
            server.close();
        } catch (IOException e) {
            /** Shouldn't happen so we should print the exception if it does */
            e.printStackTrace();
        }
        super.interrupt();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Socket conn = server.accept();
                connList.add(conn);
            } catch (SocketException e) {
                /** The server was closed. This is expected so we should just quit the loop */
                break;
            } catch (IOException e) {
                /** Something went wrong here that shouldn't have so we print the exception */
                e.printStackTrace();
                break;
            }
        }
    }

    /**
     * Attempts to broadcast the provided message to
     * all of the connected clients.
     * @param msg The message to broadcast to all
     * clients.
     */
    public void tryBroadcast(byte[] msg) {
        for (Socket conn: connList) {
            if (conn.isClosed()) continue;

            try {
                // Try to write the message to the client
                OutputStream out = conn.getOutputStream();
                out.write(msg);
            } catch (IOException outErr) {
                // Something went wrong.
                // Try to close the connection and eat the exception
                // If it occurs.
                try {
                    conn.close();
                } catch (IOException closeErr) {}
                // Schedule a cleanup virtual thread.
                Thread.ofVirtual().start(this::cleanup);
            }
        }
    }

    private void cleanup() {
        connList.removeIf((Socket s) -> s.isClosed());
    }
}
