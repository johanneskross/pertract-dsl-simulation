/*******************************************************************************
 * Copyright (C) 2018 fortiss GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     voegele - initial implementation
 *     kross 
 ******************************************************************************/
package org.fortiss.pmwt.pertract.palladio.metrics;

import java.util.ArrayList;
import java.util.List;

public class ResponseTimeMetricTmp {

	private double sumResponseTime;
	private int countMeasurements;
	private List<Double> measurements = new ArrayList<Double>();
	private List<Double> timestamps = new ArrayList<Double>();
	private String componentName;
	private String methodName;	
	
	public final String getMethodName() {
		return methodName;
	}

	public final void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public final String getComponentName() {
		return componentName;
	}

	public final void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public final double getSumResponseTime() {
		return sumResponseTime;
	}

	public final void setSumResponseTime(double sumResponseTime) {
		this.sumResponseTime = sumResponseTime;
	}

	public final List<Double> getMeasurements() {
		return measurements;
	}
	
	public final void addMeasurement(double measurements) {
		this.measurements.add(measurements);
	}
	
	public final void addMeasurements(List<Double> measurements) {
		this.measurements.addAll(measurements);
	}

	public final int getCountMeasurements() {
		return countMeasurements;
	}

	public final void setCountMeasurements(int countMeasurements) {
		this.countMeasurements = countMeasurements;
	}
	
	public final List<Double> getTimestamps() {
		return timestamps;
	}
	
	public final void addTimestamps(List<Double> timestamps) {
		this.timestamps.addAll(timestamps);
	} 
	
	public final void addTimestamp(double timestamp) {
		this.timestamps.add(timestamp);
	} 

}
