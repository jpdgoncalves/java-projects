package jpdgoncalves.iotdatasim.emitters;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import jpdgoncalves.iotdatasim.base.SensorSimulator;
import jpdgoncalves.iotdatasim.base.SensorTicker;
import jpdgoncalves.iotdatasim.sensor.DefaultHumiditySensor;
import jpdgoncalves.iotdatasim.serializers.DoubleSerializer;
import jpdgoncalves.iotdatasim.serializers.KafkaSerializer;

public class KafkaEmitterTest {

    @Test
    @Timeout(20)
    public void testEmitter() throws InterruptedException {

        final long period = 1000;
        final String topic = "humidity";

        SensorSimulator<Double> sensor = new DefaultHumiditySensor(1289073459497L);
        SensorTicker ticker = new SensorTicker(period);

        Serializer<Double> valueSerializer = new KafkaSerializer<>(new DoubleSerializer());
        MockProducer<String, Double> mockProducer = new MockProducer<>(true, new StringSerializer(), valueSerializer);
        KafkaEmitter<Double> emitter = new KafkaEmitter<>(period, topic, sensor, mockProducer);

        ticker.addSensor(sensor);
        ticker.start();
        emitter.start();

        Thread.sleep(period * 10);

        emitter.interrupt();
        ticker.interrupt();

        emitter.join(2000);
        ticker.join(2000);

        assertTrue(mockProducer.history().size() > 9);
    }
}
