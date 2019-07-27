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

import java.util.ArrayList;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

public class SimulationMeasureDTO {

	private String name;
	private String metric;
	private String unit;
	private SimulationMeasurementSummaryDTO measurementSummary;
	private ArrayList<SimulationMeasurementDTO> measurements;
	
	public SimulationMeasureDTO() {
		this.measurements = new ArrayList<SimulationMeasurementDTO>();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public SimulationMeasurementSummaryDTO getMeasurementSummary() {
		return measurementSummary;
	}

	public void setMeasurementSummary(SimulationMeasurementSummaryDTO measurementSummary) {
		this.measurementSummary = measurementSummary;
	}
	
	public void createMeasurementSummary(DescriptiveStatistics descriptiveStatistics) {
		this.measurementSummary = new SimulationMeasurementSummaryDTO(descriptiveStatistics);
	}

	public ArrayList<SimulationMeasurementDTO> getMeasurements() {
		return measurements;
	}

	public void setMeasurements(ArrayList<SimulationMeasurementDTO> measurements) {
		this.measurements = measurements;
	}
	
	public void addMeasurement(SimulationMeasurementDTO measurement) {
		this.measurements.add(measurement);
	}
	
	public void addMeasurement(double timestamp, double value) {
		SimulationMeasurementDTO measurement = new SimulationMeasurementDTO(timestamp, value);
		this.measurements.add(measurement);
	}

}
