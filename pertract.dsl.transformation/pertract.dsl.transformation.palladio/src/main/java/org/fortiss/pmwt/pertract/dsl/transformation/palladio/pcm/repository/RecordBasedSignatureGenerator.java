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

import de.uka.ipd.sdq.pcm.repository.OperationSignature;
import de.uka.ipd.sdq.pcm.repository.Parameter;
import de.uka.ipd.sdq.pcm.repository.RepositoryFactory;

public class RecordBasedSignatureGenerator extends SignatureGenerator {

	@Override
	public OperationSignature createDelegationOperationSignature() {
		OperationSignature operationSignature = RepositoryFactory.eINSTANCE.createOperationSignature();
		operationSignature.setEntityName("delegate");
		Parameter records = paramProvider.createIntParameter("records");
		Parameter partitions = paramProvider.createIntParameter("partitions");
		Parameter executors = paramProvider.createIntParameter("executors");
		operationSignature.getParameters__OperationSignature().add(records);
		operationSignature.getParameters__OperationSignature().add(partitions);
		operationSignature.getParameters__OperationSignature().add(executors);
		return operationSignature;
	}
	
	@Override
	public OperationSignature createExecutionOperationSignature() {
		OperationSignature operationSignature = RepositoryFactory.eINSTANCE.createOperationSignature();
		operationSignature.setEntityName("execute");
		Parameter records = paramProvider.createIntParameter("records");
		operationSignature.getParameters__OperationSignature().add(records);
		return operationSignature;
	}
	
	@Override
	public OperationSignature createTaskOperationSignature() {
		OperationSignature operationSignature = RepositoryFactory.eINSTANCE.createOperationSignature();
		operationSignature.setEntityName("run");
		Parameter records = paramProvider.createIntParameter("records");
		operationSignature.getParameters__OperationSignature().add(records);
		return operationSignature;
	}

}
