package jpdgoncalves.iotdatasim.emitters;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import jpdgoncalves.iotdatasim.base.SensorSimulator;
import jpdgoncalves.iotdatasim.base.Serializer;
import jpdgoncalves.iotdatasim.internals.Ticker;

/**
 * Emitter that pushes data into
 * an MQTT endpoint for a specific topic
 * 
 * @param <T> Type of data produced.
 */
public class DeprecatedMQTTEmitter<T> {
    
    private final IMqttClient client;
    private final String topic;
    private final Ticker tickerThread;
    private final SensorSimulator<T> sensor;
    private final Serializer<T> serializer;

    /**
     * Creates an Emitter that will periodically produce
     * data to the specified MQTT server.
     * @param client Client through which data is sent. It is assumed it is connected.
     * @param topic The topic to send the data to.
     * @param sensor The source of the data.
     * @param serializer The serializer that converts the data to bytes.
     */
    public DeprecatedMQTTEmitter(IMqttClient client, String topic, long period, SensorSimulator<T> sensor, Serializer<T> serializer) {
        this.client = client;
        this.topic = topic;
        this.tickerThread = new Ticker(period);
        this.sensor = sensor;
        this.serializer = serializer;

        tickerThread.addTickFn(sensor::tick);
        tickerThread.addTickFn(this::emit);
        tickerThread.start();
    }

    /**
     * Stops the emitter from sending any more data.
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
        byte[] payload = serializer.serialize(sensor.readValue());
        try {
            client.publish(topic, payload, 0, false);
        } catch (MqttException e) {
            /** Print the exception if it occurs */
            e.printStackTrace();
        }
    }
}
