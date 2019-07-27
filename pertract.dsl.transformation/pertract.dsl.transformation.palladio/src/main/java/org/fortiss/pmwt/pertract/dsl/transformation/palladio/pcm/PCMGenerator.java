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
package org.fortiss.pmwt.pertract.dsl.transformation.palladio.pcm;

import org.fortiss.pmwt.pertract.dsl.model.application.ApplicationExecutionArchitecture;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.DataWorkloadArchitecture;
import org.fortiss.pmwt.pertract.dsl.model.resources.ResourceArchitecture;
import org.fortiss.pmwt.pertract.dsl.transformation.palladio.PerformanceModelGenerator;
import org.fortiss.pmwt.pertract.dsl.transformation.palladio.pcm.allocation.AllocationGenerator;
import org.fortiss.pmwt.pertract.dsl.transformation.palladio.pcm.repository.RepositoryGenerator;
import org.fortiss.pmwt.pertract.dsl.transformation.palladio.pcm.resourceenvironment.ResourceEnvironmentGenerator;
import org.fortiss.pmwt.pertract.dsl.transformation.palladio.pcm.system.SystemGenerator;
import org.fortiss.pmwt.pertract.dsl.transformation.palladio.pcm.usage.UsageGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uka.ipd.sdq.pcm.allocation.Allocation;
import de.uka.ipd.sdq.pcm.repository.Repository;
import de.uka.ipd.sdq.pcm.resourceenvironment.ResourceEnvironment;
import de.uka.ipd.sdq.pcm.system.System;
import de.uka.ipd.sdq.pcm.usagemodel.UsageModel;

public class PCMGenerator implements PerformanceModelGenerator{

	PCMExport pcmExport;
	RepositoryGenerator repositoryGenerator;
	SystemGenerator systemGenerator;
	ResourceEnvironmentGenerator resourceEnvironmentGenerator;
	AllocationGenerator allocationGenerator;
	UsageGenerator usageGenerator;
	private final Logger log = LoggerFactory.getLogger(PCMGenerator.class);
	
	public PCMGenerator() {
		this.pcmExport = new PCMExport();
		this.repositoryGenerator = new RepositoryGenerator();
		this.systemGenerator = new SystemGenerator();
		this.resourceEnvironmentGenerator = new ResourceEnvironmentGenerator();
		this.allocationGenerator = new AllocationGenerator();
		this.usageGenerator = new UsageGenerator();
	}
	
	public byte[] generatePCMModels(ApplicationExecutionArchitecture applicationModel, DataWorkloadArchitecture dataWorkload, ResourceArchitecture resources) {
		log.info("Start generating models");
		Repository repository = this.repositoryGenerator.generateRepository(applicationModel, dataWorkload.getDataModels());
		System system = this.systemGenerator.generateSystemModel(repository);
		ResourceEnvironment resourceEnvironment = this.resourceEnvironmentGenerator.generateResourceEnvironment(resources);
		Allocation allocation = this.allocationGenerator.generateAllocation(system, resourceEnvironment);
		UsageModel usage = this.usageGenerator.createUsageModel(system, applicationModel.getApplicationConfiguration(), dataWorkload);
		return pcmExport.zipModels(repository, system, resourceEnvironment, allocation, usage);
	}

	@Override
	public void generatePerformanceModels(ApplicationExecutionArchitecture applicationModel,
			DataWorkloadArchitecture dataWorkload, ResourceArchitecture resources, String outputFolder) {
		log.info("Start generating models");
		Repository repository = this.repositoryGenerator.generateRepository(applicationModel, dataWorkload.getDataModels());
		System system = this.systemGenerator.generateSystemModel(repository);
		ResourceEnvironment resourceEnvironment = this.resourceEnvironmentGenerator.generateResourceEnvironment(resources);
		Allocation allocation = this.allocationGenerator.generateAllocation(system, resourceEnvironment);
		UsageModel usage = this.usageGenerator.createUsageModel(system, applicationModel.getApplicationConfiguration(), dataWorkload);		
		pcmExport.saveModels(repository, system, resourceEnvironment, allocation, usage, outputFolder);
	}
	
}
