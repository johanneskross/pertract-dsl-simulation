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
package org.fortiss.pmwt.pertract.dsl.simulation.service;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.Map;
import java.util.SortedMap;

import org.fortiss.pmwt.pertract.dsl.simulation.service.configuration.SimulationConfigDTO;
import org.fortiss.pmwt.pertract.dsl.simulation.service.results.SimulationMeasureDTO;
import org.fortiss.pmwt.pertract.dsl.simulation.service.results.SimulationResultsDTO;
import org.fortiss.pmwt.pertract.dsl.transformation.palladio.simulator.PCMSimulator;
import org.fortiss.pmwt.pertract.palladio.ResultsAnalyzer;
import org.fortiss.pmwt.pertract.dsl.transformation.palladio.PerformanceModelGenerator;
import org.fortiss.pmwt.pertract.dsl.transformation.palladio.pcm.PCMGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimulationService implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(SimulationService.class);
	private String workspace = "/opt/";
	private SortedMap<Integer, SimulationConfigDTO> queue;
	private Map<Integer, String> inProgress;
	private Map<Integer, SimulationResultsDTO> results;
	
	public SimulationService(SortedMap<Integer, SimulationConfigDTO> queue, Map<Integer, String> inProgress, Map<Integer, SimulationResultsDTO> results) {
		this.queue = queue;
		this.inProgress = inProgress;
		this.results = results;
	}
	
	@Override
	public void run() {
		boolean run = true;
		try {
			while(run) {
				if (queue.isEmpty()) {
					Thread.sleep(1000);
				} else {
					int id = queue.firstKey();
					SimulationConfigDTO simulationConfig = queue.get(id);
					inProgress.put(id, "In progress");
					queue.remove(id);
					this.simulate(id, simulationConfig);
				}
			}
		} catch (InterruptedException e) {
            log.error("Could not run simulation service", e);
        }	
	}

	private void simulate(int id, SimulationConfigDTO config) {
		if (!new File(workspace).canWrite()) {
			throw new InvalidParameterException(workspace);
		}
		String status = "Transforming PerTract DSL to Palladio component model";
		log.info(status);
		inProgress.put(id, status);
		PerformanceModelGenerator generator = new PCMGenerator();
		generator.generatePerformanceModels(config.getApplicationExecutionArchitecture(), config.getDataWorkloadArchitecture(), config.getResourceArchitecture(), workspace); 			
		
		status = "Simulating Palladio component model";
		log.info(status);
		inProgress.put(id, status);
		PCMSimulator simulator = new PCMSimulator();
		File resultsDirectory = simulator.simulate(workspace, config.getSimulationTime());
		
		status = "Analyzing simulation results";
		log.info(status);
		inProgress.put(id, status);
		int cores = config.getResourceArchitecture().getResourceNodes().get(0).getProcessingResourceUnit().getReplications(); //TODO assumes same cores for each node
		SimulationResultsDTO simulationResults = anaylzeSimulationResults(resultsDirectory, config.getSimulationTime(), cores);
		
		log.info("Finished simulation for config with id " + id);
		results.put(id, simulationResults);
		inProgress.remove(id);	
	}
	
	private SimulationResultsDTO anaylzeSimulationResults(File resultsDirectory, int simulationTime, int cores) {
		SimulationResultsDTO results = new SimulationResultsDTO();
		ResultsAnalyzer pcmResultsAnalyzer = new ResultsAnalyzer(resultsDirectory.getPath(), 0, simulationTime);
		results.addMeasures(pcmResultsAnalyzer.analyzeResponseTimes());
		int responseTimeOfApp = getResponseTimeOfApp(results, simulationTime);
		pcmResultsAnalyzer = new ResultsAnalyzer(resultsDirectory.getPath(), 0, responseTimeOfApp);
		results.addMeasures(pcmResultsAnalyzer.analyzeCPUUtilization(cores, 1000));
		pcmResultsAnalyzer.deleteSourceDirectory();
		return results;
	}

	private int getResponseTimeOfApp(SimulationResultsDTO results, int simulationTime) {	
		for (SimulationMeasureDTO measure : results.getMeasures()) {
			if (measure.getName().contains("EntryLevelSystem")) {
				return (int) measure.getMeasurements().get(measure.getMeasurements().size()-1).getValue();
			}
		}
		return simulationTime;
	}
}
