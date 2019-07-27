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

import de.uka.ipd.sdq.pcm.repository.InfrastructureSignature;
import de.uka.ipd.sdq.pcm.repository.OperationSignature;
import de.uka.ipd.sdq.pcm.repository.Parameter;
import de.uka.ipd.sdq.pcm.repository.RepositoryFactory;

public abstract class SignatureGenerator {
	
	protected RepositoryParameterArchetypeProvider paramProvider;

	public SignatureGenerator() {
		this.paramProvider = new RepositoryParameterArchetypeProvider();
	}

	public abstract OperationSignature createDelegationOperationSignature();
	
	public abstract OperationSignature createExecutionOperationSignature();
	
	public abstract OperationSignature createTaskOperationSignature();
	
	protected InfrastructureSignature createAllocOperationSignature() {
		InfrastructureSignature infrastructureSignature = RepositoryFactory.eINSTANCE.createInfrastructureSignature();
		infrastructureSignature.setEntityName("allocCore");
		Parameter cores = paramProvider.createIntParameter("amount");
		infrastructureSignature.getParameters__InfrastructureSignature().add(cores);
		return infrastructureSignature;
	}
	
	protected InfrastructureSignature createFreeOperationSignature() {
		InfrastructureSignature infrastructureSignature = RepositoryFactory.eINSTANCE.createInfrastructureSignature();
		infrastructureSignature.setEntityName("freeCore");
		Parameter cores = paramProvider.createIntParameter("amount");
		infrastructureSignature.getParameters__InfrastructureSignature().add(cores);
		return infrastructureSignature;
	}
	
}
