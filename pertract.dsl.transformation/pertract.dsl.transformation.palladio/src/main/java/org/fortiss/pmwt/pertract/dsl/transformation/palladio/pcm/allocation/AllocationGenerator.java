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
package org.fortiss.pmwt.pertract.dsl.transformation.palladio.pcm.allocation;

import java.util.List;

import de.uka.ipd.sdq.pcm.allocation.Allocation;
import de.uka.ipd.sdq.pcm.allocation.AllocationContext;
import de.uka.ipd.sdq.pcm.allocation.AllocationFactory;
import de.uka.ipd.sdq.pcm.core.composition.AssemblyContext;
import de.uka.ipd.sdq.pcm.resourceenvironment.ResourceEnvironment;
import de.uka.ipd.sdq.pcm.system.System;

public class AllocationGenerator {

	public Allocation generateAllocation(System system, ResourceEnvironment resourceEnvironment) {
		Allocation allocation = AllocationFactory.eINSTANCE.createAllocation();
		allocation.setTargetResourceEnvironment_Allocation(resourceEnvironment);
		allocation.setSystem_Allocation(system);
		List<AssemblyContext> assemblyContexts = system.getAssemblyContexts__ComposedStructure();
		for (AssemblyContext assemblyContext : assemblyContexts) {
			AllocationContext allocationContext = AllocationFactory.eINSTANCE.createAllocationContext();
			allocationContext.setAssemblyContext_AllocationContext(assemblyContext);
			allocationContext.setResourceContainer_AllocationContext(resourceEnvironment.getResourceContainer_ResourceEnvironment().get(0));
			allocationContext.setEntityName(assemblyContext.getEntityName());
			allocation.getAllocationContexts_Allocation().add(allocationContext);
		}
		return allocation;
	}
	
}
