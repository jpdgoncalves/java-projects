package jpdgoncalves.iotdatasim.emitters;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import jpdgoncalves.iotdatasim.base.SensorSimulator;
import jpdgoncalves.iotdatasim.base.SensorTicker;
import jpdgoncalves.iotdatasim.sensor.DefaultTempSensor;
import jpdgoncalves.iotdatasim.serializers.DoubleSerializer;

public class DefaultSocketServerEmitterTest {

    @Test
    @Timeout(30)
    void testRun() throws UnknownHostException, IOException, InterruptedException {
        int port = 12000;
        long period = 1000;
        SensorSimulator<Double> sensor = new DefaultTempSensor(1289073459497L);
        SensorTicker ticker = new SensorTicker(1000);
        DoubleSerializer serializer = new DoubleSerializer();
        DefaultSocketServerEmitter<Double> emitter = new DefaultSocketServerEmitter<>(port, period, sensor, serializer);
        
        Thread emitterThread = new Thread(emitter, "Emitter");

        ticker.addSensor(sensor);
        ticker.start();
        emitterThread.start();

        Thread.sleep(5);
        Socket client = new Socket("localhost", port);
        InputStream cStream = client.getInputStream();

        for (int i = 0; i < 10; i++) {
            double measure = serializer.read(cStream);
            System.out.println("Read measure " + i + ": " + measure);
        }

        ticker.interrupt();
        emitterThread.interrupt();

        ticker.join(2000);
        emitterThread.join(2000);

        client.close();
    }
}
