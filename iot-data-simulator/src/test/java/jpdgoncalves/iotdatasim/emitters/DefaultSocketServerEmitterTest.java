package jpdgoncalves.iotdatasim.emitters;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import jpdgoncalves.iotdatasim.base.DataEmitter;
import jpdgoncalves.iotdatasim.base.SensorSimulator;
import jpdgoncalves.iotdatasim.base.SensorTicker;
import jpdgoncalves.iotdatasim.sensor.DefaultTempSensor;
import jpdgoncalves.iotdatasim.serializers.DoubleSerializer;

public class DefaultSocketServerEmitterTest {

    @Test
    @Timeout(30)
    void testRun() throws UnknownHostException, IOException, InterruptedException {
        int port = 12000;
        SensorSimulator<Double> sensor = new DefaultTempSensor(1289073459497L);
        SensorTicker ticker = new SensorTicker(1000);
        DoubleSerializer serializer = new DoubleSerializer();
        DataEmitter<Double> emitter = new DefaultSocketServerEmitter<>(port, sensor, serializer);
        
        Thread tickerThread = new Thread(ticker, "Ticker");
        Thread emitterThread = new Thread(emitter, "Emitter");

        ticker.addSensor(sensor);
        tickerThread.start();
        emitterThread.start();

        Thread.sleep(5);
        Socket client = new Socket("localhost", port);
        InputStream cStream = client.getInputStream();

        for (int i = 0; i < 10; i++) {
            double measure = serializer.deserialize(cStream.readNBytes(Double.BYTES));
            System.out.println("Read measure " + i + ": " + measure);
        }

        tickerThread.interrupt();
        emitterThread.interrupt();

        tickerThread.join(2000);
        emitterThread.join(2000);

        client.close();
    }
}
