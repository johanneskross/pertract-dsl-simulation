/*******************************************************************************
 * Copyright (C) 2018 fortiss GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     kross - initial implementation
 ******************************************************************************/
package org.fortiss.pmwt.pertract.dsl.simulation.service.results;

public class SimulationMeasurementDTO {

	private double timestamp;
	private double value;

	public SimulationMeasurementDTO() {
		// empty
	}
	
	public SimulationMeasurementDTO(double timestamp, double value) {
		this.timestamp = timestamp;
		this.value = value;
	}
	
	public double getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(double timestamp) {
		this.timestamp = timestamp;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
