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

import org.apache.commons.lang3.tuple.Triple;
import org.fortiss.pmwt.pertract.dsl.model.application.ExecutionNode;
import org.fortiss.pmwt.pertract.dsl.model.application.ProcessingType;
import org.fortiss.pmwt.pertract.dsl.model.application.ResourceProfile;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.DataModel;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.FileDataModel;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.RecordDataModel;

import de.uka.ipd.sdq.pcm.repository.BasicComponent;
import de.uka.ipd.sdq.pcm.repository.InfrastructureRequiredRole;
import de.uka.ipd.sdq.pcm.repository.InfrastructureSignature;
import de.uka.ipd.sdq.pcm.repository.OperationRequiredRole;
import de.uka.ipd.sdq.pcm.repository.OperationSignature;
import de.uka.ipd.sdq.pcm.repository.PassiveResource;
import de.uka.ipd.sdq.pcm.repository.Signature;
import de.uka.ipd.sdq.pcm.seff.AbstractAction;
import de.uka.ipd.sdq.pcm.seff.AcquireAction;
import de.uka.ipd.sdq.pcm.seff.InternalAction;
import de.uka.ipd.sdq.pcm.seff.ReleaseAction;
import de.uka.ipd.sdq.pcm.seff.ResourceDemandingBehaviour;
import de.uka.ipd.sdq.pcm.seff.ResourceDemandingSEFF;
import de.uka.ipd.sdq.pcm.seff.SeffFactory;
import de.uka.ipd.sdq.pcm.seff.StartAction;
import de.uka.ipd.sdq.pcm.seff.StopAction;

public class SeffGenerator {
	
	private SeffArchetypeProvider provider;
	private ActionGenerator actionGenerator;
	
	public SeffGenerator(DataModel dataModel) {
		this.provider = new SeffArchetypeProvider();
		if (dataModel instanceof FileDataModel) {
			this.actionGenerator = new FileBasedActionGenerator(this, this.provider, (FileDataModel) dataModel);
		} else {
			this.actionGenerator = new RecordBasedActionGenerator(this, this.provider, (RecordDataModel) dataModel);
		}
	}
	
	public ResourceDemandingSEFF createDelegatingCompositeSeff(BasicComponent component, OperationSignature signature, List<Triple<OperationSignature,OperationRequiredRole,Double>> calls) {
		ResourceDemandingSEFF rdseff = provider.createResourceDemandingSEFF(component, signature);
		StartAction startAction = provider.createStartAction("startAction");
		AbstractAction predecessor = startAction;
		double precedingFactors = 1.0;
		for (Triple<OperationSignature,OperationRequiredRole,Double> triple : calls) {
			precedingFactors *= triple.getRight();
			AbstractAction action = actionGenerator.createDelegatingAction(triple.getLeft(), triple.getMiddle(), precedingFactors);
			predecessor.setSuccessor_AbstractAction(action);
			action.setPredecessor_AbstractAction(predecessor);
			rdseff.getSteps_Behaviour().add(predecessor);
			predecessor = action;
		}
		rdseff.getSteps_Behaviour().add(predecessor);
		StopAction stopAction = provider.createStopAction("stopAction");
		stopAction.setPredecessor_AbstractAction(predecessor);
		rdseff.getSteps_Behaviour().add(stopAction);
		component.getServiceEffectSpecifications__BasicComponent().add(rdseff);
		return rdseff;
	}
	
	public ResourceDemandingSEFF createDelegatingExecutionSeff(BasicComponent component, Signature signature, OperationSignature operationSignature, OperationRequiredRole operationRequiredRole, ExecutionNode node){
		ResourceDemandingSEFF rdseff = provider.createResourceDemandingSEFF(component, signature);
		StartAction startAction = provider.createStartAction("startAction");
		StopAction stopAction = provider.createStopAction("stopAction");
		AbstractAction action = actionGenerator.createDelegatingExecutionAction(operationSignature, operationRequiredRole, node);
		connect(rdseff, startAction, action, stopAction);
		component.getServiceEffectSpecifications__BasicComponent().add(rdseff);
		return rdseff;
	}
	
