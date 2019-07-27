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
package org.fortiss.pmwt.pertract.dsl.transformation.palladio;

import org.fortiss.pmwt.pertract.dsl.model.application.ApplicationExecutionArchitecture;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.DataWorkloadArchitecture;
import org.fortiss.pmwt.pertract.dsl.model.resources.ResourceArchitecture;

public interface PerformanceModelGenerator {

	void generatePerformanceModels(ApplicationExecutionArchitecture applicationModel, DataWorkloadArchitecture dataWorkload, ResourceArchitecture resources, String outputFolder);
	
}
