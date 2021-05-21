package it.unimore.dipi.iot.demo.mqtt.smartobject;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;


/**
 * Emulated IoT MQTT Device generating and publishing Temperature Data
 * modeled through the class Temperature Sensor
 *
 * @author : Marco Picone, Ph.D. (marco.picone@unimore.it)
 * @created: 21/05/2021
 * @project: WLDT - MQTT Example
 */
public class DemoMqttDevice {

    private final static Logger logger = LoggerFactory.getLogger(DemoMqttDevice.class);

    //BROKER URL
    private static String BROKER_URL = "tcp://127.0.0.1:1883";

    //Message Limit generated and sent by the producer
    private static final int MESSAGE_COUNT = 1000;

    //Topic used to publish generated demo data E.g.: telemetry/{{device_id}}/resource/{{resource_id}}
    private static final String TOPIC = "telemetry/com:iot:dummy:dummyMqttDevice001/resource/temperature";
    
    public static void main(String[] args) {

        logger.info("SimpleProducer started ...");

        try{

            //Generate a random MQTT client ID using the UUID class
            String mqttClientId = UUID.randomUUID().toString();

            //Represents a persistent data store, used to store outbound and inbound messages while they
            //are in flight, enabling delivery to the QoS specified. In that case use a memory persistence.
            //When the application stops all the temporary data will be deleted.
            MqttClientPersistence persistence = new MemoryPersistence();

            //The the persistence is not passed to the constructor the default file persistence is used.
            //In case of a file-based storage the same MQTT client UUID should be used
            IMqttClient client = new MqttClient(BROKER_URL,mqttClientId, persistence);

            //Define MQTT Connection Options such as reconnection, persistent/clean session and connection timeout
            //Authentication option can be added -> See AuthProducer example
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            //Connect to the target broker
            client.connect(options);

            logger.info("Connected ! Client Id: {}", mqttClientId);

            //Create an instance of an Engine Temperature Sensor
            TemperatureSensor temperatureSensor = new TemperatureSensor();

            //Start to publish MESSAGE_COUNT messages
            for(int i = 0; i < MESSAGE_COUNT; i++) {

                //Send data as simple numeric value
            	double sensorValue = temperatureSensor.getTemperatureValue();
            	String payloadString = Double.toString(sensorValue);

            	//Internal Method to publish MQTT data using the created MQTT Client
            	publishData(client, TOPIC, payloadString);

            	//Sleep for 1 Second
            	Thread.sleep(1000);
            }

            //Disconnect from the broker and close the connection
            client.disconnect();
            client.close();

            logger.info("Disconnected !");

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Send a target String Payload to the specified MQTT topic
     *
     * @param mqttClient
     * @param topic
     * @param msgString
     * @throws MqttException
     */
    public static void publishData(IMqttClient mqttClient, String topic, String msgString) throws MqttException {

        logger.debug("Publishing to Topic: {} Data: {}", topic, msgString);

        if (mqttClient.isConnected() && msgString != null && topic != null) {

            //Create an MQTT Message defining the required QoS Level and if the message is retained or not
            MqttMessage msg = new MqttMessage(msgString.getBytes());
            msg.setQos(0);
            msg.setRetained(false);
            mqttClient.publish(topic,msg);
            
            logger.debug("Data Correctly Published !");
        }
        else{
            logger.error("Error: Topic or Msg = Null or MQTT Client is not Connected !");
        }

    }



}
