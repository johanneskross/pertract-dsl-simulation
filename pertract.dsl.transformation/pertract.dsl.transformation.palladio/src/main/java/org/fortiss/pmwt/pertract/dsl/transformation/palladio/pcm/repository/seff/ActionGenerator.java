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

import java.util.ArrayList;
import java.util.List;

import org.fortiss.pmwt.pertract.dsl.model.application.ExecutionNode;
import org.fortiss.pmwt.pertract.dsl.model.application.ProcessingType;
import org.fortiss.pmwt.pertract.dsl.model.application.ResourceDemand;
import org.fortiss.pmwt.pertract.dsl.model.application.ResourceProfile;
import org.fortiss.pmwt.pertract.dsl.transformation.palladio.pcm.resourceenvironment.ResourceRepositorySource;

import de.uka.ipd.sdq.pcm.core.CoreFactory;
import de.uka.ipd.sdq.pcm.core.PCMRandomVariable;
import de.uka.ipd.sdq.pcm.parameter.VariableUsage;
import de.uka.ipd.sdq.pcm.repository.InfrastructureRequiredRole;
import de.uka.ipd.sdq.pcm.repository.InfrastructureSignature;
import de.uka.ipd.sdq.pcm.repository.OperationRequiredRole;
import de.uka.ipd.sdq.pcm.repository.OperationSignature;
import de.uka.ipd.sdq.pcm.resourcetype.ProcessingResourceType;
import de.uka.ipd.sdq.pcm.seff.AbstractAction;
import de.uka.ipd.sdq.pcm.seff.InternalAction;
import de.uka.ipd.sdq.pcm.seff.seff_performance.InfrastructureCall;
import de.uka.ipd.sdq.pcm.seff.seff_performance.ParametricResourceDemand;
import de.uka.ipd.sdq.pcm.seff.seff_performance.SeffPerformanceFactory;

public abstract class ActionGenerator {
	
	protected SeffGenerator seffGenerator;
	protected SeffArchetypeProvider provider;
	private ResourceRepositorySource resourceRepositorySource;
	
	public ActionGenerator(SeffGenerator seffGenerator, SeffArchetypeProvider provider) {
		this.seffGenerator = seffGenerator;
		this.provider = provider;
		this.resourceRepositorySource = new ResourceRepositorySource();
	}
	
	abstract AbstractAction createDelegatingAction(OperationSignature key, OperationRequiredRole value, double transmissionFactor);
	
	abstract AbstractAction createDelegatingExecutionAction(OperationSignature operationSignature, OperationRequiredRole operationRequiredRole, ExecutionNode node);
	
	abstract AbstractAction createExecutingAction(OperationRequiredRole taskRole, OperationSignature taskSignature, ExecutionNode node);
	
	public List<InternalAction> createResourceDemandActions(ResourceProfile resourceProfile) {
		List<InternalAction> actions = new ArrayList<InternalAction>();
		List<InternalAction> delay = createResourceDemandAction(resourceProfile, ProcessingType.DELAY);
		actions.addAll(delay);
		List<InternalAction> cpu = createResourceDemandAction(resourceProfile, ProcessingType.CPU);
		actions.addAll(cpu);
		return actions;
	}
	
	public List<InternalAction> createResourceDemandAction(ResourceProfile resourceProfile, ProcessingType type) {
		List<InternalAction> actions = new ArrayList<InternalAction>();
		for (ResourceDemand rd : resourceProfile.getResourceDemands()) {
			if (rd.getProcessingType().equals(type.getName())) {
				InternalAction action = provider.createInternalAction(type.getName());
				ParametricResourceDemand demand = SeffPerformanceFactory.eINSTANCE.createParametricResourceDemand();
				ProcessingResourceType resourceType = (ProcessingResourceType) getProcessingResourceType(type);
				demand.setRequiredResource_ParametricResourceDemand(resourceType);
				PCMRandomVariable randomVariable = CoreFactory.eINSTANCE.createPCMRandomVariable();
				randomVariable.setSpecification(rd.getRandomVariable());
				demand.setSpecification_ParametericResourceDemand(randomVariable);
				action.getResourceDemand_Action().add(demand);
				actions.add(action);
			}
		}
		return actions;
	}
	
	private ProcessingResourceType getProcessingResourceType(ProcessingType type) {
		if (type.equals(ProcessingType.CPU)) {
			return (ProcessingResourceType) this.resourceRepositorySource.getCpuResourceType();
		} else if (type.equals(ProcessingType.DELAY)) {
			return  (ProcessingResourceType) this.resourceRepositorySource.getDelayResourceType();
		} else if (type.equals(ProcessingType.DRIVE_READ) || type.equals(ProcessingType.DRIVE_WRITE)) {
			return  (ProcessingResourceType) this.resourceRepositorySource.getHddResourceType();
		} else {
			throw new RuntimeException("Wrong processing type " + type.getName());
		}
	}
	
	public InternalAction createAllocCoreInternalAction(InfrastructureRequiredRole appResourceRole, InfrastructureSignature allocSignature) {
		InternalAction alloc = provider.createInternalAction("alloc");
		InfrastructureCall allocCall = provider.createInfrastructureCall(appResourceRole, allocSignature);
		VariableUsage allocUsage = provider.createVariableUsage("amount", "1");
		allocCall.getInputVariableUsages__CallAction().add(allocUsage);
		PCMRandomVariable allocVariable = provider.createPCMRandomVariable("1");
		allocCall.setNumberOfCalls__InfrastructureCall(allocVariable);
		alloc.getInfrastructureCall__Action().add(allocCall);
		return alloc;
	}
	
	public InternalAction createFreeCoreInternalAction(InfrastructureRequiredRole appResourceRole, InfrastructureSignature freeSignature) {
		InternalAction free = provider.createInternalAction("free");
		InfrastructureCall freeCall = provider.createInfrastructureCall(appResourceRole, freeSignature);
		VariableUsage freeUsage = provider.createVariableUsage("amount", "1");
		freeCall.getInputVariableUsages__CallAction().add(freeUsage);
		PCMRandomVariable freeVariable = provider.createPCMRandomVariable("1");
		freeCall.setNumberOfCalls__InfrastructureCall(freeVariable);
		free.getInfrastructureCall__Action().add(freeCall);
		return free;
	}

}
