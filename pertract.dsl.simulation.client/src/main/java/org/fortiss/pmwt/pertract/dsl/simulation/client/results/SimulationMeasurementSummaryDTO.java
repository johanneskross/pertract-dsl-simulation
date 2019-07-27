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
package org.fortiss.pmwt.pertract.dsl.simulation.client.results;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

public class SimulationMeasurementSummaryDTO {

	private double mean;
	private double min;
	private double max;
	private double percentile05;
	private double percentile25;
	private double percentile50;
	private double percentile75;
	private double percentile95;
	private int measurementCount;
	
	public SimulationMeasurementSummaryDTO() {
		// empty
	}

	public SimulationMeasurementSummaryDTO(DescriptiveStatistics descriptiveStatistics) {
		this.mean = descriptiveStatistics.getMean();
		this.min = descriptiveStatistics.getMin();
		this.max = descriptiveStatistics.getMax();
		this.measurementCount = descriptiveStatistics.getValues().length;
		this.percentile05 = descriptiveStatistics.getPercentile(5);
		this.percentile25 = descriptiveStatistics.getPercentile(25);
		this.percentile50 = descriptiveStatistics.getPercentile(50);
		this.percentile75 = descriptiveStatistics.getPercentile(75);
		this.percentile95 = descriptiveStatistics.getPercentile(95);
	}
	
	public String toString() {
		String summary = "";
		summary += "mean: " + mean + "\n";
		summary += "min: " + min + "\n";
		summary += "max: " + max + "\n";
		summary += "percentile05: " + percentile05 + "\n";
		summary += "percentile25: " + percentile25 + "\n";
		summary += "percentile50: " + percentile50 + "\n";
		summary += "percentile75: " + percentile75 + "\n";
		summary += "percentile95: " + percentile95 + "\n";
		summary += "measurementCount: " + measurementCount;
		return summary;
	}

	public double getMean() {
		return mean;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getPercentile05() {
		return percentile05;
	}

	public void setPercentile05(double percentile05) {
		this.percentile05 = percentile05;
	}

	public double getPercentile25() {
		return percentile25;
	}

	public void setPercentile25(double percentile25) {
		this.percentile25 = percentile25;
	}

	public double getPercentile50() {
		return percentile50;
	}

	public void setPercentile50(double percentile50) {
		this.percentile50 = percentile50;
	}

	public double getPercentile75() {
		return percentile75;
	}

	public void setPercentile75(double percentile75) {
		this.percentile75 = percentile75;
	}

	public double getPercentile95() {
		return percentile95;
	}

	public void setPercentile95(double percentile95) {
		this.percentile95 = percentile95;
	}

	public int getMeasurementCount() {
		return measurementCount;
	}

	public void setMeasurementCount(int measurementCount) {
		this.measurementCount = measurementCount;
	}
	
}
