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
import de.uka.ipd.sdq.pcm.repository.OperationInterface;
import de.uka.ipd.sdq.pcm.repository.RepositoryFactory;

public class RepositoryEntityArchetypeProvider {

	protected BasicComponent createComponent(String name) {
		BasicComponent basicComponent = RepositoryFactory.eINSTANCE.createBasicComponent();
		basicComponent.setEntityName(name);
		return basicComponent;
	}
	
	protected OperationInterface createInterface(String name) {
		OperationInterface operationInterface = RepositoryFactory.eINSTANCE.createOperationInterface();
		operationInterface.setEntityName(name);
		return operationInterface;
	}
	
	protected InfrastructureInterface createInfrastructureInterface(String name) {
		InfrastructureInterface infrastructureInterface = RepositoryFactory.eINSTANCE.createInfrastructureInterface();
		infrastructureInterface.setEntityName(name);
		return infrastructureInterface;
	}
	
}
