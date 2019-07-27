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

import org.fortiss.pmwt.pertract.dsl.model.application.BatchConfiguration;
import org.fortiss.pmwt.pertract.dsl.model.application.MiniBatchConfiguration;
import org.fortiss.pmwt.pertract.dsl.model.application.StreamConfiguration;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.FileDataModel;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.OpenDataSource;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.RecordDataModel;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.SingleDataSource;

import de.uka.ipd.sdq.pcm.core.CoreFactory;
import de.uka.ipd.sdq.pcm.core.PCMRandomVariable;
import de.uka.ipd.sdq.pcm.parameter.ParameterFactory;
import de.uka.ipd.sdq.pcm.parameter.VariableCharacterisation;
import de.uka.ipd.sdq.pcm.parameter.VariableCharacterisationType;
import de.uka.ipd.sdq.pcm.parameter.VariableUsage;
import de.uka.ipd.sdq.pcm.repository.OperationProvidedRole;
import de.uka.ipd.sdq.pcm.repository.OperationSignature;
import de.uka.ipd.sdq.pcm.usagemodel.AbstractUserAction;
import de.uka.ipd.sdq.pcm.usagemodel.EntryLevelSystemCall;
import de.uka.ipd.sdq.pcm.usagemodel.ScenarioBehaviour;
import de.uka.ipd.sdq.pcm.usagemodel.Start;
import de.uka.ipd.sdq.pcm.usagemodel.Stop;
import de.uka.ipd.sdq.pcm.usagemodel.UsagemodelFactory;
import de.uka.ipd.sdq.stoex.StoexFactory;
import de.uka.ipd.sdq.stoex.VariableReference;

public class ScenarioBehaviorGenerator {
	
	public ScenarioBehaviour createBatchScenarioBehaviour(OperationProvidedRole providedRole, BatchConfiguration appConfig, FileDataModel fileDataModel, SingleDataSource closedDataSource) {
		EntryLevelSystemCall entryLevelSystemCall = createEntryLevelSystemCall(providedRole);
		VariableUsage files = this.createVariableUsage("files", fileDataModel.getFiles().size());
		VariableUsage defaultSplitSize = this.createVariableUsage("defaultSplitSize", fileDataModel.getPartitionSize());
		VariableUsage sizePerFile = this.createVariableUsage("sizePerFile", fileDataModel.getFiles().get(0).getSize());
		VariableUsage executors = this.createVariableUsage("executors", appConfig.getExecutors());
		entryLevelSystemCall.getInputParameterUsages_EntryLevelSystemCall().add(files);
		entryLevelSystemCall.getInputParameterUsages_EntryLevelSystemCall().add(defaultSplitSize);
		entryLevelSystemCall.getInputParameterUsages_EntryLevelSystemCall().add(sizePerFile);
		entryLevelSystemCall.getInputParameterUsages_EntryLevelSystemCall().add(executors);
		return createScenarioBehaviour(entryLevelSystemCall);
	}
	
	public ScenarioBehaviour createMiniBatchScenarioBehaviour(OperationProvidedRole providedRole, MiniBatchConfiguration appConfig, RecordDataModel recordDataModel, OpenDataSource openDataSource) {
		EntryLevelSystemCall entryLevelSystemCall = createEntryLevelSystemCall(providedRole);
		double arrivalsPerMiniBatch = openDataSource.getArrivalRate() * appConfig.getMiniBatchInterval();
		VariableUsage files = this.createVariableUsage("records", arrivalsPerMiniBatch);
		VariableUsage partitions = this.createVariableUsage("partitions", recordDataModel.getDataSource().getPartitions());
		VariableUsage executors = this.createVariableUsage("executors", appConfig.getExecutors());
		entryLevelSystemCall.getInputParameterUsages_EntryLevelSystemCall().add(files);
		entryLevelSystemCall.getInputParameterUsages_EntryLevelSystemCall().add(partitions);
		entryLevelSystemCall.getInputParameterUsages_EntryLevelSystemCall().add(executors);
		return createScenarioBehaviour(entryLevelSystemCall);
	}
	
