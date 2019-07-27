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

import java.util.ArrayList;
import java.util.List;

import org.fortiss.pmwt.pertract.dsl.model.resources.NetworkChannel;
import org.fortiss.pmwt.pertract.dsl.model.resources.ProcessingResourceUnit;
import org.fortiss.pmwt.pertract.dsl.model.resources.ResourceArchitecture;
import org.fortiss.pmwt.pertract.dsl.model.resources.ResourceNode;
import org.fortiss.pmwt.pertract.dsl.model.resources.ResourceRole;
import org.fortiss.pmwt.pertract.dsl.model.resources.SchedulingPolicy;

import de.uka.ipd.sdq.pcm.core.CoreFactory;
import de.uka.ipd.sdq.pcm.core.PCMRandomVariable;
import de.uka.ipd.sdq.pcm.resourceenvironment.ClusterResourceSpecification;
import de.uka.ipd.sdq.pcm.resourceenvironment.CommunicationLinkResourceSpecification;
import de.uka.ipd.sdq.pcm.resourceenvironment.LinkingResource;
import de.uka.ipd.sdq.pcm.resourceenvironment.ProcessingResourceSpecification;
import de.uka.ipd.sdq.pcm.resourceenvironment.ResourceContainer;
import de.uka.ipd.sdq.pcm.resourceenvironment.ResourceEnvironment;
import de.uka.ipd.sdq.pcm.resourceenvironment.ResourceenvironmentFactory;
import de.uka.ipd.sdq.pcm.resourcetype.ProcessingResourceType;

public class ResourceEnvironmentGenerator {
	
	private ResourceRepositorySource resources;
		
	public ResourceEnvironmentGenerator() {
		this.resources = new ResourceRepositorySource();
	}
	
	public ResourceEnvironment generateResourceEnvironment (ResourceArchitecture resourceArchitecture) {
		ResourceEnvironment re = ResourceenvironmentFactory.eINSTANCE.createResourceEnvironment();
		re.setEntityName(resourceArchitecture.getName());
		LinkingResource linkingResource = this.createLinkingResource(resourceArchitecture.getNetworkChannel());
		linkingResource.setResourceEnvironment_LinkingResource(re);
		
		ResourceContainer masterNodeContainer = ResourceenvironmentFactory.eINSTANCE.createResourceContainer();
		List<ResourceContainer> workerNodeContainers = new ArrayList<>();
		for (ResourceNode resourceNode : resourceArchitecture.getResourceNodes()) {
			ResourceContainer resourceContainer = this.createResourceContainer(resourceNode);
			if(resourceNode.getClusterSpecification().getResourceRole().equals(ResourceRole.MASTER.getName())) {
				masterNodeContainer = resourceContainer;
			} else if(resourceNode.getClusterSpecification().getResourceRole().equals(ResourceRole.WORKER.getName())) {
				workerNodeContainers.add(resourceContainer);
			}
		}
		
		for (ResourceContainer workerNodeContainer : workerNodeContainers) {
			workerNodeContainer.setParentResourceContainer__ResourceContainer(masterNodeContainer);
			linkingResource.getConnectedResourceContainers_LinkingResource().add(workerNodeContainer);
			masterNodeContainer.getNestedResourceContainers__ResourceContainer().add(workerNodeContainer);
		}
		
		linkingResource.getConnectedResourceContainers_LinkingResource().add(masterNodeContainer);
		re.getResourceContainer_ResourceEnvironment().add(masterNodeContainer);
		re.getLinkingResources__ResourceEnvironment().add(linkingResource);
		return re;
	}
	
	private LinkingResource createLinkingResource(NetworkChannel networkChannel){
		LinkingResource linkingResource = ResourceenvironmentFactory.eINSTANCE.createLinkingResource();
		linkingResource.setEntityName("LAN");
		CommunicationLinkResourceSpecification clrs = this.createCommunicationLinkResourceSpecification(networkChannel);
		linkingResource.setCommunicationLinkResourceSpecifications_LinkingResource(clrs);
		return linkingResource;
	}
	
	private CommunicationLinkResourceSpecification createCommunicationLinkResourceSpecification(NetworkChannel networkChannel) {
		CommunicationLinkResourceSpecification clrs = ResourceenvironmentFactory.eINSTANCE.createCommunicationLinkResourceSpecification();
		clrs.setLatency_CommunicationLinkResourceSpecification(this.createVariable(String.valueOf(networkChannel.getLatency())));
		clrs.setThroughput_CommunicationLinkResourceSpecification(this.createVariable(String.valueOf(networkChannel.getBandwidth())));
		clrs.setFailureProbability(0);
		clrs.setCommunicationLinkResourceType_CommunicationLinkResourceSpecification(resources.getLanResourceType());
		return clrs;
	}
	
