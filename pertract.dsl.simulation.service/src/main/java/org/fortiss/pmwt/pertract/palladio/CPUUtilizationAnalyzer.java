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
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.fortiss.pmwt.pertract.dsl.simulation.service.results.SimulationMeasureDTO;
import org.fortiss.pmwt.pertract.palladio.util.HelperClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CPUUtilizationAnalyzer {		

	private final Logger log = LoggerFactory.getLogger(CPUUtilizationAnalyzer.class);
	
    public List<SimulationMeasureDTO> analyze(final File[] files, final double startTime, final double endTime, final int nbrCores, final double processingRate) {  
    	int sampleRate = 10;
    	int newEndTime = (int) Math.ceil((double) endTime / sampleRate) * sampleRate;
    	List<SimulationMeasureDTO> measures = new ArrayList<SimulationMeasureDTO>();
    	for (final File file : files) {
			 if (isFileAnalysable(file)) {
			    SortedMap<Double,Double> aggregatedTimes = this.aggregateTimes(file);
			    SortedMap<Integer, Double> timeseries = createEmptyTimeseries(sampleRate, aggregatedTimes.lastKey());
			    fillTimeseries(timeseries, aggregatedTimes, sampleRate, nbrCores);
			    measures.add(createSimulationMeasure(file, timeseries, newEndTime, sampleRate));
			 }
    	}
		return measures;              
    }

	public boolean isFileAnalysable(File file) {
		if (file.getName().startsWith("Demanded time at") && file.getName().contains("[CPU]")) {
			return true;
		}		
		return false;
	}
    
    private SortedMap<Double,Double> aggregateTimes(final File file){
    	SortedMap<Double,Double> aggregatedTimes = new TreeMap<>();
		try {    								 
			FileReader fr = new FileReader(file);		
		    BufferedReader br = new BufferedReader(fr);
		    String line;
	        boolean firstLine = true;
	        while ((line = br.readLine()) != null) {
	            if (firstLine) {
	                firstLine = false;
	            } else {
	            	Double eventTime = Double.parseDouble(line.split(";")[0]);
	            	Double demandedTime = Double.parseDouble(line.split(";")[1]);
	        		if (aggregatedTimes.containsKey(eventTime)) {
	            		aggregatedTimes.put(eventTime, aggregatedTimes.get(eventTime)+demandedTime);
	            	} else {
	            		aggregatedTimes.put(eventTime, demandedTime);
	            	}
	            }
	        }   
	        br.close();	 
	        fr.close();
		} catch (FileNotFoundException e) {
			log.info("file not found", e);
		} catch (IOException e) {
			log.info("check IO", e);
		}    
		return aggregatedTimes;
    }
    
    private SortedMap<Integer,Double> createEmptyTimeseries(int sampleRate, Double lastEventTime) {
    	SortedMap<Integer,Double> timeseries = new TreeMap<>();
    	for (int i = 0; i < (lastEventTime+sampleRate)/sampleRate; i++) {
    		timeseries.put(i*sampleRate, 0d);
    	}
    	return timeseries;
    }

	private void fillTimeseries(SortedMap<Integer, Double> timeseries, SortedMap<Double, Double> aggregatedTimes, int sampleRate, int cores) {
		for (Entry<Double, Double> entry : aggregatedTimes.entrySet()) {
			int sampleIndex = ((int) (entry.getKey()/sampleRate)) * sampleRate;
			double utilization = entry.getValue() / (sampleRate * cores);
			double accumulatedUitilization = timeseries.get(sampleIndex) + utilization;
			if (accumulatedUitilization>1.0) {
				int i = sampleIndex;
				while (accumulatedUitilization>1.0) {
					timeseries.put(i, 1.0);
					i+=sampleRate;
					if (timeseries.containsKey(i)) {
						accumulatedUitilization = accumulatedUitilization + timeseries.get(i) - 1.0;
					} else {
						accumulatedUitilization=0.0;
					}
				}
				timeseries.put(i, accumulatedUitilization);
			} else {
				timeseries.put(sampleIndex, accumulatedUitilization);
			}
		}
		
	}
	
	private SimulationMeasureDTO createSimulationMeasure(File file, SortedMap<Integer, Double> timeseries, int endTime, int sampleRate) {
    	SimulationMeasureDTO measure = new SimulationMeasureDTO();
    	measure.setName(HelperClass.getResourceContainerName(file.getName()));
    	measure.setMetric("cpu utilization");
		measure.setUnit("percentage");
		DescriptiveStatistics statistics = new DescriptiveStatistics();
		for (int timestamp = 0; timestamp <= endTime; timestamp+=sampleRate) {
			double value = timeseries.get(timestamp);
			measure.addMeasurement(timestamp, value);
			statistics.addValue(value);
		}
    	measure.createMeasurementSummary(statistics);
    	return measure;
	}

}
