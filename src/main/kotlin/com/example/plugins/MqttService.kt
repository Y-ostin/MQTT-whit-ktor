package com.example.plugins

import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttService {

    private val broker = "tcp://localhost:1883"
    private val clientId = "KtorMqttClient"
    private val persistence = MemoryPersistence()
    private lateinit var client: MqttClient

    init {
        try {
            client = MqttClient(broker, clientId, persistence)
            val connOpts = MqttConnectOptions()
            connOpts.isCleanSession = true
            println("Connecting to broker: $broker")
            client.connect(connOpts)
            println("Connected")
        } catch (me: MqttException) {
            me.printStackTrace()
        }
    }

    fun publish(topic: String, content: String, qos: Int = 2) {
        try {
            val message = MqttMessage(content.toByteArray())
            message.qos = qos
            client.publish(topic, message)
            println("Message published: $content")
        } catch (me: MqttException) {
            me.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            client.disconnect()
            println("Disconnected")
        } catch (me: MqttException) {
            me.printStackTrace()
        }
    }
}
