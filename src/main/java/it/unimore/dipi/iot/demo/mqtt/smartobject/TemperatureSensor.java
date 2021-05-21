package it.unimore.dipi.iot.demo.mqtt.smartobject;

import java.util.Random;

/**
 *  Emulated Temperature Sensor generating random values within
 *  a target range
 *
 * @author : Marco Picone, Ph.D. (marco.picone@unimore.it)
 * @created: 21/05/2021
 * @project: WLDT - MQTT Example
 */
public class TemperatureSensor {

    private Random rnd;
    
    private double temperatureValue;
    
    public TemperatureSensor() {
        this.rnd = new Random(System.currentTimeMillis());
        this.temperatureValue = 0.0; 
    }
 
    private void generateEngineTemperature() {
    	temperatureValue =  80 + rnd.nextDouble() * 20.0;     
    }

	public double getTemperatureValue() {
		generateEngineTemperature();
		return temperatureValue;
	}

	public void setTemperatureValue(double temperatureValue) {
		this.temperatureValue = temperatureValue;
	}

	@Override
	public String toString() {
		return "EngineTemperatureSensor [temperatureValue=" + temperatureValue + "]";
	}
    
}