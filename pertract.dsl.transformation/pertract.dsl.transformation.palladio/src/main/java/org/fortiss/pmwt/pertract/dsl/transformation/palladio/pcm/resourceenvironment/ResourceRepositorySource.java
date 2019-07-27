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
package org.fortiss.pmwt.pertract.dsl.transformation.palladio.pcm.resourceenvironment;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import de.uka.ipd.sdq.pcm.resourcetype.CommunicationLinkResourceType;
import de.uka.ipd.sdq.pcm.resourcetype.ResourceRepository;
import de.uka.ipd.sdq.pcm.resourcetype.ResourceRole;
import de.uka.ipd.sdq.pcm.resourcetype.ResourceType;
import de.uka.ipd.sdq.pcm.resourcetype.SchedulingPolicy;

public class ResourceRepositorySource {

	private static final String PROCESSOR_SHARING = "Processor Sharing";
	private static final String DELAY = "Delay";
	private static final String ROUND_ROBIN = "Round Robin";
	private static final String MASTER = "Master";
	private static final String WORKER = "Worker";
	private static final String DELAY_PROCESSING_TYPE = "DELAY";
	private static final String CPU_PROCESSING_TYPE = "CPU";
	private static final String HDD_PROCESSING_TYPE = "HDD";
	private static final String LAN_LINKING_TYPE = "LAN";
	
	private CommunicationLinkResourceType lanResourceType;
	private SchedulingPolicy processorSharingPolicy;
	private SchedulingPolicy delaySchedulingPolicy;
	private SchedulingPolicy roundRobinSchedulingPolicy;
	private ResourceType cpuResourceType;
	private ResourceType hddResourceType;
	private ResourceType delayResourceType;
	private ResourceRole masterResourceRole;
	private ResourceRole workerResourceRole;
	
	public ResourceRepositorySource() {
		ResourceRepository resourceRepository = this.loadResourceRepository();
		this.initializeSchedulingPolicies(resourceRepository);
		this.initializeResourceTypes(resourceRepository);
		this.initializeResourceRoles(resourceRepository);
	}
	
	private ResourceRepository loadResourceRepository() {
		de.uka.ipd.sdq.pcm.resourcetype.ResourcetypePackage.eINSTANCE.eClass();
		org.eclipse.emf.ecore.resource.Resource.Factory.Registry reg = org.eclipse.emf.ecore.resource.Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("resourcetype", new XMIResourceFactoryImpl());
		ResourceSet resourceSet = new ResourceSetImpl();
		org.eclipse.emf.ecore.resource.Resource resource = resourceSet.getResource(URI.createURI(ResourceEnvironmentGenerator.class.getResource("/Palladio.resourcetype").toString()), true);
		resource.setURI(URI.createURI("pathmap://PCM_MODELS/Palladio.resourcetype"));
		ResourceRepository resourceRepository = (ResourceRepository) resource.getContents().get(0);
		return resourceRepository;
	}
	
	private void initializeSchedulingPolicies(ResourceRepository resourceRepository) {
		EList<SchedulingPolicy> schedulingPolicies = resourceRepository.getSchedulingPolicies__ResourceRepository();
		for (SchedulingPolicy schedulingPolicy : schedulingPolicies) {
			switch (schedulingPolicy.getEntityName()) {
			case PROCESSOR_SHARING:
				this.processorSharingPolicy = schedulingPolicy;
				break;
			case DELAY:
				this.delaySchedulingPolicy = schedulingPolicy;
				break;
			case ROUND_ROBIN:
				this.roundRobinSchedulingPolicy = schedulingPolicy;
				break;
			}
		}
	}
	
	private void initializeResourceTypes(ResourceRepository resourceRepository) {
		List<ResourceType> resourceTypes = resourceRepository.getAvailableResourceTypes_ResourceRepository();
		for (ResourceType resourceType : resourceTypes) {
			switch (resourceType.getEntityName()) {
			case HDD_PROCESSING_TYPE:
				hddResourceType = resourceType;
				break;
			case DELAY_PROCESSING_TYPE:
				delayResourceType = resourceType;
				break;
			case CPU_PROCESSING_TYPE:
				cpuResourceType = resourceType;
				break;
			case LAN_LINKING_TYPE:
				lanResourceType = (CommunicationLinkResourceType) resourceType;
				break;
			}
		}
	}
	
	private void initializeResourceRoles(ResourceRepository resourceRepository) {
		List<ResourceRole> resourceRoles = resourceRepository.getResourceRoles_ResourceRepository();
		for (ResourceRole resourceRole : resourceRoles) {
			switch (resourceRole.getEntityName()) {
			case MASTER:
				masterResourceRole = resourceRole;
				break;
			case WORKER:
				workerResourceRole = resourceRole;
				break;
			}
		}
	}

	public CommunicationLinkResourceType getLanResourceType() {
		return lanResourceType;
	}

	public SchedulingPolicy getProcessorSharingPolicy() {
		return processorSharingPolicy;
	}

	public SchedulingPolicy getDelaySchedulingPolicy() {
		return delaySchedulingPolicy;
	}

	public SchedulingPolicy getRoundRobinSchedulingPolicy() {
		return roundRobinSchedulingPolicy;
	}

	public ResourceType getCpuResourceType() {
		return cpuResourceType;
	}

	public ResourceType getHddResourceType() {
		return hddResourceType;
	}

	public ResourceType getDelayResourceType() {
		return delayResourceType;
	}

	public ResourceRole getMasterResourceRole() {
		return masterResourceRole;
	}

	public ResourceRole getWorkerResourceRole() {
		return workerResourceRole;
	}
	
}
