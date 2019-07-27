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

import de.uka.ipd.sdq.pcm.repository.Parameter;
import de.uka.ipd.sdq.pcm.repository.PrimitiveDataType;
import de.uka.ipd.sdq.pcm.repository.RepositoryFactory;

public class RepositoryParameterArchetypeProvider {

	private PrimitiveTypeSource dataTypes;

	public RepositoryParameterArchetypeProvider() {
		this.dataTypes = new PrimitiveTypeSource();
	}
	
	protected Parameter createIntParameter(String name) {
		Parameter parameter = RepositoryFactory.eINSTANCE.createParameter();
		parameter.setParameterName(name);
		PrimitiveDataType dataType = dataTypes.getIntDataType();
		parameter.setDataType__Parameter(dataType);
		return parameter;
	}
	
	protected Parameter createBoolParameter(String name) {
		Parameter parameter = RepositoryFactory.eINSTANCE.createParameter();
		parameter.setParameterName(name);
		PrimitiveDataType dataType = dataTypes.getBoolDataType();
		parameter.setDataType__Parameter(dataType);
		return parameter;
	}
}
