# MQTT Digital Twin Example - WhiteLabel Digital Twin Framework

This project shows how to implement a Digital Twin for an MQTT Device trough the WLDT Framework.

The MQTT-to-MQTT built-in IoT dedicated worker is implemented through the class \code{Mqtt2MqttWorker} 
providing a configurable way to automatically synchronize data between twins over MQTT. 
The protocol is based on a Pub/Sub approach where categorization of data and resources 
are inferred by topics definition (e.g., a device '01' can send a telemetry data for a resource 'abcd' 
to the following topic /dev/01/res/abcd/). 
An MQTT physical device can be at the same time a data producer 
(e.g., to publish for example telemetry data) or a consumer (e.g., to receive external commands) 
and the provided topics organization has been inspired by the categorization provided 
by open source projects [Eclipse Hono](https://www.eclipse.org/hono/) 
and [Ditto](https://www.eclipse.org/ditto/). 
Developers can use up to four different types of topics 
(also through a template placeholders like [Mustache](https://mustache.github.io/)) 
to dynamically synchronize MQTT topics according to available device and resource information. 
As illustrated in the following example, available topics typologies belong to: *telemetry*, *events* and 
*command requests and responses* allowing the granular mirroring of a physical device trough topics mapping.

The following example shows a WLDT implementation using the built-in MQTT to MQTT worker 
to automatically create a digital twin of an existing MQTT physical object
             
```java             
Mqtt2MqttConfiguration mqttConf = new Mqtt2MqttConfiguration();
mqttConf.setOutgoingClientQoS(0);
mqttConf.setDestinationBrokerAddress("127.0.0.1");
mqttConf.setDestinationBrokerPort(1884);
mqttConf.setDestinationBrokerBaseTopic("wldt");
mqttConf.setDeviceId("id:97b904ada0f9");
mqttConf.setDeviceTelemetryTopic("telemetry/{{device_id}}");
mqttConf.setEventTopic("events/{{device_id}}");
mqttConf.setBrokerAddress("127.0.0.1");
mqttConf.setBrokerPort(1883);

WldtEngine wldtEngine = new WldtEngine(new WldtConfiguration());
wldtEngine.addNewWorker(
	new Mqtt2MqttWorker(
	wldtEngine.getWldtId(), 
	mqttConf));
wldtEngine.startWorkers();
```

