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
package org.fortiss.pmwt.pertract.dsl.transformation.palladio.pcm.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uka.ipd.sdq.pcm.core.composition.AssemblyConnector;
import de.uka.ipd.sdq.pcm.core.composition.AssemblyContext;
import de.uka.ipd.sdq.pcm.core.composition.AssemblyInfrastructureConnector;
import de.uka.ipd.sdq.pcm.core.composition.CompositionFactory;
import de.uka.ipd.sdq.pcm.core.composition.ProvidedDelegationConnector;
import de.uka.ipd.sdq.pcm.repository.InfrastructureInterface;
import de.uka.ipd.sdq.pcm.repository.InfrastructureProvidedRole;
import de.uka.ipd.sdq.pcm.repository.InfrastructureRequiredRole;
import de.uka.ipd.sdq.pcm.repository.Interface;
import de.uka.ipd.sdq.pcm.repository.OperationInterface;
import de.uka.ipd.sdq.pcm.repository.OperationProvidedRole;
import de.uka.ipd.sdq.pcm.repository.OperationRequiredRole;
import de.uka.ipd.sdq.pcm.repository.ProvidedRole;
import de.uka.ipd.sdq.pcm.repository.Repository;
import de.uka.ipd.sdq.pcm.repository.RepositoryComponent;
import de.uka.ipd.sdq.pcm.repository.RequiredRole;
import de.uka.ipd.sdq.pcm.system.System;

public class ConnectorGenerator {

	public void addConnectors(System system, Repository repository) {
		Map<Interface, ProvidedRole> interfaceProvidedRoles = new HashMap<>();
		Map<ProvidedRole, AssemblyContext> providedMapping = new HashMap<>();
		Map<RequiredRole, AssemblyContext> requiredMapping = new HashMap<>();

		this.mapProvidedandRequiredRoles(system, repository, interfaceProvidedRoles, providedMapping, requiredMapping);
		this.addAssemblyConnectors(system, interfaceProvidedRoles, providedMapping, requiredMapping);
		this.addProvidedDelegationConnectors(system, providedMapping);
	}

	private void mapProvidedandRequiredRoles(System system, Repository repository,
			Map<Interface, ProvidedRole> interfaceProvidedRoles, 
			Map<ProvidedRole, AssemblyContext> providedMapping,
			Map<RequiredRole, AssemblyContext> requiredMapping) {

		List<AssemblyContext> assemblyContexts = system.getAssemblyContexts__ComposedStructure();
		for (AssemblyContext assemblyContext : assemblyContexts) {
			RepositoryComponent repositoryComponent = assemblyContext.getEncapsulatedComponent__AssemblyContext();
			this.mapProvidedRoles(providedMapping, interfaceProvidedRoles, assemblyContext, repositoryComponent);
			this.mapSystemProvidedRole(providedMapping, assemblyContext, repositoryComponent, system);
			this.mapRequiredRoles(requiredMapping, assemblyContext, repositoryComponent);
		}
	}

	private void mapProvidedRoles(Map<ProvidedRole, AssemblyContext> providedMapping,
			Map<Interface, ProvidedRole> interfaceProvidedRoles, AssemblyContext assemblyContext,
			RepositoryComponent repositoryComponent) {
		List<ProvidedRole> providedRoles = repositoryComponent.getProvidedRoles_InterfaceProvidingEntity();
		for (ProvidedRole providedRole : providedRoles) {
			if (providedRole instanceof OperationProvidedRole) {
				OperationProvidedRole operationProvidedRole = (OperationProvidedRole) providedRole;
				OperationInterface operationInterface = operationProvidedRole
						.getProvidedInterface__OperationProvidedRole();
				interfaceProvidedRoles.put(operationInterface, operationProvidedRole);
				providedMapping.put(operationProvidedRole, assemblyContext);
			} else if (providedRole instanceof InfrastructureProvidedRole) {
				InfrastructureProvidedRole infrastructureProvidedRole = (InfrastructureProvidedRole) providedRole;
				InfrastructureInterface infrastructureInterface = infrastructureProvidedRole.getProvidedInterface__InfrastructureProvidedRole();
				interfaceProvidedRoles.put(infrastructureInterface, infrastructureProvidedRole);
				providedMapping.put(infrastructureProvidedRole, assemblyContext);
			}
		}
	}

	private void mapSystemProvidedRole(Map<ProvidedRole, AssemblyContext> providedMapping,
			AssemblyContext assemblyContext, RepositoryComponent repositoryComponent, System system) {
		if (assemblyContext.getEntityName().contains("Application")) {
			ProvidedRole providedRole = system.getProvidedRoles_InterfaceProvidingEntity().get(0);
			if (providedRole instanceof OperationProvidedRole) {
				OperationProvidedRole systemOperationProvidedRole = (OperationProvidedRole) providedRole;
				providedMapping.put(systemOperationProvidedRole, assemblyContext);
			}
		}
	}

	private void mapRequiredRoles(Map<RequiredRole, AssemblyContext> requiredMapping, AssemblyContext assemblyContext,
			RepositoryComponent repositoryComponent) {
		List<RequiredRole> requiredRoles = repositoryComponent.getRequiredRoles_InterfaceRequiringEntity();
		for (RequiredRole requiredRole : requiredRoles) {
			requiredMapping.put(requiredRole, assemblyContext);
		}
	}

