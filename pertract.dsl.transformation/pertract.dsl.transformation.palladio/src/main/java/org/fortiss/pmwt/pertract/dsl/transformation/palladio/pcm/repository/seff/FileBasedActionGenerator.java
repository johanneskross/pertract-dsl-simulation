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

import org.fortiss.pmwt.pertract.dsl.model.application.ExecutionNode;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.FileDataModel;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.FileSpecification;

import de.uka.ipd.sdq.pcm.core.CoreFactory;
import de.uka.ipd.sdq.pcm.core.PCMRandomVariable;
import de.uka.ipd.sdq.pcm.parameter.VariableUsage;
import de.uka.ipd.sdq.pcm.repository.OperationRequiredRole;
import de.uka.ipd.sdq.pcm.repository.OperationSignature;
import de.uka.ipd.sdq.pcm.seff.AbstractAction;
import de.uka.ipd.sdq.pcm.seff.BranchAction;
import de.uka.ipd.sdq.pcm.seff.DistributedCallAction;
import de.uka.ipd.sdq.pcm.seff.ExternalCallAction;
import de.uka.ipd.sdq.pcm.seff.GuardedBranchTransition;
import de.uka.ipd.sdq.pcm.seff.ResourceDemandingBehaviour;
import de.uka.ipd.sdq.pcm.seff.SeffFactory;
import de.uka.ipd.sdq.pcm.seff.StartAction;
import de.uka.ipd.sdq.pcm.seff.StopAction;

public class FileBasedActionGenerator extends ActionGenerator{
	
	private FileDataModel fileDataModel;
	
	public FileBasedActionGenerator(SeffGenerator seffGenerator, SeffArchetypeProvider provider, FileDataModel fileDataModel) {
		super(seffGenerator, provider);
		this.fileDataModel = fileDataModel;
	}

	@Override
	public ExternalCallAction createDelegatingAction(OperationSignature operationSignature, OperationRequiredRole operationRequiredRole, double transmissionFactor) {
		ExternalCallAction externalAction = SeffFactory.eINSTANCE.createExternalCallAction();
		externalAction.setEntityName(operationSignature.getEntityName());
		externalAction.setCalledService_ExternalService(operationSignature);
		externalAction.setRole_ExternalService(operationRequiredRole);
		VariableUsage files = provider.createVariableUsage("files", "files.VALUE");
		VariableUsage defaultSplitSize = provider.createVariableUsage("defaultSplitSize", "defaultSplitSize.VALUE * " + transmissionFactor);
		VariableUsage sizePerFile = provider.createVariableUsage("sizePerFile", "sizePerFile.VALUE");
		VariableUsage executors = provider.createVariableUsage("executors", "executors.VALUE");
		externalAction.getInputVariableUsages__CallAction().add(files);
		externalAction.getInputVariableUsages__CallAction().add(defaultSplitSize);
		externalAction.getInputVariableUsages__CallAction().add(sizePerFile);
		externalAction.getInputVariableUsages__CallAction().add(executors);
		return externalAction;
	}
	
	@Override
	public DistributedCallAction createDelegatingExecutionAction(OperationSignature operationSignature, OperationRequiredRole operationRequiredRole, ExecutionNode node) {
		if (node.isSpout()) {
			return createDelegatingExecutionForSpout(operationSignature, operationRequiredRole, node);
		} else {
			if (node.getParallelism() != 0) {
				String parallelism = String.valueOf(node.getParallelism());
				return createDistributedCallAction(operationSignature, operationRequiredRole, parallelism, parallelism, "true");
			} else {
				return createDistributedCallAction(operationSignature, operationRequiredRole, "1", "1", "true");
			}
		}
	}
	
	private DistributedCallAction createDelegatingExecutionForSpout(OperationSignature operationSignature, OperationRequiredRole operationRequiredRole, ExecutionNode node) {
		int splits = calculateSplits();
		double splitProbability = calculateSplitPropability();
		String totalForkCount = String.valueOf(splits);
		String simultaneousForkCount = String.valueOf(splits);
		//String totalForkCount = "files.VALUE * ((sizePerFile.VALUE / defaultSplitSize.VALUE) + (sizePerFile.VALUE%defaultSplitSize.VALUE == 0.00 ? 0.00 : 1.00))";
		//String simultaneousForkCount = "files.VALUE * ((sizePerFile.VALUE / defaultSplitSize.VALUE) + (sizePerFile.VALUE%defaultSplitSize.VALUE == 0.00 ? 0.0 : 1.00))";
		String isDefaultSplit = "BoolPMF[ (true;" + splitProbability + ") (false;" + (1-splitProbability) + ") ]";
		return createDistributedCallAction(operationSignature, operationRequiredRole, totalForkCount, simultaneousForkCount, isDefaultSplit);
	}

	@Override
	public AbstractAction createExecutingAction(OperationRequiredRole taskRole, OperationSignature taskSignature, ExecutionNode node) {
		if (node.isSpout()) {
			return createExecutingActionForSpout(taskRole, taskSignature);
		} else {
			return createExecutingExternalCallAction(taskRole, taskSignature, "defaultSplitSize.VALUE");
		}
	}
	
