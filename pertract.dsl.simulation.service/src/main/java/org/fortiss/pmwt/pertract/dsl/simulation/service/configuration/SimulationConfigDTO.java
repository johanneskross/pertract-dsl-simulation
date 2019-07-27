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
package org.fortiss.pmwt.pertract.dsl.simulation.service.configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emfjson.jackson.resource.JsonResourceFactory;
import org.fortiss.pmwt.pertract.dsl.EMFPersistence;
import org.fortiss.pmwt.pertract.dsl.model.application.ApplicationDSLFactory;
import org.fortiss.pmwt.pertract.dsl.model.application.ApplicationDSLPackage;
import org.fortiss.pmwt.pertract.dsl.model.application.ApplicationExecutionArchitecture;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.DataWorkloadArchitecture;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.DataWorkloadDSLFactory;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.DataWorkloadDSLPackage;
import org.fortiss.pmwt.pertract.dsl.model.resources.ResourceArchitecture;
import org.fortiss.pmwt.pertract.dsl.model.resources.ResourceDSLFactory;
import org.fortiss.pmwt.pertract.dsl.model.resources.ResourceDSLPackage;

import com.fasterxml.jackson.databind.JsonNode;

@XmlRootElement
public class SimulationConfigDTO{

	private ApplicationExecutionArchitecture applicationExecutionArchitecture;
	private DataWorkloadArchitecture dataWorkloadArchitecture;
	private ResourceArchitecture resourceArchitecture;
	private int simulationTime;
	
	public SimulationConfigDTO() {
		ApplicationDSLPackage.eINSTANCE.eClass();
		ApplicationDSLFactory.eINSTANCE.eClass();
		DataWorkloadDSLPackage.eINSTANCE.eClass();
		DataWorkloadDSLFactory.eINSTANCE.eClass();
		ResourceDSLPackage.eINSTANCE.eClass();
		ResourceDSLFactory.eINSTANCE.eClass();
	}

	public ApplicationExecutionArchitecture getApplicationExecutionArchitecture() {
		return applicationExecutionArchitecture;
	}

	public void setApplicationExecutionArchitecture(JsonNode applicationExecutionArchitecture) {
		try {
			Resource.Factory.Registry registry = Resource.Factory.Registry.INSTANCE;
			Map<String, Object> map = registry.getExtensionToFactoryMap();
			map.put(EMFPersistence.FILE_EXTENSION_APP, new JsonResourceFactory());
			ResourceSet resourceSet = new ResourceSetImpl();
			Resource resource = resourceSet.createResource(URI.createURI("*."+EMFPersistence.FILE_EXTENSION_APP));			
			resource.load(new ByteArrayInputStream(applicationExecutionArchitecture.toString().getBytes()), null);
			this.applicationExecutionArchitecture = (ApplicationExecutionArchitecture) resource.getContents().get(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public DataWorkloadArchitecture getDataWorkloadArchitecture() {
		return dataWorkloadArchitecture;
	}

	public void setDataWorkloadArchitecture(JsonNode dataWorkloadArchitecture) {
		try {
			Resource.Factory.Registry registry = Resource.Factory.Registry.INSTANCE;
			Map<String, Object> map = registry.getExtensionToFactoryMap();
			map.put(EMFPersistence.FILE_EXTENSION_DATA, new JsonResourceFactory());
			ResourceSet resourceSet = new ResourceSetImpl();
			Resource resource = resourceSet.createResource(URI.createURI("*."+EMFPersistence.FILE_EXTENSION_DATA));			
			resource.load(new ByteArrayInputStream(dataWorkloadArchitecture.toString().getBytes()), null);
			this.dataWorkloadArchitecture = (DataWorkloadArchitecture) resource.getContents().get(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ResourceArchitecture getResourceArchitecture() {
		return resourceArchitecture;
	}

	public void setResourceArchitecture(JsonNode resourceArchitecture) {
		try {
			Resource.Factory.Registry registry = Resource.Factory.Registry.INSTANCE;
			Map<String, Object> map = registry.getExtensionToFactoryMap();
			map.put(EMFPersistence.FILE_EXTENSION_RESOURCE, new JsonResourceFactory());
			ResourceSet resourceSet = new ResourceSetImpl();
			Resource resource = resourceSet.createResource(URI.createURI("*."+EMFPersistence.FILE_EXTENSION_RESOURCE));			
			resource.load(new ByteArrayInputStream(resourceArchitecture.toString().getBytes()), null);
			this.resourceArchitecture = (ResourceArchitecture) resource.getContents().get(0);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	public int getSimulationTime() {
		return simulationTime;
	}

	public void setSimulationTime(int simulationTime) {
		this.simulationTime = simulationTime;
	}

}
