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
package org.fortiss.pmwt.pertract.dsl.simulation.client.configuration;

import javax.xml.bind.annotation.XmlRootElement;

import org.fortiss.pmwt.pertract.dsl.model.application.ApplicationDSLFactory;
import org.fortiss.pmwt.pertract.dsl.model.application.ApplicationDSLPackage;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.DataWorkloadDSLFactory;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.DataWorkloadDSLPackage;
import org.fortiss.pmwt.pertract.dsl.model.resources.ResourceDSLFactory;
import org.fortiss.pmwt.pertract.dsl.model.resources.ResourceDSLPackage;

import com.fasterxml.jackson.databind.JsonNode;

@XmlRootElement
public class SimulationConfigurationDTO{

	private JsonNode applicationExecutionArchitecture;
	private JsonNode dataWorkloadArchitecture;
	private JsonNode resourceArchitecture;
	private int simulationTime;
	
	public SimulationConfigurationDTO() {
		ApplicationDSLPackage.eINSTANCE.eClass();
		ApplicationDSLFactory.eINSTANCE.eClass();
		DataWorkloadDSLPackage.eINSTANCE.eClass();
		DataWorkloadDSLFactory.eINSTANCE.eClass();
		ResourceDSLPackage.eINSTANCE.eClass();
		ResourceDSLFactory.eINSTANCE.eClass();
	}

	public JsonNode getApplicationExecutionArchitecture() {
		return applicationExecutionArchitecture;
	}

	public void setApplicationExecutionArchitecture(JsonNode applicationExecutionArchitecture) {
		this.applicationExecutionArchitecture = applicationExecutionArchitecture;
	}

	public JsonNode getDataWorkloadArchitecture() {
		return dataWorkloadArchitecture;
	}

	public void setDataWorkloadArchitecture(JsonNode dataWorkloadArchitecture) {
		this.dataWorkloadArchitecture = dataWorkloadArchitecture;
	}

	public JsonNode getResourceArchitecture() {
		return resourceArchitecture;
	}

	public void setResourceArchitecture(JsonNode resourceArchitecture) {
		this.resourceArchitecture = resourceArchitecture;
	}

	public int getSimulationTime() {
		return simulationTime;
	}

	public void setSimulationTime(int simulationTime) {
		this.simulationTime = simulationTime;
	}

}
