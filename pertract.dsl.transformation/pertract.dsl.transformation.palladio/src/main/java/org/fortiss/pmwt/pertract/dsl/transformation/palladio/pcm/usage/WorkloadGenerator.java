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
package org.fortiss.pmwt.pertract.dsl.transformation.palladio.pcm.usage;

import de.uka.ipd.sdq.pcm.core.CoreFactory;
import de.uka.ipd.sdq.pcm.core.PCMRandomVariable;
import de.uka.ipd.sdq.pcm.usagemodel.ClosedWorkload;
import de.uka.ipd.sdq.pcm.usagemodel.OpenWorkload;
import de.uka.ipd.sdq.pcm.usagemodel.UsagemodelFactory;

public class WorkloadGenerator {

	public ClosedWorkload createClosedWorkload(int population, double thinkTime) {
		PCMRandomVariable thinkTimeVariable = CoreFactory.eINSTANCE.createPCMRandomVariable();
		thinkTimeVariable.setSpecification(String.valueOf(thinkTime));
		ClosedWorkload closedWorkload = UsagemodelFactory.eINSTANCE.createClosedWorkload();
		closedWorkload.setThinkTime_ClosedWorkload(thinkTimeVariable);
		closedWorkload.setPopulation(population);
		return closedWorkload;
	}
	
	public OpenWorkload createOpenWorklad(double interArrivalTime) {
		PCMRandomVariable interArrivalTimeVariable = CoreFactory.eINSTANCE.createPCMRandomVariable();
		interArrivalTimeVariable.setSpecification(String.valueOf(interArrivalTime));
		OpenWorkload openWorkload = UsagemodelFactory.eINSTANCE.createOpenWorkload();
		openWorkload.setInterArrivalTime_OpenWorkload(interArrivalTimeVariable);
		return openWorkload;
	}
	
}
