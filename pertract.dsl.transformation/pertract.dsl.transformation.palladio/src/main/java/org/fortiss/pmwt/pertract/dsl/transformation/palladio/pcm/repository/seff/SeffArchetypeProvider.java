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

import de.uka.ipd.sdq.pcm.core.CoreFactory;
import de.uka.ipd.sdq.pcm.core.PCMRandomVariable;
import de.uka.ipd.sdq.pcm.parameter.ParameterFactory;
import de.uka.ipd.sdq.pcm.parameter.VariableCharacterisation;
import de.uka.ipd.sdq.pcm.parameter.VariableCharacterisationType;
import de.uka.ipd.sdq.pcm.parameter.VariableUsage;
import de.uka.ipd.sdq.pcm.repository.BasicComponent;
import de.uka.ipd.sdq.pcm.repository.InfrastructureRequiredRole;
import de.uka.ipd.sdq.pcm.repository.InfrastructureSignature;
import de.uka.ipd.sdq.pcm.repository.Signature;
import de.uka.ipd.sdq.pcm.seff.InternalAction;
import de.uka.ipd.sdq.pcm.seff.ResourceDemandingSEFF;
import de.uka.ipd.sdq.pcm.seff.SeffFactory;
import de.uka.ipd.sdq.pcm.seff.StartAction;
import de.uka.ipd.sdq.pcm.seff.StopAction;
import de.uka.ipd.sdq.pcm.seff.seff_performance.InfrastructureCall;
import de.uka.ipd.sdq.pcm.seff.seff_performance.SeffPerformanceFactory;
import de.uka.ipd.sdq.stoex.StoexFactory;
import de.uka.ipd.sdq.stoex.VariableReference;

public class SeffArchetypeProvider {

	public StartAction createStartAction(String name) {
		StartAction startAction = SeffFactory.eINSTANCE.createStartAction();
		startAction.setEntityName(name);
		return startAction;
	}
	
	public StopAction createStopAction(String name) {
		StopAction stopAction = SeffFactory.eINSTANCE.createStopAction();
		stopAction.setEntityName(name);
		return stopAction;
	}
	
	public VariableUsage createVariableUsage(String parameterName, String value) {
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
	
	public PCMRandomVariable createPCMRandomVariable(String specification) {
		PCMRandomVariable variable = CoreFactory.eINSTANCE.createPCMRandomVariable();
		variable.setSpecification(specification);
		return variable;
	}
	
	public InternalAction createInternalAction(String name) {
		InternalAction internalAction = SeffFactory.eINSTANCE.createInternalAction();
		internalAction.setEntityName(name);
		return internalAction;
	}
	
	public InfrastructureCall createInfrastructureCall(InfrastructureRequiredRole requiredRole, InfrastructureSignature signature) {
		InfrastructureCall infrastructureCall = SeffPerformanceFactory.eINSTANCE.createInfrastructureCall();
		infrastructureCall.setRequiredRole__InfrastructureCall(requiredRole);
		infrastructureCall.setSignature__InfrastructureCall(signature);
		return infrastructureCall;
	}
	
	public ResourceDemandingSEFF createResourceDemandingSEFF(BasicComponent component, Signature signature) {
		ResourceDemandingSEFF rdseff = SeffFactory.eINSTANCE.createResourceDemandingSEFF();
		rdseff.setBasicComponent_ServiceEffectSpecification(component);
		rdseff.setDescribedService__SEFF(signature);
		return rdseff;
	}
}
