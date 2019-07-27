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
package org.fortiss.pmwt.pertract.dsl.simulation.client.results;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SimulationResultsDTO {

	private List<SimulationMeasureDTO> measures;
	
	public SimulationResultsDTO() {
		this.setMeasures(new ArrayList<SimulationMeasureDTO>());
	}

	public List<SimulationMeasureDTO> getMeasures() {
		return measures;
	}

	public void setMeasures(List<SimulationMeasureDTO> measures) {
		this.measures = measures;
	}

	public void addMeasure(SimulationMeasureDTO measure) {
		this.measures.add(measure);
	}
	
	public void addMeasures(List<SimulationMeasureDTO> measures) {
		this.measures.addAll(measures);
	}

}
