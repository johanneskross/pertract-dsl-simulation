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
package org.fortiss.pmwt.pertract.dsl.transformation.palladio.pcm.usage;

import org.fortiss.pmwt.pertract.dsl.model.application.ApplicationConfiguration;
import org.fortiss.pmwt.pertract.dsl.model.application.BatchConfiguration;
import org.fortiss.pmwt.pertract.dsl.model.application.MiniBatchConfiguration;
import org.fortiss.pmwt.pertract.dsl.model.application.StreamConfiguration;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.DataModel;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.DataSource;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.DataWorkloadArchitecture;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.FileDataModel;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.OpenDataSource;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.RecordDataModel;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.SingleDataSource;

import de.uka.ipd.sdq.pcm.repository.OperationProvidedRole;
import de.uka.ipd.sdq.pcm.repository.ProvidedRole;
import de.uka.ipd.sdq.pcm.system.System;
import de.uka.ipd.sdq.pcm.usagemodel.ClosedWorkload;
import de.uka.ipd.sdq.pcm.usagemodel.OpenWorkload;
import de.uka.ipd.sdq.pcm.usagemodel.ScenarioBehaviour;
import de.uka.ipd.sdq.pcm.usagemodel.UsageModel;
import de.uka.ipd.sdq.pcm.usagemodel.UsageScenario;
import de.uka.ipd.sdq.pcm.usagemodel.UsagemodelFactory;

public class UsageGenerator {
	
	private ScenarioBehaviorGenerator scenarioBehaviorGenerator;
	private WorkloadGenerator workloadGenerator;
	
	public UsageGenerator() {
		this.scenarioBehaviorGenerator = new ScenarioBehaviorGenerator();
		this.workloadGenerator = new WorkloadGenerator();
	}
	
	public UsageModel createUsageModel(System system, ApplicationConfiguration appConfig, DataWorkloadArchitecture dataWorkload) {
		UsageModel usageModel = UsagemodelFactory.eINSTANCE.createUsageModel();
		for (ProvidedRole providedRole : system.getProvidedRoles_InterfaceProvidingEntity()) {
			if (providedRole instanceof OperationProvidedRole) {
				OperationProvidedRole operationProvidedRole = (OperationProvidedRole) providedRole;
				UsageScenario usageScenario = createUsageScenario(operationProvidedRole, appConfig, dataWorkload);
				usageModel.getUsageScenario_UsageModel().add(usageScenario);
			}
		}
		return usageModel;
	}
	
	/*
	 * We consider the following combinations for usage scenarios:
	 * Apache MapReduce: 		FileDataModel 	&& ClosedDataSource	&& BatchConfiguration
	 * Apache Spark Batch: 		FileDataModel 	&& ClosedDataSource	&& BatchConfiguration
	 * 
	 * Apache Spark Streaming:	RecordDataModel	&& OpenDataSource	&& MiniBatchConfiguration
	 * 
	 * Apache Flink: 			RecordDataModel	&& OpenDataSource 	&& StreamConfiguration
	 * Apache Storm: 			RecordDataModel	&& OpenDataSource 	&& StreamConfiguration
	*/
	private UsageScenario createUsageScenario(OperationProvidedRole operationProvidedRole, ApplicationConfiguration appConfig, DataWorkloadArchitecture dataWorkload) {
		DataModel dataModel = dataWorkload.getDataModels().get(0);
		DataSource dataSource = dataModel.getDataSource();
		boolean isFileDataModel = dataModel instanceof FileDataModel;
		boolean isRecordDataModel = dataModel instanceof RecordDataModel;
		boolean isClosedDataSource = dataSource instanceof SingleDataSource;
		boolean isOpenDataSource = dataSource instanceof OpenDataSource;
		boolean isBatchConfig = appConfig instanceof BatchConfiguration;
		boolean isMiniBatchConfig = appConfig instanceof MiniBatchConfiguration;
		boolean isStreamConfig = appConfig instanceof StreamConfiguration;
		
		if (isFileDataModel && isClosedDataSource && isBatchConfig) {
			return createBatchUsageScenario(operationProvidedRole, dataWorkload, appConfig);
		} else if (isRecordDataModel && isOpenDataSource && isMiniBatchConfig) {
			return createMiniBatchUsageScenario(operationProvidedRole, dataWorkload, appConfig);
		} else if (isRecordDataModel && isOpenDataSource && isStreamConfig){
			return createStreamUsageScenario(operationProvidedRole, dataWorkload, appConfig);
		} else {
			throw new RuntimeException("Could not match workload scenario"
					+ "dataModel " + dataModel.getClass() + ", "
					+ "dataSource " + dataSource.getClass() + ", "
					+ "appConfig " + appConfig.getClass());
		}
	}
	