	public ScenarioBehaviour createStreamScenarioBehaviour(OperationProvidedRole providedRole, StreamConfiguration appConfig, RecordDataModel recordDataModel, OpenDataSource openDataSource) {
		EntryLevelSystemCall entryLevelSystemCall = createEntryLevelSystemCall(providedRole);
		VariableUsage files = this.createVariableUsage("records", 1);
		VariableUsage partitions = this.createVariableUsage("partitions", recordDataModel.getDataSource().getPartitions());
		VariableUsage executors = this.createVariableUsage("executors", appConfig.getExecutors());
		entryLevelSystemCall.getInputParameterUsages_EntryLevelSystemCall().add(files);
		entryLevelSystemCall.getInputParameterUsages_EntryLevelSystemCall().add(partitions);
		entryLevelSystemCall.getInputParameterUsages_EntryLevelSystemCall().add(executors);
		return createScenarioBehaviour(entryLevelSystemCall);
	}
	
	private EntryLevelSystemCall createEntryLevelSystemCall(OperationProvidedRole providedRole) {
		EntryLevelSystemCall entryLevelSystemCall = UsagemodelFactory.eINSTANCE.createEntryLevelSystemCall();
		entryLevelSystemCall.setEntityName("entryLevelSystemCall");
		entryLevelSystemCall.setProvidedRole_EntryLevelSystemCall(providedRole);
		if (providedRole.getProvidedInterface__OperationProvidedRole().getSignatures__OperationInterface().size() == 1) {
			OperationSignature signature = providedRole.getProvidedInterface__OperationProvidedRole().getSignatures__OperationInterface().get(0);
			entryLevelSystemCall.setOperationSignature__EntryLevelSystemCall(signature);
		}
		return entryLevelSystemCall;
	}
	
	private ScenarioBehaviour createScenarioBehaviour(EntryLevelSystemCall entryLevelSystemCall) {
		ScenarioBehaviour scenarioBehaviour = UsagemodelFactory.eINSTANCE.createScenarioBehaviour();
		scenarioBehaviour.setEntityName("Behavior");
		Start start = this.preAddStartAction(scenarioBehaviour, entryLevelSystemCall);
		Stop stop = this.postAddStopAction(scenarioBehaviour, entryLevelSystemCall);
		entryLevelSystemCall.setPredecessor(start);
		entryLevelSystemCall.setSuccessor(stop);
		scenarioBehaviour.getActions_ScenarioBehaviour().add(start);
		scenarioBehaviour.getActions_ScenarioBehaviour().add(entryLevelSystemCall);
		scenarioBehaviour.getActions_ScenarioBehaviour().add(stop);
		return scenarioBehaviour;
	}
	
	private Start preAddStartAction(ScenarioBehaviour scenarioBehaviour, AbstractUserAction successor) {
		Start start = UsagemodelFactory.eINSTANCE.createStart();
		start.setEntityName("Start");
		start.setSuccessor(successor);
		return start;
	}
	
	private Stop postAddStopAction(ScenarioBehaviour scenarioBehaviour, AbstractUserAction predecessor) {
		Stop stop = UsagemodelFactory.eINSTANCE.createStop();
		stop.setEntityName("Stop");
		stop.setPredecessor(predecessor);
		return stop;
	}
	
	private VariableUsage createVariableUsage(String parameterName, long value) {
		return createVariableUsage(parameterName, String.valueOf(value));
	}
	
	private VariableUsage createVariableUsage(String parameterName, double value) {
		return createVariableUsage(parameterName, String.valueOf(value));
	}
	
	private VariableUsage createVariableUsage(String parameterName, String value) {
		VariableUsage usage = ParameterFactory.eINSTANCE.createVariableUsage();
		
		PCMRandomVariable pcmVariable = CoreFactory.eINSTANCE.createPCMRandomVariable();
		pcmVariable.setSpecification(value);
		VariableCharacterisation characterisation = ParameterFactory.eINSTANCE.createVariableCharacterisation();
		characterisation.setSpecification_VariableCharacterisation(pcmVariable);
		characterisation.setType(VariableCharacterisationType.VALUE);
		usage.getVariableCharacterisation_VariableUsage().add(characterisation);
		
		PCMRandomVariable pcmByteSizeVariable = CoreFactory.eINSTANCE.createPCMRandomVariable();
		pcmByteSizeVariable.setSpecification(value);
		VariableCharacterisation byteSizeCharacterisation = ParameterFactory.eINSTANCE.createVariableCharacterisation();
		byteSizeCharacterisation.setSpecification_VariableCharacterisation(pcmByteSizeVariable);
		byteSizeCharacterisation.setType(VariableCharacterisationType.BYTESIZE);
		usage.getVariableCharacterisation_VariableUsage().add(byteSizeCharacterisation);
		
		VariableReference reference = StoexFactory.eINSTANCE.createVariableReference();
		reference.setReferenceName(parameterName);
		usage.setNamedReference__VariableUsage(reference);
		return usage;
	}
	
}
