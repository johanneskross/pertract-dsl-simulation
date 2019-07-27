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
package org.fortiss.pmwt.pertract.dsl.simulation.client;

import javax.naming.TimeLimitExceededException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.emfjson.jackson.resource.JsonResourceFactory;
import org.fortiss.pmwt.pertract.dsl.model.application.ApplicationExecutionArchitecture;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.DataWorkloadArchitecture;
import org.fortiss.pmwt.pertract.dsl.model.resources.ResourceArchitecture;
import org.fortiss.pmwt.pertract.dsl.simulation.client.configuration.SimulationConfigurationDTO;
import org.fortiss.pmwt.pertract.dsl.simulation.client.results.SimulationResultsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PerTractSimulationClient {

	private Client client;
	private WebTarget webtarget;
	private static final String API_PATH = "/api/v1";
	private final Logger log = LoggerFactory.getLogger(PerTractSimulationClient.class);

	public PerTractSimulationClient(String serverUrl) {
		this.client = ClientBuilder.newClient();
		this.webtarget = client.target(serverUrl + API_PATH);
	}

	public SimulationResultsDTO simulate(ApplicationExecutionArchitecture app, DataWorkloadArchitecture data, ResourceArchitecture resource, int simulationTime) {
		SimulationConfigurationDTO simulationConfig = createSimulationConfig(app, data, resource, simulationTime);
		try {
			String statusLocation = postSimulation(simulationConfig);
			SimulationResultsDTO results = getResults(statusLocation);
			deleteResult(statusLocation + "/results");
			return results;
		} catch (Exception e) {
			log.error("server error", e);
		}
		return new SimulationResultsDTO();
	}

	public String postSimulation(SimulationConfigurationDTO simulationConfig) throws NoContentException {
		Response response = webtarget.path("simulation").request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(simulationConfig, MediaType.APPLICATION_JSON), Response.class);
		if (response.getStatus() == Status.ACCEPTED.getStatusCode()) {
			String location = response.getLocation().toString();
			log.info("Simulation status availbable at " + location);
			return location;
		} else {
			throw new NoContentException(response.toString());
		}
	}

	public SimulationResultsDTO getResults(String statusLocation) throws NoContentException, InterruptedException, TimeLimitExceededException {
		Response response = client.target(statusLocation).request(MediaType.APPLICATION_JSON).get();
		while (response.getStatus() == Status.OK.getStatusCode()) {
			String header = response.getHeaderString("status");
			if (header.equals("Results are available")) {
				SimulationResultsDTO results = response.readEntity(SimulationResultsDTO.class);
				log.info("Simulation results available at " + response.getLocation().toString());
				return results;
			} else {
				log.info(header);
				Thread.sleep(3000);
			}
			response = client.target(statusLocation).request(MediaType.APPLICATION_JSON).get();
		}
		throw new NoContentException(response.toString());
	}

	public void deleteResult(String resultLocation) throws NotFoundException {
		log.info("Deleting results on server");
		Response response = client.target(resultLocation).request(MediaType.APPLICATION_JSON).delete();
		if (response.getStatus() != Status.OK.getStatusCode()) {
			throw new RuntimeException(response.getStatusInfo().toString());
		}
	}

	private SimulationConfigurationDTO createSimulationConfig(ApplicationExecutionArchitecture app, DataWorkloadArchitecture data, ResourceArchitecture resource, int simulationTime) {
		SimulationConfigurationDTO config = new SimulationConfigurationDTO();
		ObjectMapper mapper = new JsonResourceFactory().getMapper();
		config.setApplicationExecutionArchitecture(mapper.valueToTree(app));
		config.setDataWorkloadArchitecture(mapper.valueToTree(data));
		config.setResourceArchitecture(mapper.valueToTree(resource));
		config.setSimulationTime(simulationTime);
		return config;
	}

}
