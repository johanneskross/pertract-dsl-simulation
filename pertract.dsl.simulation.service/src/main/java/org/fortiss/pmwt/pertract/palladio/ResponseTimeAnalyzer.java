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
package org.fortiss.pmwt.pertract.palladio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.fortiss.pmwt.pertract.dsl.simulation.service.results.SimulationMeasureDTO;
import org.fortiss.pmwt.pertract.dsl.simulation.service.results.SimulationMeasurementSummaryDTO;
import org.fortiss.pmwt.pertract.palladio.metrics.ResponseTimeMetricTmp;
import org.fortiss.pmwt.pertract.palladio.util.HelperClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseTimeAnalyzer{

	private final Logger log = LoggerFactory.getLogger(ResponseTimeAnalyzer.class);
	
	public boolean isFileAnalysable(File file) {
		if (file.getName().startsWith("Response Time of")) {
			if(file.getName().contains("delegate") || file.getName().contains("run")) {
				return true;	
			}
		}
		return false;
	}

	public List<SimulationMeasureDTO> analyze(final File[] files, final double startTime, final double endTime) {

		List<SimulationMeasureDTO> measures = new ArrayList<SimulationMeasureDTO>();
		List<ResponseTimeMetricTmp> responseTimeMetricTmpList = new ArrayList<ResponseTimeMetricTmp>();

			for (final File fileEntry : files) {
				if (this.isFileAnalysable(fileEntry)) {
					this.analyzeFile(fileEntry, startTime, endTime, responseTimeMetricTmpList);
				}
			}

			for (ResponseTimeMetricTmp responseTimeMetricTmp : responseTimeMetricTmpList) {
				SimulationMeasureDTO measure = new SimulationMeasureDTO();
				measure.setName(responseTimeMetricTmp.getComponentName() + "." + responseTimeMetricTmp.getMethodName()+"()");
				measure.setMetric("response time");
				measure.setUnit("seconds");
				
				DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
				for (int i = 0; i < responseTimeMetricTmp.getMeasurements().size(); i++) {
					double timestamp = responseTimeMetricTmp.getTimestamps().get(i);
					double measurement = responseTimeMetricTmp.getMeasurements().get(i);
					measure.addMeasurement(timestamp, measurement);
					descriptiveStatistics.addValue(measurement);
				}
				SimulationMeasurementSummaryDTO measurementSummary = new SimulationMeasurementSummaryDTO(descriptiveStatistics);
				measure.setMeasurementSummary(measurementSummary);
				measures.add(measure);
			}

		
		return measures;

	}
		
	private void analyzeFile(File fileEntry, final double startTime, final double endTime, List<ResponseTimeMetricTmp> responseTimeMetricTmpList) {
		try {
			String componentName = HelperClass.getComponentName(fileEntry.getName());
			String methodName = HelperClass.getMethodName(fileEntry.getName());
			
			ResponseTimeMetricTmp responseTimeMetricTmp = getResponseTimeMetricTmp(responseTimeMetricTmpList, componentName, methodName);

			if (responseTimeMetricTmp == null) {
				responseTimeMetricTmp = new ResponseTimeMetricTmp();
				responseTimeMetricTmp.setComponentName(HelperClass.getComponentName(fileEntry.getName()));
				responseTimeMetricTmp.setMethodName(HelperClass.getMethodName(fileEntry.getName()));
				responseTimeMetricTmpList.add(responseTimeMetricTmp);
			}

			FileReader fr = new FileReader(fileEntry);
			BufferedReader br = new BufferedReader(fr);
			String line;
			boolean firstLine = true;
			double sum = 0;
			
			int measurements = 0;

			while ((line = br.readLine()) != null) {
				// header line in each PCM result file
				if (firstLine) {
					firstLine = false;
				} else if (endTime > startTime) {
					if (Double.parseDouble(line.split(";")[0]) >= startTime
							&& Double.parseDouble(line.split(";")[0]) < endTime) {
						sum += Double.parseDouble(line.split(";")[1]);
						measurements++;
						responseTimeMetricTmp.addTimestamp(Double.parseDouble(line.split(";")[0]));
						responseTimeMetricTmp.addMeasurement(Double.parseDouble(line.split(";")[1]));
					}
				} else {
					if (Double.parseDouble(line.split(";")[0]) >= startTime) {
						sum += Double.parseDouble(line.split(";")[1]);
						measurements++;
						responseTimeMetricTmp.addTimestamp(Double.parseDouble(line.split(";")[0]));
						responseTimeMetricTmp.addMeasurement(Double.parseDouble(line.split(";")[1]));
					}
				}
			}
			responseTimeMetricTmp.setCountMeasurements(responseTimeMetricTmp.getCountMeasurements() + measurements);
			responseTimeMetricTmp.setSumResponseTime(responseTimeMetricTmp.getSumResponseTime() + sum);
			br.close();
		} catch (FileNotFoundException e) {
			log.info("file not found", e);
		} catch (IOException e) {
			log.info("check IO", e);
		}

	}

	private ResponseTimeMetricTmp getResponseTimeMetricTmp(final List<ResponseTimeMetricTmp> responseTimeMetricTmpList, final String componentName, final String methodName) {
		for (ResponseTimeMetricTmp responseTimeMetricTmp : responseTimeMetricTmpList) {
			if (responseTimeMetricTmp.getComponentName().equals(componentName)) {
				if (responseTimeMetricTmp.getMethodName().equals(methodName)) {
					return responseTimeMetricTmp;
				}
			}
		}
		return null;
	}

}
