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

import java.util.List;

import de.uka.ipd.sdq.pcm.core.composition.AssemblyContext;
import de.uka.ipd.sdq.pcm.core.composition.CompositionFactory;
import de.uka.ipd.sdq.pcm.repository.Interface;
import de.uka.ipd.sdq.pcm.repository.OperationInterface;
import de.uka.ipd.sdq.pcm.repository.OperationProvidedRole;
import de.uka.ipd.sdq.pcm.repository.Repository;
import de.uka.ipd.sdq.pcm.repository.RepositoryComponent;
import de.uka.ipd.sdq.pcm.repository.RepositoryFactory;
import de.uka.ipd.sdq.pcm.system.System;
import de.uka.ipd.sdq.pcm.system.SystemFactory;

public class SystemGenerator {
	
	private ConnectorGenerator connectorGenerator;
	
	public SystemGenerator() {
		this.connectorGenerator = new ConnectorGenerator();
	}

	public System generateSystemModel(Repository repository) {
		System system = SystemFactory.eINSTANCE.createSystem();
		this.addAssemplyContexts(system, repository);
		this.addSystemOperationProvidedRole(system, repository);
		this.connectorGenerator.addConnectors(system, repository);
		return system;
	}

	private void addAssemplyContexts(System system, Repository repository) {
		List<RepositoryComponent> repositoryComponents = repository.getComponents__Repository();
		for (RepositoryComponent repositoryComponent : repositoryComponents) {
			AssemblyContext assemblyContext = this.createAssemblyContext(repositoryComponent);
			system.getAssemblyContexts__ComposedStructure().add(assemblyContext);
		}
	}
	
	private AssemblyContext createAssemblyContext(RepositoryComponent repositoryComponent) {
		AssemblyContext assemblyContext = CompositionFactory.eINSTANCE.createAssemblyContext();
		assemblyContext.setEntityName("Assembly " + repositoryComponent.getEntityName());
		assemblyContext.setEncapsulatedComponent__AssemblyContext(repositoryComponent);
		return assemblyContext;
	}
	
	private void addSystemOperationProvidedRole(System system, Repository repository) {
		List<Interface> interfaces = repository.getInterfaces__Repository();
		for (Interface iface : interfaces) {
			if (iface.getEntityName().equals("Application")) {
				OperationProvidedRole operationProvidedRole = this.createOperationProvidedRole(iface);
				system.getProvidedRoles_InterfaceProvidingEntity().add(operationProvidedRole);
			}
		}
	}
	
	private OperationProvidedRole createOperationProvidedRole(Interface iface) {
		OperationProvidedRole operationProvidedRole = RepositoryFactory.eINSTANCE.createOperationProvidedRole();
		operationProvidedRole.setEntityName("Provided " + iface.getEntityName());
		operationProvidedRole.setProvidedInterface__OperationProvidedRole((OperationInterface) iface);
		return operationProvidedRole;
	}
	
}
