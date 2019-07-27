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
package org.fortiss.pmwt.pertract.dsl.transformation.palladio.pcm.repository;

import de.uka.ipd.sdq.pcm.repository.BasicComponent;
import de.uka.ipd.sdq.pcm.repository.InfrastructureInterface;
import de.uka.ipd.sdq.pcm.repository.InfrastructureProvidedRole;
import de.uka.ipd.sdq.pcm.repository.InfrastructureRequiredRole;
import de.uka.ipd.sdq.pcm.repository.OperationInterface;
import de.uka.ipd.sdq.pcm.repository.OperationProvidedRole;
import de.uka.ipd.sdq.pcm.repository.OperationRequiredRole;
import de.uka.ipd.sdq.pcm.repository.RepositoryFactory;

public class RepositoryRoleArchetypeProvider {

	protected void addOperationProvidedRole(BasicComponent basicComponent, OperationInterface operationInterface) {
		OperationProvidedRole operationProvidedRole = this.createOperationProvidedRole(operationInterface);
		basicComponent.getProvidedRoles_InterfaceProvidingEntity().add(operationProvidedRole);
	}
	
	protected OperationProvidedRole createOperationProvidedRole(OperationInterface operationInterface) {
		OperationProvidedRole operationProvidedRole = RepositoryFactory.eINSTANCE.createOperationProvidedRole();
		operationProvidedRole.setEntityName("Provided_" + operationInterface.getEntityName());
		operationProvidedRole.setProvidedInterface__OperationProvidedRole(operationInterface);
		return operationProvidedRole;
	}
	
	protected OperationRequiredRole addOperationRequiredRole(BasicComponent basicComponent, OperationInterface operationInterface) {
		OperationRequiredRole operationRequiredRole = this.createOperationRequiredRole(operationInterface);
		basicComponent.getRequiredRoles_InterfaceRequiringEntity().add(operationRequiredRole);
		return operationRequiredRole;
	}
	
	protected OperationRequiredRole createOperationRequiredRole(OperationInterface operationInterface) {
		OperationRequiredRole operationRequiredRole = RepositoryFactory.eINSTANCE.createOperationRequiredRole();
		operationRequiredRole.setEntityName("Required_" +  operationInterface.getEntityName());
		operationRequiredRole.setRequiredInterface__OperationRequiredRole(operationInterface);
		return operationRequiredRole;
	}
	
	protected InfrastructureProvidedRole addInfrastructureProvidedRole(BasicComponent basicComponent, InfrastructureInterface infrastructureInterface) {
		InfrastructureProvidedRole infrastructureProvidedRole = this.createInfrastructureProvidedRole(infrastructureInterface);
		basicComponent.getProvidedRoles_InterfaceProvidingEntity().add(infrastructureProvidedRole);
		return infrastructureProvidedRole;
	}
	
	protected InfrastructureProvidedRole createInfrastructureProvidedRole(InfrastructureInterface infrastructureInterface) {
		InfrastructureProvidedRole infrastructureProvidedRole = RepositoryFactory.eINSTANCE.createInfrastructureProvidedRole();
		infrastructureProvidedRole.setEntityName("Provided_" + infrastructureInterface.getEntityName());
		infrastructureProvidedRole.setProvidedInterface__InfrastructureProvidedRole(infrastructureInterface);
		return infrastructureProvidedRole;
	}
	
	protected InfrastructureRequiredRole addInfrastructureRequiredRole(BasicComponent basicComponent, InfrastructureInterface infrastructureInterface) {
		InfrastructureRequiredRole infrastructureRequiredRole = this.createInfrastructureRequiredRole(infrastructureInterface);
		basicComponent.getRequiredRoles_InterfaceRequiringEntity().add(infrastructureRequiredRole);
		return infrastructureRequiredRole;
	}
	
	protected InfrastructureRequiredRole createInfrastructureRequiredRole(InfrastructureInterface infrastructureInterface) {
		InfrastructureRequiredRole infrastructureRequiredRole = RepositoryFactory.eINSTANCE.createInfrastructureRequiredRole();
		infrastructureRequiredRole.setEntityName("Required_" + infrastructureInterface.getEntityName());
		infrastructureRequiredRole.setRequiredInterface__InfrastructureRequiredRole(infrastructureInterface);
		return infrastructureRequiredRole;
	}
}