	public ResourceDemandingSEFF createExecutingSeff(BasicComponent component, OperationSignature signature, InfrastructureRequiredRole appResourceRole, InfrastructureSignature allocSignature, InfrastructureSignature freeSignature, OperationRequiredRole taskRole, OperationSignature taskSignature, ExecutionNode node){
		ResourceDemandingSEFF rdseff = provider.createResourceDemandingSEFF(component, signature);
		StartAction startAction = provider.createStartAction("startAction");
		StopAction stopAction = provider.createStopAction("stopAction");
		InternalAction alloc = actionGenerator.createAllocCoreInternalAction(appResourceRole, allocSignature);
		InternalAction free = actionGenerator.createFreeCoreInternalAction(appResourceRole, freeSignature);
		AbstractAction action = actionGenerator.createExecutingAction(taskRole, taskSignature, node);
		connect(rdseff, startAction, alloc, action, free, stopAction);
		component.getServiceEffectSpecifications__BasicComponent().add(rdseff);
		return rdseff;
	}
	
	public ResourceDemandingSEFF createTaskSeff(BasicComponent component, OperationSignature signature, ResourceProfile resourceProfile){
		ResourceDemandingSEFF rdseff = provider.createResourceDemandingSEFF(component, signature);
		StartAction startAction = provider.createStartAction("startAction");
		StopAction stopAction = provider.createStopAction("stopAction");
		List<InternalAction> demands = actionGenerator.createResourceDemandActions(resourceProfile);
		List<AbstractAction> actions = new ArrayList<AbstractAction>();
		actions.add(startAction);
		actions.addAll(demands);
		actions.add(stopAction);
		connect(rdseff, actions.toArray(new AbstractAction[actions.size()]));
		component.getServiceEffectSpecifications__BasicComponent().add(rdseff);
		return rdseff;
	}
	
	public ResourceDemandingSEFF createAllocCoreSeff(BasicComponent component, Signature signature){
		ResourceDemandingSEFF rdseff = provider.createResourceDemandingSEFF(component, signature);
		StartAction startAction = provider.createStartAction("startAction");
		StopAction stopAction = provider.createStopAction("stopAction");
		AcquireAction acquireAction = SeffFactory.eINSTANCE.createAcquireAction();
		PassiveResource resource = component.getPassiveResource_BasicComponent().get(0);
		acquireAction.setPassiveresource_AcquireAction(resource);
		acquireAction.setEntityName("allocCore");
		connect(rdseff, startAction, acquireAction, stopAction);
		component.getServiceEffectSpecifications__BasicComponent().add(rdseff);
		return rdseff;
	}
	
	public ResourceDemandingSEFF addFreeCoreSeff(BasicComponent component, Signature signature){
		ResourceDemandingSEFF rdseff = provider.createResourceDemandingSEFF(component, signature);
		StartAction startAction = provider.createStartAction("startAction");
		StopAction stopAction = provider.createStopAction("stopAction");
		ReleaseAction releaseAction = SeffFactory.eINSTANCE.createReleaseAction();
		PassiveResource resource = component.getPassiveResource_BasicComponent().get(0);
		releaseAction.setPassiveResource_ReleaseAction(resource);
		releaseAction.setEntityName("freeCore");
		connect(rdseff, startAction, releaseAction, stopAction);
		component.getServiceEffectSpecifications__BasicComponent().add(rdseff);
		return rdseff;
	}
	
	protected void connect(ResourceDemandingBehaviour behavior, AbstractAction... actions) {
		int index = 0;
		AbstractAction previousAction = null;
		for (AbstractAction action : actions) {
			if (index > 0) {
				previousAction.setSuccessor_AbstractAction(action);
				action.setPredecessor_AbstractAction(previousAction);
				behavior.getSteps_Behaviour().add(previousAction);
			}
			previousAction = action;
			index++;
		}
		behavior.getSteps_Behaviour().add(previousAction);
	}
	
}
