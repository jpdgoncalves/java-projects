package jpdgoncalves.iotdatasim.emitters;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import jpdgoncalves.iotdatasim.base.SensorSimulator;
import jpdgoncalves.iotdatasim.internals.Ticker;

/**
 * Emitter that periodically sends records through
 * a kafka producer.
 * 
 * @param <T> The type of data the emitter produces.
 */
public class DeprecatedKafkaEmitter<T> {

    private final String topic;
    private final Ticker tickerThread;

    private final SensorSimulator<T> sensor;
    private final Producer<String, T> producer;

    /**
     * Create the kafka emitter.
     * @param period
     * @param topic
     * @param sensor
     * @param producer
     */
    public DeprecatedKafkaEmitter(long period, String topic, SensorSimulator<T> sensor, Producer<String, T> producer) {
        tickerThread = new Ticker(period);
        this.topic = topic;
        this.sensor = sensor;
        this.producer = producer;

        tickerThread.addTickFn(sensor::tick);
        tickerThread.addTickFn(this::emit);
        tickerThread.start();
    }

    /**
     * Stops the emitter from producing anymore data.
     */
    public void stop() {
        tickerThread.interrupt();

        try {
            tickerThread.join();
        } catch (InterruptedException e) {
            /** Ignore this exception. */
        }
    }

    private void emit() {
        ProducerRecord<String, T> record = new ProducerRecord<String, T>(topic, sensor.readValue());
        producer.send(record);
    }
}