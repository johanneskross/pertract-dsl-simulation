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

import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.fortiss.pmwt.pertract.dsl.transformation.palladio.pcm.PCMGenerator;

import de.uka.ipd.sdq.pcm.repository.DataType;
import de.uka.ipd.sdq.pcm.repository.PrimitiveDataType;
import de.uka.ipd.sdq.pcm.repository.PrimitiveTypeEnum;
import de.uka.ipd.sdq.pcm.repository.Repository;

public class PrimitiveTypeSource {
	
	private PrimitiveDataType intDataType;
	private PrimitiveDataType boolDataType;
	
	public PrimitiveTypeSource() {
		Repository repository = this.loadRepository();
		this.initializePrimitiveTypes(repository);
	}

	private Repository loadRepository() {
		de.uka.ipd.sdq.pcm.repository.RepositoryPackage.eINSTANCE.eClass();
		org.eclipse.emf.ecore.resource.Resource.Factory.Registry reg = org.eclipse.emf.ecore.resource.Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("repository", new XMIResourceFactoryImpl());
		ResourceSet resourceSet = new ResourceSetImpl();
		org.eclipse.emf.ecore.resource.Resource resource = resourceSet.getResource(URI.createURI(PCMGenerator.class.getResource("/PrimitiveTypes.repository").toString()), true);
		resource.setURI(URI.createURI("pathmap://PCM_MODELS/PrimitiveTypes.repository"));
		Repository repository = (Repository) resource.getContents().get(0);
		return repository;
	}
	
	private void initializePrimitiveTypes(Repository repository){
		List<DataType> types = repository.getDataTypes__Repository();
		for (DataType type : types) {
			if (type instanceof PrimitiveDataType) {
				PrimitiveDataType primitiveDataType = (PrimitiveDataType) type;
				if (primitiveDataType.getType() == PrimitiveTypeEnum.INT) {
					this.setIntDataType(primitiveDataType);
				} else if (primitiveDataType.getType() == PrimitiveTypeEnum.BOOL) {
					this.setBoolDataType(primitiveDataType);
				}
			}
		}
	}

	public PrimitiveDataType getIntDataType() {
		return intDataType;
	}

	public void setIntDataType(PrimitiveDataType intDataType) {
		this.intDataType = intDataType;
	}

	public PrimitiveDataType getBoolDataType() {
		return boolDataType;
	}

	public void setBoolDataType(PrimitiveDataType boolDataType) {
		this.boolDataType = boolDataType;
	}
	
}