	private UsageScenario createBatchUsageScenario(OperationProvidedRole providedRole, DataWorkloadArchitecture workload, ApplicationConfiguration appConfig) {
		BatchConfiguration batchConfig = (BatchConfiguration) appConfig;
		FileDataModel fileDataModel = (FileDataModel) workload.getDataModels().get(0);
		SingleDataSource closedDataSource = (SingleDataSource) fileDataModel.getDataSource();
		UsageScenario usageScenario = UsagemodelFactory.eINSTANCE.createUsageScenario();
		usageScenario.setEntityName("Scenario");
		ScenarioBehaviour scenarioBehaviour = this.scenarioBehaviorGenerator.createBatchScenarioBehaviour(providedRole, batchConfig, fileDataModel, closedDataSource);
		usageScenario.setScenarioBehaviour_UsageScenario(scenarioBehaviour);
		ClosedWorkload closedWorkload = this.workloadGenerator.createClosedWorkload(1, 0.0);
		usageScenario.setWorkload_UsageScenario(closedWorkload);
		return usageScenario;
	}
	
	private UsageScenario createMiniBatchUsageScenario(OperationProvidedRole providedRole, DataWorkloadArchitecture workload, ApplicationConfiguration appConfig) {
		MiniBatchConfiguration miniBatchConfig = (MiniBatchConfiguration) appConfig;
		RecordDataModel recordDataModel = (RecordDataModel) workload.getDataModels();
		OpenDataSource openDataSource = (OpenDataSource) recordDataModel.getDataSource();
		UsageScenario usageScenario = UsagemodelFactory.eINSTANCE.createUsageScenario();
		usageScenario.setEntityName("Scenario");
		ScenarioBehaviour scenarioBehaviour = this.scenarioBehaviorGenerator.createMiniBatchScenarioBehaviour(providedRole, miniBatchConfig, recordDataModel, openDataSource);
		usageScenario.setScenarioBehaviour_UsageScenario(scenarioBehaviour);
		ClosedWorkload closedWorkload = this.workloadGenerator.createClosedWorkload(1, miniBatchConfig.getMiniBatchInterval());
		usageScenario.setWorkload_UsageScenario(closedWorkload);
		return usageScenario;
	}
	
	private UsageScenario createStreamUsageScenario(OperationProvidedRole providedRole, DataWorkloadArchitecture workload, ApplicationConfiguration appConfig) {
		StreamConfiguration streamConfig = (StreamConfiguration) appConfig;
		RecordDataModel recordDataModel = (RecordDataModel) workload.getDataModels().get(0);
		OpenDataSource openDataSource = (OpenDataSource) recordDataModel.getDataSource();
		UsageScenario usageScenario = UsagemodelFactory.eINSTANCE.createUsageScenario();
		usageScenario.setEntityName("Scenario");
		ScenarioBehaviour scenarioBehaviour = this.scenarioBehaviorGenerator.createStreamScenarioBehaviour(providedRole, streamConfig, recordDataModel, openDataSource);
		usageScenario.setScenarioBehaviour_UsageScenario(scenarioBehaviour);
		OpenWorkload openWorkload = workloadGenerator.createOpenWorklad(openDataSource.getArrivalRate());
		usageScenario.setWorkload_UsageScenario(openWorkload);
		return usageScenario;
	}
}
