package it.unimore.dipi.iot.example.mqtt;

import it.unimore.dipi.iot.wldt.engine.WldtConfiguration;
import it.unimore.dipi.iot.wldt.engine.WldtEngine;
import it.unimore.dipi.iot.wldt.exception.WldtConfigurationException;
import it.unimore.dipi.iot.wldt.process.WldtMqttProcess;
import it.unimore.dipi.iot.wldt.worker.mqtt.Mqtt2MqttConfiguration;
import it.unimore.dipi.iot.wldt.worker.mqtt.Mqtt2MqttWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Author: Marco Picone, Ph.D. (marco.picone@unimore.it)
 * Date: 24/07/2020
 * Project: MQTT Digital Twin Example - White Label Digital Twin - Java Framework
 */
public class WldtMqttExampleProcess {

    private static final String TAG = "[WLDT-MQTT-Process]";

    private static final Logger logger = LoggerFactory.getLogger(WldtMqttProcess.class);

    public static void main(String[] args)  {

        try{

            logger.info("{} Initializing WLDT-Engine ... ", TAG);

            //Manual creation of the WldtConfiguration
            WldtConfiguration wldtConfiguration = new WldtConfiguration();
            wldtConfiguration.setDeviceNameSpace("it.unimore.dipi.things");
            wldtConfiguration.setWldtBaseIdentifier("wldt");
            wldtConfiguration.setWldtStartupTimeSeconds(10);
            wldtConfiguration.setApplicationMetricsEnabled(false);

            WldtEngine wldtEngine = new WldtEngine(wldtConfiguration);
            wldtEngine.addNewWorker(new Mqtt2MqttWorker(wldtEngine.getWldtId(), getMqttExampleConfiguration()));
            wldtEngine.startWorkers();

        }catch (Exception | WldtConfigurationException e){
            e.printStackTrace();
        }
    }

    /**
     * Example configuration for the MQTT-to-MQTT WLDT Worker
     * @return
     */
    private static Mqtt2MqttConfiguration getMqttExampleConfiguration(){

        Mqtt2MqttConfiguration mqtt2MqttConfiguration = new Mqtt2MqttConfiguration();

        mqtt2MqttConfiguration.setOutgoingClientQoS(0);
        mqtt2MqttConfiguration.setDestinationBrokerAddress("127.0.0.1");
        mqtt2MqttConfiguration.setDestinationBrokerPort(1884);
        mqtt2MqttConfiguration.setDestinationBrokerBaseTopic("wldt");
        mqtt2MqttConfiguration.setDeviceId("20a23b54-0f03-4f07-833b-97b904ada0f9");
        mqtt2MqttConfiguration.setResourceIdList(Arrays.asList("d5800329-6306-4cfb-b036-459211ef0a56-0", "dummy_sensor:5fd5ae51-9875-4618-9ef8-34f35de2c812-1"));
        mqtt2MqttConfiguration.setDeviceTelemetryTopic("telemetry/{{device_id}}");
        mqtt2MqttConfiguration.setResourceTelemetryTopic("telemetry/{{device_id}}/resource/{{resource_id}}");
        mqtt2MqttConfiguration.setEventTopic("events/{{device_id}}");
        mqtt2MqttConfiguration.setCommandRequestTopic("commands/{{device_id}}/request");
        mqtt2MqttConfiguration.setCommandResponseTopic("commands/{{device_id}}/response");
        mqtt2MqttConfiguration.setBrokerAddress("127.0.0.1");
        mqtt2MqttConfiguration.setBrokerPort(1883);

        return mqtt2MqttConfiguration;
    }

}
