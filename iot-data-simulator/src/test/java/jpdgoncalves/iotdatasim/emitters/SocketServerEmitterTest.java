package jpdgoncalves.iotdatasim.emitters;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import jpdgoncalves.iotdatasim.base.DataProducer;
import jpdgoncalves.iotdatasim.base.SensorSimulator;
import jpdgoncalves.iotdatasim.base.Serializer;
import jpdgoncalves.iotdatasim.internals.SocketServerThread;
import jpdgoncalves.iotdatasim.sensor.DefaultTempSensor;
import jpdgoncalves.iotdatasim.serializers.DoubleSerializer;

public class SocketServerEmitterTest {

    @Test
    @Timeout(20)
    public void testSocketServerEmitter() throws IOException, InterruptedException {
        long period = 1000;
        long seed = 298587092859L;
        int port = 10002;

        SensorSimulator<Double> sensor = new DefaultTempSensor(seed);
        Serializer<Double> serializer = new DoubleSerializer();
        SocketServerThread socketServerThread = new SocketServerThread(port);
        SocketEmitter<Double> emitter = new SocketEmitter<>(socketServerThread, serializer);
        DataProducer<Double> producer = new DataProducer<>(period, sensor, emitter);

        producer.start();
        Socket client = new Socket("localhost", port);
        InputStream in = client.getInputStream();

        for (int i = 1; i <= 10; i++) {
            double measure = serializer.read(in);
            System.out.println("Measure " + i + "=" + measure);
        }

        client.close();
        producer.stop();
        socketServerThread.interrupt();
        socketServerThread.join();
    }
}
