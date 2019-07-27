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

public class FileBasedSignatureGenerator extends SignatureGenerator {

	@Override
	public OperationSignature createDelegationOperationSignature() {
		OperationSignature operationSignature = RepositoryFactory.eINSTANCE.createOperationSignature();
		operationSignature.setEntityName("delegate");
		Parameter files = paramProvider.createIntParameter("files");
		Parameter sizePerFile = paramProvider.createIntParameter("sizePerFile");
		Parameter splitsPerFile = paramProvider.createIntParameter("splitsPerFile");
		Parameter defaultSplitSize = paramProvider.createIntParameter("defaultSplitSize");
		Parameter executors = paramProvider.createIntParameter("executors");
		operationSignature.getParameters__OperationSignature().add(files);
		operationSignature.getParameters__OperationSignature().add(sizePerFile);
		operationSignature.getParameters__OperationSignature().add(splitsPerFile);
		operationSignature.getParameters__OperationSignature().add(defaultSplitSize);
		operationSignature.getParameters__OperationSignature().add(executors);
		return operationSignature;
	}
	
	@Override
	public OperationSignature createExecutionOperationSignature() {
		OperationSignature operationSignature = RepositoryFactory.eINSTANCE.createOperationSignature();
		operationSignature.setEntityName("execute");
		Parameter isDefaultSplit = paramProvider.createBoolParameter("isDefaultSplit");
		Parameter defaultSplitSize = paramProvider.createIntParameter("defaultSplitSize");
		Parameter remainingSplitSize = paramProvider.createIntParameter("remainingSplitSize");
		Parameter executors = paramProvider.createIntParameter("executors");
		operationSignature.getParameters__OperationSignature().add(isDefaultSplit);
		operationSignature.getParameters__OperationSignature().add(defaultSplitSize);
		operationSignature.getParameters__OperationSignature().add(remainingSplitSize);
		operationSignature.getParameters__OperationSignature().add(executors);
		return operationSignature;
	}
	
	@Override
	public OperationSignature createTaskOperationSignature() {
		OperationSignature operationSignature = RepositoryFactory.eINSTANCE.createOperationSignature();
		operationSignature.setEntityName("run");
		Parameter dataSize = paramProvider.createIntParameter("dataSize");
		Parameter executors = paramProvider.createIntParameter("executors");
		operationSignature.getParameters__OperationSignature().add(dataSize);
		operationSignature.getParameters__OperationSignature().add(executors);
		return operationSignature;
	}

}
