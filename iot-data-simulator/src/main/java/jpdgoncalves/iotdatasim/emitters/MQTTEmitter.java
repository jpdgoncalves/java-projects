package jpdgoncalves.iotdatasim.emitters;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import jpdgoncalves.iotdatasim.base.Emitter;
import jpdgoncalves.iotdatasim.base.Serializer;

public class MQTTEmitter<T> implements Emitter<T> {

    private final String topic;
    private final Serializer<T> serializer;
    private final IMqttClient client;

    public MQTTEmitter(String topic, Serializer<T> serializer, IMqttClient mqttClient) {
        this.topic = topic;
        this.serializer = serializer;
        this.client = mqttClient;
    }

    @Override
    public void emit(T data) {
        try {
            client.publish(topic, serializer.serialize(data), 0, false);
        } catch (MqttException e) {
            /** Print the exception if it occurs */
            e.printStackTrace();
        }
    }

}