	private void addAssemblyConnectors(System system,
			Map<Interface, ProvidedRole> interfaceProvidedRoles,
			Map<ProvidedRole, AssemblyContext> providedMapping,
			Map<RequiredRole, AssemblyContext> requiredMapping) {
		for (Map.Entry<RequiredRole, AssemblyContext> entry : requiredMapping.entrySet()) {
			RequiredRole requiredRole = entry.getKey();
			AssemblyContext requiringAssemblyContext = entry.getValue();
			
			if (requiredRole instanceof OperationRequiredRole) {
				OperationRequiredRole operationRequiredRole = (OperationRequiredRole) requiredRole;
				OperationInterface operationInterface = operationRequiredRole.getRequiredInterface__OperationRequiredRole();
				OperationProvidedRole operationProvidedRole = (OperationProvidedRole) interfaceProvidedRoles.get(operationInterface);
				AssemblyContext providingAssemblyContext = providedMapping.get(operationProvidedRole);
				AssemblyConnector assemblyConnector = this.createAssemblyConnector(operationProvidedRole,
						operationRequiredRole, providingAssemblyContext, requiringAssemblyContext);
				system.getConnectors__ComposedStructure().add(assemblyConnector);
			} else if (requiredRole instanceof InfrastructureRequiredRole) {
				InfrastructureRequiredRole infrastructureRequiredRole = (InfrastructureRequiredRole) requiredRole;
				InfrastructureInterface infrastructureInterface = infrastructureRequiredRole.getRequiredInterface__InfrastructureRequiredRole();
				InfrastructureProvidedRole infrastructureProvidedRole = (InfrastructureProvidedRole) interfaceProvidedRoles.get(infrastructureInterface);
				AssemblyContext providingAssemblyContext = providedMapping.get(infrastructureProvidedRole);
				AssemblyInfrastructureConnector assemblyConnector = this.createAssemblyInfrastructureConnector(infrastructureProvidedRole,
						infrastructureRequiredRole, providingAssemblyContext, requiringAssemblyContext);
				system.getConnectors__ComposedStructure().add(assemblyConnector);
			}
			
		}
	}

	private AssemblyConnector createAssemblyConnector(OperationProvidedRole operationProvidedRole,
			OperationRequiredRole operationRequiredRole, AssemblyContext providingAssemblyContext,
			AssemblyContext requiringAssemblyContext) {
		AssemblyConnector assemblyConnector = CompositionFactory.eINSTANCE.createAssemblyConnector();
		assemblyConnector.setProvidedRole_AssemblyConnector(operationProvidedRole);
		assemblyConnector.setRequiredRole_AssemblyConnector(operationRequiredRole);
		assemblyConnector.setProvidingAssemblyContext_AssemblyConnector(providingAssemblyContext);
		assemblyConnector.setRequiringAssemblyContext_AssemblyConnector(requiringAssemblyContext);
		assemblyConnector.setEntityName("Connector " + providingAssemblyContext.getEntityName() + " -> "
				+ requiringAssemblyContext.getEntityName());
		return assemblyConnector;
	}

	private AssemblyInfrastructureConnector createAssemblyInfrastructureConnector(
			InfrastructureProvidedRole providedRole, InfrastructureRequiredRole requiredRole,
			AssemblyContext providingAssemblyContext, AssemblyContext requiringAssemblyContext) {
		AssemblyInfrastructureConnector assemblyConnector = CompositionFactory.eINSTANCE
				.createAssemblyInfrastructureConnector();
		assemblyConnector.setProvidedRole__AssemblyInfrastructureConnector(providedRole);
		assemblyConnector.setRequiredRole__AssemblyInfrastructureConnector(requiredRole);
		assemblyConnector.setProvidingAssemblyContext__AssemblyInfrastructureConnector(providingAssemblyContext);
		assemblyConnector.setRequiringAssemblyContext__AssemblyInfrastructureConnector(requiringAssemblyContext);
		assemblyConnector.setEntityName("Connector " + providingAssemblyContext.getEntityName() + " -> "
				+ requiringAssemblyContext.getEntityName());
		return assemblyConnector;
	}

	private void addProvidedDelegationConnectors(System system,
			Map<ProvidedRole, AssemblyContext> providedMapping) {
		List<ProvidedRole> providedRoles = system.getProvidedRoles_InterfaceProvidingEntity();
		for (ProvidedRole providedRole : providedRoles) {
			OperationProvidedRole outerProvidedRole = (OperationProvidedRole) providedRole;
			if (providedMapping.containsKey(outerProvidedRole)) {
				AssemblyContext assemblyContext = providedMapping.get(outerProvidedRole);

				RepositoryComponent repositoryComponent = assemblyContext.getEncapsulatedComponent__AssemblyContext();
				OperationProvidedRole innerProvidedRole = (OperationProvidedRole) repositoryComponent
						.getProvidedRoles_InterfaceProvidingEntity().get(0);

				ProvidedDelegationConnector providedDelegationConnector = this
						.createProvidedDelegationConnector(outerProvidedRole, innerProvidedRole, assemblyContext);
				system.getConnectors__ComposedStructure().add(providedDelegationConnector);
			}
		}
	}

	private ProvidedDelegationConnector createProvidedDelegationConnector(OperationProvidedRole outerProvidedRole,
			OperationProvidedRole innerProvidedRole, AssemblyContext assemblyContext) {
		ProvidedDelegationConnector providedDelegationConnector = CompositionFactory.eINSTANCE
				.createProvidedDelegationConnector();
		providedDelegationConnector.setOuterProvidedRole_ProvidedDelegationConnector(outerProvidedRole);
		providedDelegationConnector.setInnerProvidedRole_ProvidedDelegationConnector(innerProvidedRole);
		providedDelegationConnector.setAssemblyContext_ProvidedDelegationConnector(assemblyContext);
		return providedDelegationConnector;
	}

}
