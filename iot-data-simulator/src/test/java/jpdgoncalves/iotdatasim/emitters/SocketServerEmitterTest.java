package jpdgoncalves.iotdatasim.emitters;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import jpdgoncalves.iotdatasim.base.SensorSimulator;
import jpdgoncalves.iotdatasim.base.DeprecatedSerializer;
import jpdgoncalves.iotdatasim.sensor.DefaultTempSensor;
import jpdgoncalves.iotdatasim.serializers.DoubleSerializer;

public class SocketServerEmitterTest {

    @Test
    @Timeout(20)
    public void testSocketServerEmitter() throws IOException {
        long period = 1000;
        long seed = 298587092859L;
        int port = 10002;

        SensorSimulator<Double> sensor = new DefaultTempSensor(seed);
        DeprecatedSerializer<Double> serializer = new DoubleSerializer();
        SocketServerEmitter<Double> emitter = new SocketServerEmitter<>(period, port, sensor, serializer);

        Socket client = new Socket("localhost", port);
        InputStream in = client.getInputStream();

        for (int i = 1; i <= 10; i++) {
            double measure = serializer.read(in);
            System.out.println("Measure " + i + "=" + measure);
        }

        client.close();
        emitter.stop();
    }
}
