package jpdgoncalves.iotdatasim.emitters;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import jpdgoncalves.iotdatasim.base.SensorSimulator;

/**
 * Emitter that periodically sends records through
 * a kafka producer.
 * 
 * @param <T> The type of data the emitter produces.
 */
public class KafkaEmitter<T> extends Thread {

    private final SensorSimulator<T> sensor;
    private final Producer<String, T> producer;
    private final long period;
    private final String topic;

    /**
     * Creates a new Kafka Emitter thread.
     * @param period How frequently data is sent through the Producer.
     * @param topic The topic to where the data is sent.
     * @param sensor The sensor that produces that data.
     * @param producer The Kafka Producer to where the data is sent.
     */
    public KafkaEmitter(long period, String topic, SensorSimulator<T> sensor, Producer<String, T> producer) {
        if (period <= 0) throw new IllegalArgumentException("Period must be larger than 0");

        this.period = period;
        this.topic = topic;
        this.sensor = sensor;
        this.producer = producer;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                ProducerRecord<String, T> record = new ProducerRecord<String,T>(topic, sensor.readValue());
                producer.send(record);
                Thread.sleep(period);
            }
        } catch (InterruptedException e) {
            /* Ignore the exception */
        }
    }
}