	private PCMRandomVariable createVariable(String specification) {
		PCMRandomVariable pcmRandomVariable = CoreFactory.eINSTANCE.createPCMRandomVariable();
		pcmRandomVariable.setSpecification(specification);
		return pcmRandomVariable;
	}
	
	private ResourceContainer createResourceContainer(ResourceNode node) {
		ResourceContainer resourceContainer = ResourceenvironmentFactory.eINSTANCE.createResourceContainer();
		resourceContainer.setEntityName(node.getName());
		ProcessingResourceSpecification processingSpec = createCPUProcessingResourceSpecification(node.getProcessingResourceUnit());
		resourceContainer.getActiveResourceSpecifications_ResourceContainer().add(processingSpec);
		ProcessingResourceSpecification delaySpec = createDelayProcessingResourceSpecification();
		resourceContainer.getActiveResourceSpecifications_ResourceContainer().add(delaySpec);
		ClusterResourceSpecification clusterSpec = createClusterResourceSpecification(node);
		resourceContainer.setClusterResourceSpecification_ResourceContainer(clusterSpec);
		return resourceContainer;
	}
	
	private ProcessingResourceSpecification createCPUProcessingResourceSpecification(ProcessingResourceUnit processingResourceUnit) {
		ProcessingResourceSpecification processingSpec = ResourceenvironmentFactory.eINSTANCE.createProcessingResourceSpecification();
		PCMRandomVariable pcmRandomVariable = CoreFactory.eINSTANCE.createPCMRandomVariable();
		pcmRandomVariable.setSpecification(String.valueOf(processingResourceUnit.getProcessingRate()));
		processingSpec.setProcessingRate_ProcessingResourceSpecification(pcmRandomVariable);
		processingSpec.setNumberOfReplicas(processingResourceUnit.getReplications());
		processingSpec.setActiveResourceType_ActiveResourceSpecification((ProcessingResourceType) this.resources.getCpuResourceType());
		processingSpec.setSchedulingPolicy(this.resources.getProcessorSharingPolicy());
		return processingSpec;
	}
	
	private ProcessingResourceSpecification createDelayProcessingResourceSpecification() {
		ProcessingResourceSpecification processingSpec = ResourceenvironmentFactory.eINSTANCE.createProcessingResourceSpecification();
		PCMRandomVariable pcmRandomVariable = CoreFactory.eINSTANCE.createPCMRandomVariable();
		pcmRandomVariable.setSpecification("1000");
		processingSpec.setProcessingRate_ProcessingResourceSpecification(pcmRandomVariable);
		processingSpec.setNumberOfReplicas(1);
		processingSpec.setActiveResourceType_ActiveResourceSpecification((ProcessingResourceType) this.resources.getDelayResourceType());
		processingSpec.setSchedulingPolicy(this.resources.getDelaySchedulingPolicy());
		return processingSpec;
	}
	
	private ClusterResourceSpecification createClusterResourceSpecification(ResourceNode node) {
		ClusterResourceSpecification clusterSpec = ResourceenvironmentFactory.eINSTANCE.createClusterResourceSpecification();
		this.setResourceRole(clusterSpec, node);
		this.setActionSchedulingPolicy(clusterSpec, node);
		return clusterSpec;
	}
	
	private void setResourceRole(ClusterResourceSpecification clusterSpec, ResourceNode node) {
		String resourceRole = node.getClusterSpecification().getResourceRole();
		if (resourceRole.equals(ResourceRole.MASTER.getName())) {
			clusterSpec.setResourceRole(this.resources.getMasterResourceRole());
		}else if (resourceRole.equals(ResourceRole.WORKER.getName())) {
			clusterSpec.setResourceRole(this.resources.getWorkerResourceRole());
		}
	}
	
	private void setActionSchedulingPolicy(ClusterResourceSpecification clusterSpec, ResourceNode node) {
		String resourceRole = node.getClusterSpecification().getResourceRole();
		if (resourceRole.equals(ResourceRole.MASTER.getName())) {
			String schedulingPolicy = node.getClusterSpecification().getSchedulingPolicy();
			if (schedulingPolicy.equals(SchedulingPolicy.ROUND_ROBIN.getName())) {
				clusterSpec.setActionSchedulingPolicy(this.resources.getRoundRobinSchedulingPolicy());
			}
		}
		
	}
	
}
