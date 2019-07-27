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
package org.fortiss.pmwt.pertract.dsl.transformation.palladio.pcm.repository.seff;

import org.fortiss.pmwt.pertract.dsl.model.application.ApplicationExecutionArchitecture;
import org.fortiss.pmwt.pertract.dsl.model.application.ExecutionNode;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.RecordDataModel;

import de.uka.ipd.sdq.pcm.core.CoreFactory;
import de.uka.ipd.sdq.pcm.core.PCMRandomVariable;
import de.uka.ipd.sdq.pcm.parameter.VariableUsage;
import de.uka.ipd.sdq.pcm.repository.OperationRequiredRole;
import de.uka.ipd.sdq.pcm.repository.OperationSignature;
import de.uka.ipd.sdq.pcm.seff.AbstractAction;
import de.uka.ipd.sdq.pcm.seff.DistributedCallAction;
import de.uka.ipd.sdq.pcm.seff.ExternalCallAction;
import de.uka.ipd.sdq.pcm.seff.SeffFactory;

public class RecordBasedActionGenerator extends ActionGenerator {
	
	private RecordDataModel recordDataModel;

	public RecordBasedActionGenerator(SeffGenerator seffGenerator, SeffArchetypeProvider provider, RecordDataModel recordDataModel) {
		super(seffGenerator, provider);
		this.recordDataModel = recordDataModel;
	}
	
	@Override
	public ExternalCallAction createDelegatingAction(OperationSignature operationSignature, OperationRequiredRole operationRequiredRole, double transmissionFactor) {
		ExternalCallAction externalAction = SeffFactory.eINSTANCE.createExternalCallAction();
		externalAction.setEntityName(operationSignature.getEntityName());
		externalAction.setCalledService_ExternalService(operationSignature);
		externalAction.setRole_ExternalService(operationRequiredRole);
		VariableUsage records = provider.createVariableUsage("records", "records.VALUE");
		VariableUsage partitions = provider.createVariableUsage("partitions", "partitions.VALUE");
		VariableUsage executors = provider.createVariableUsage("executors", "executors.VALUE");
		externalAction.getInputVariableUsages__CallAction().add(records);
		externalAction.getInputVariableUsages__CallAction().add(partitions);
		externalAction.getInputVariableUsages__CallAction().add(executors);
		return externalAction;
	}
	
	@Override
	public DistributedCallAction createDelegatingExecutionAction(OperationSignature operationSignature, OperationRequiredRole operationRequiredRole, ExecutionNode node) {
		return createDistributedCallAction(operationSignature, operationRequiredRole, getParallelism(node), getParallelism(node));
	}
	
	private String getParallelism(ExecutionNode node) {
		if (node.getParallelism() > 0) {
			return String.valueOf(node.getParallelism());
		} else {
			ExecutionNode parent = node;
			while (node.getParent() != null) {
				parent = node.getParent();
			}
			ApplicationExecutionArchitecture architecture = (ApplicationExecutionArchitecture) parent.eContainer();
			return String.valueOf(architecture.getApplicationConfiguration().getDefaultParallelism());
		}
	}

	private DistributedCallAction createDistributedCallAction(OperationSignature operationSignature, OperationRequiredRole operationRequiredRole, String totalForkCount, String simultaneousForkCount) {
		DistributedCallAction distributedCallAction = SeffFactory.eINSTANCE.createDistributedCallAction();
		distributedCallAction.setEntityName("distributedCall");
		distributedCallAction.setCalledService_ExternalService(operationSignature);
		distributedCallAction.setRole_ExternalService(operationRequiredRole);
		PCMRandomVariable totalForkCountVariable = CoreFactory.eINSTANCE.createPCMRandomVariable();
		totalForkCountVariable.setSpecification(totalForkCount);
		PCMRandomVariable simultaneousForkCountVariable = CoreFactory.eINSTANCE.createPCMRandomVariable();
		simultaneousForkCountVariable.setSpecification(simultaneousForkCount);
		distributedCallAction.setTotalForkCount_DistributedAction(totalForkCountVariable);
		distributedCallAction.setSimultaneousForkCount_DistributedAction(simultaneousForkCountVariable);
		VariableUsage records = provider.createVariableUsage("records", "records.VALUE / partitions.VALUE");
		distributedCallAction.getInputVariableUsages__CallAction().add(records);
		return distributedCallAction;
	}
	
	@Override
	public AbstractAction createExecutingAction(OperationRequiredRole taskRole, OperationSignature taskSignature, ExecutionNode node) {
		ExternalCallAction externalAction = SeffFactory.eINSTANCE.createExternalCallAction();
		externalAction.setEntityName("runTasks");
		externalAction.setRole_ExternalService(taskRole);
		externalAction.setCalledService_ExternalService(taskSignature);
		VariableUsage dataSize = provider.createVariableUsage("dataSize", "dataSize.VALUE"); // TODO: should be records 
		externalAction.getInputVariableUsages__CallAction().add(dataSize);
		return externalAction;
	}

}
