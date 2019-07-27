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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.fortiss.pmwt.pertract.dsl.simulation.service.results.SimulationMeasureDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ResultsAnalyzer {

	protected String resultsDirectory;
	protected double startTime;
	protected double endTime;
	
	private final Logger log = LoggerFactory.getLogger(ResultsAnalyzer.class);

	public ResultsAnalyzer(final String resultsDirectory, final double startTime, final double endTime) {
		this.resultsDirectory = resultsDirectory;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public List<SimulationMeasureDTO> analyzeCPUUtilization(final int nbrCores, final int processingRate) {
		CPUUtilizationAnalyzer cpuUtilizationAnalyzer = new CPUUtilizationAnalyzer();
		final File folder = new File(this.resultsDirectory);
		return cpuUtilizationAnalyzer.analyze(folder.listFiles(), this.startTime, this.endTime, nbrCores, processingRate);
	}

	public List<SimulationMeasureDTO> analyzeResponseTimes() {
		ResponseTimeAnalyzer responseTimeAnalyzer = new ResponseTimeAnalyzer();
		final File folder = new File(this.resultsDirectory);
		return responseTimeAnalyzer.analyze(folder.listFiles(), this.startTime, this.endTime);
	}

	public void deleteSourceDirectory() {
		try {
			File resultsFile = new File(this.resultsDirectory);
			FileUtils.deleteDirectory(resultsFile);
			File parentFile = resultsFile.getParentFile();
			if (parentFile.isDirectory()) {
				for (File file : parentFile.listFiles()) {
					FileUtils.deleteQuietly(file);
				}
			}
		} catch (IOException e) {
			log.error("IO Exception while deleting soruce directory", e);
		}
	}
	
}