	public BranchAction createExecutingActionForSpout(OperationRequiredRole taskRole, OperationSignature taskSignature) {
		BranchAction branch = SeffFactory.eINSTANCE.createBranchAction();
		branch.setEntityName("Split");
		GuardedBranchTransition defaultSplit = createBranchTransition("defaultCase", "isDefaultSplit.VALUE == true", "defaultSplitSize.VALUE", taskRole, taskSignature);
		GuardedBranchTransition remainingSplit = createBranchTransition("splitFileCase", "isDefaultSplit.VALUE == false", "remainingSplitSize.VALUE", taskRole, taskSignature);
		branch.getBranches_Branch().add(defaultSplit);
		branch.getBranches_Branch().add(remainingSplit);
		return branch;
	}
	
	private DistributedCallAction createDistributedCallAction(OperationSignature signature, OperationRequiredRole requiredRole, String totalForkCount, String simultaneousForkCount, String isDefaultSplit) {
		DistributedCallAction distributedCallAction = SeffFactory.eINSTANCE.createDistributedCallAction();
		distributedCallAction.setEntityName("distributedCall");
		distributedCallAction.setCalledService_ExternalService(signature);
		distributedCallAction.setRole_ExternalService(requiredRole);
		PCMRandomVariable totalForkCountVariable = CoreFactory.eINSTANCE.createPCMRandomVariable();
		totalForkCountVariable.setSpecification(totalForkCount);
		PCMRandomVariable simultaneousForkCountVariable = CoreFactory.eINSTANCE.createPCMRandomVariable();
		simultaneousForkCountVariable.setSpecification(simultaneousForkCount);
		distributedCallAction.setTotalForkCount_DistributedAction(totalForkCountVariable);
		distributedCallAction.setSimultaneousForkCount_DistributedAction(simultaneousForkCountVariable);
		VariableUsage isDefaultSplitSize = provider.createVariableUsage("isDefaultSplit", isDefaultSplit);
		VariableUsage defaultSplitSize = provider.createVariableUsage("defaultSplitSize", "defaultSplitSize.VALUE * " + calculateSplits() + " / " + totalForkCount);
		VariableUsage remainingSplitSize = provider.createVariableUsage("remainingSplitSize", "sizePerFile.VALUE % defaultSplitSize.VALUE");
		VariableUsage executors = provider.createVariableUsage("executors", "executors.VALUE");
		distributedCallAction.getInputVariableUsages__CallAction().add(isDefaultSplitSize);
		distributedCallAction.getInputVariableUsages__CallAction().add(defaultSplitSize);
		distributedCallAction.getInputVariableUsages__CallAction().add(remainingSplitSize);
		distributedCallAction.getInputVariableUsages__CallAction().add(executors);
		return distributedCallAction;
	}
	
	private ExternalCallAction createExecutingExternalCallAction(OperationRequiredRole taskRole, OperationSignature taskSignature, String dataSizeVariable) {
		ExternalCallAction externalAction = SeffFactory.eINSTANCE.createExternalCallAction();
		externalAction.setEntityName("runTasks");
		externalAction.setRole_ExternalService(taskRole);
		externalAction.setCalledService_ExternalService(taskSignature);
		VariableUsage dataSize = provider.createVariableUsage("dataSize", dataSizeVariable);
		VariableUsage executors = provider.createVariableUsage("executors", "executors.VALUE");
		externalAction.getInputVariableUsages__CallAction().add(dataSize);
		externalAction.getInputVariableUsages__CallAction().add(executors);
		return externalAction;
	}
	
	private GuardedBranchTransition createBranchTransition(String name, String branchCondition, String dataSizeVariable, OperationRequiredRole taskRole, OperationSignature taskSignature) {
		GuardedBranchTransition transition = SeffFactory.eINSTANCE.createGuardedBranchTransition();
		transition.setEntityName(name);
		PCMRandomVariable condition = CoreFactory.eINSTANCE.createPCMRandomVariable();
		condition.setSpecification(branchCondition);
		transition.setBranchCondition_GuardedBranchTransition(condition);
		ResourceDemandingBehaviour behaviour = SeffFactory.eINSTANCE.createResourceDemandingBehaviour();
		StartAction startAction = provider.createStartAction("startAction");
		StopAction stopAction = provider.createStopAction("stopAction");
		ExternalCallAction externalAction = createExecutingExternalCallAction(taskRole, taskSignature, dataSizeVariable);
		seffGenerator.connect(behaviour, startAction, externalAction, stopAction);
		transition.setBranchBehaviour_BranchTransition(behaviour);
		return transition;
	}
	
	private double calculateSplitPropability() {
		double defaultBlocks = 0;
		double totalBlocks = 0;
		for( FileSpecification file : fileDataModel.getFiles()){
			defaultBlocks += Math.floor((double)file.getSize() / fileDataModel.getPartitionSize());
			totalBlocks += Math.ceil((double)file.getSize() / fileDataModel.getPartitionSize());
		}
		double splitProbability = defaultBlocks/totalBlocks;
		return Math.floor(splitProbability * 10000) / 10000;
	}
	
	private int calculateSplits() {
		double totalBlocks = 0;
		for( FileSpecification file : fileDataModel.getFiles()){
			totalBlocks += Math.ceil((double)file.getSize() / fileDataModel.getPartitionSize());
		}
		return (int) totalBlocks;
	}


}
