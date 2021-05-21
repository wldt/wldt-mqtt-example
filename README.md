# MQTT Digital Twin Example - WhiteLabel Digital Twin Framework

This project shows how to implement a Digital Twin for an MQTT Device through the WLDT Framework.
Both a demo MQTT device and an MQTT Consumer has been included in the repository in order to easily test the example.
The MQTT broker is not included in the repository, and an external one should be used.

The MQTT-to-MQTT built-in IoT dedicated worker is implemented through the class \code{Mqtt2MqttWorker} 
providing a configurable way to automatically synchronize data between twins over MQTT. 
The protocol is based on a Pub/Sub approach where categorization of data and resources 
are inferred by topics definition (e.g., a device '01' can send a telemetry data for a resource 'abcd' 
to the following topic /dev/01/res/abcd/). 
An MQTT physical device can be at the same time a data producer 
(e.g., to publish for example telemetry data) or a consumer (e.g., to receive external commands).

Developers can define the topic of interest specifying if they are going out from incoming or outgoing from 
and to the device. The topic definition also uses a template placeholders like [Mustache](https://mustache.github.io/)) 
to dynamically synchronize MQTT topics according to available device and resource information. 
Mapped placeholder are associated to the _device id_ (template value: `{{device_id}}`) and _resource id_ (template value: `{{resource_id}}`).  
A topic can be described through the class **MqttTopicDescriptor** as illustrated in the following examples.

In this first example a telemetry topic (from the device) can be defined and then mirrored by the Digital Twin 
as a `MqttTopicDescriptor.MQTT_TOPIC_TYPE_DEVICE_OUTGOING` with the following declaration:

```java
MqttTopicDescriptor temperatureTelemetryTopic = new MqttTopicDescriptor(
        "temperature_topic_id",
        "temperature_resource",
        "telemetry/{{device_id}}/resource/{{resource_id}}",
        MqttTopicDescriptor.MQTT_TOPIC_TYPE_DEVICE_OUTGOING);
```

In this second example a command topic (from external application to the device) can be defined and then mirrored by the Digital Twin
as a `MqttTopicDescriptor.MQTT_TOPIC_TYPE_DEVICE_INCOMING` with the following declaration:

```java
MqttTopicDescriptor exampleCommandTopic = new MqttTopicDescriptor(
        "command_topic_id",
        "default_command",
        "command/{{device_id}}",
        MqttTopicDescriptor.MQTT_TOPIC_TYPE_DEVICE_INCOMING);
```

The following example shows a WLDT implementation using the built-in MQTT to MQTT worker 
to automatically create a digital twin of an existing MQTT physical object
             
```java             
Mqtt2MqttConfiguration mqtt2MqttConfiguration = new Mqtt2MqttConfiguration();

mqtt2MqttConfiguration.setDtPublishingQoS(0);
mqtt2MqttConfiguration.setBrokerAddress(SOURCE_BROKER_ADDRESS);
mqtt2MqttConfiguration.setBrokerPort(SOURCE_BROKER_PORT);
mqtt2MqttConfiguration.setDestinationBrokerAddress(DESTINATION_BROKER_ADDRESS);
mqtt2MqttConfiguration.setDestinationBrokerPort(DESTINATION_BROKER_PORT);
mqtt2MqttConfiguration.setDtTopicPrefix(DT_PREFIX);
mqtt2MqttConfiguration.setDeviceId(DEVICE_ID);

//Specify Topic List Configuration
mqtt2MqttConfiguration.setTopicList(
        Arrays.asList(
                temperatureTelemetryTopic,
                exampleCommandTopic
        )
);
```

A dedicated WLDT ProcessingPipeline can be associated to each topic in order to customize the
management of received and forwarded messages through the WDLT Digital Twin.
Each ProcessingPipeline can be defined as the composition of one or multiple ProcessingStep
defined by the developer. Each step use as reference data structure the class `MqttPipelineData`
containing the information about the topic, message's type and payload. 
Two examples of two different pipelines are presented below. 

In that first example a ProcessingPipeline is associated to the temperature telemetry topic
providing three steps:
- An `IdentityProcessingStep` that is just used to show a log of the incoming packet and payload
- The `MqttAverageProcessingStep` dedicated to evaluate the average value of the last 10 received temperature samples
- The `MqttTopicChangeStep` changes the output topic 

```java             
//Add Processing Pipeline for target topics
mqtt2MqttWorker.addTopicProcessingPipeline("temperature_topic_id",
        new ProcessingPipeline(
                new IdentityProcessingStep(),
                new MqttAverageProcessingStep(),
                new MqttTopicChangeStep()
        )
);
```

In the second example a ProcessingPipeline is associated to the command topic providing two steps:
- An `IdentityProcessingStep` that is just used to show a log of the incoming packet and payload
- The `MqttPayloadChangeStep` changes the payload in order to adapt the command received from 
  an external application into the format supported by the physical device

```java 
mqtt2MqttWorker.addTopicProcessingPipeline(DEMO_COMMAND_TOPIC_ID,
        new ProcessingPipeline(
                new IdentityProcessingStep(),
                new MqttPayloadChangeStep()
        )
);
```

When a device is mirrored through a target protocol and Worker a listener can be defined and specified to 
received callbacks related to each stage. Mapped stages are the following:

- onDeviceMirrored
- onDeviceMirroringError
- onResourceMirrored
- onResourceMirroringError

```java 
mqtt2MqttWorker.addMirroringListener(new MirroringListener() {

@Override
public void onDeviceMirrored(String deviceId, Map<String, Object> metadata) {
    logger.info("onDeviceMirrored() callback ! DeviceId: {} -> Metadata: {}", deviceId, metadata);
}

@Override
public void onDeviceMirroringError(String deviceId, String errorMsg) {
    logger.info("onDeviceMirroringError() callback ! DeviceId: {} -> ErrorMsg: {}", deviceId, errorMsg);
}

@Override
public void onResourceMirrored(String resourceId, Map<String, Object> metadata) {
    logger.info("onResourceMirrored() callback ! ResourceId: {} -> Metadata: {}", resourceId, metadata);
}

@Override
public void onResourceMirroringError(String resourceId, String errorMsg) {
    logger.info("onResourceMirroringError() callback ! ResourceId: {} -> ErrorMsg: {}", resourceId, errorMsg);
}

});
```

Once the both worker is properly defined the WLDT can be activated with the new worker and then started.

```java
WldtConfiguration wldtConfiguration = new WldtConfiguration();
wldtConfiguration.setDeviceNameSpace("it.unimore.dipi.things");
wldtConfiguration.setWldtBaseIdentifier("wldt");
wldtConfiguration.setWldtStartupTimeSeconds(10);
wldtConfiguration.setApplicationMetricsEnabled(false);

WldtEngine wldtEngine = new WldtEngine(wldtConfiguration);

wldtEngine.addNewWorker(mqtt2MqttWorker);
wldtEngine.startWorkers();
```
