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

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.eclipse.emf.common.util.EList;
import org.fortiss.pmwt.pertract.dsl.model.application.ApplicationExecutionArchitecture;
import org.fortiss.pmwt.pertract.dsl.model.application.DirectedEdge;
import org.fortiss.pmwt.pertract.dsl.model.application.ExecutionNode;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.DataModel;
import org.fortiss.pmwt.pertract.dsl.model.dataworkload.FileDataModel;
import org.fortiss.pmwt.pertract.dsl.transformation.palladio.pcm.repository.seff.SeffGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uka.ipd.sdq.pcm.core.CoreFactory;
import de.uka.ipd.sdq.pcm.core.PCMRandomVariable;
import de.uka.ipd.sdq.pcm.repository.BasicComponent;
import de.uka.ipd.sdq.pcm.repository.InfrastructureInterface;
import de.uka.ipd.sdq.pcm.repository.InfrastructureRequiredRole;
import de.uka.ipd.sdq.pcm.repository.InfrastructureSignature;
import de.uka.ipd.sdq.pcm.repository.OperationInterface;
import de.uka.ipd.sdq.pcm.repository.OperationRequiredRole;
import de.uka.ipd.sdq.pcm.repository.OperationSignature;
import de.uka.ipd.sdq.pcm.repository.PassiveResource;
import de.uka.ipd.sdq.pcm.repository.Repository;
import de.uka.ipd.sdq.pcm.repository.RepositoryFactory;

public class RepositoryGenerator {
	
	private RepositoryEntityArchetypeProvider entityProvider;
	private RepositoryRoleArchetypeProvider roleProvider;
	private SignatureGenerator signatureGenerator;
	private SeffGenerator seffGen;
	
	private Repository repository;
	private BasicComponent appComponent;
	private InfrastructureInterface appResourcesInterface;
	private InfrastructureSignature allocSignature;
	private InfrastructureSignature freeSignature;
	private OperationInterface appInterface;
	private OperationSignature appSignature;
	private BasicComponent appResourcesComponent;
	
	private final Logger log = LoggerFactory.getLogger(RepositoryGenerator.class);
	
	public RepositoryGenerator() {
		this.entityProvider = new RepositoryEntityArchetypeProvider();
		this.roleProvider = new RepositoryRoleArchetypeProvider();
	}
	
	public Repository generateRepository(ApplicationExecutionArchitecture applicationModel, EList<DataModel> dataModels) {
		this.initializeSubGenerators(dataModels.get(0));
		this.repository = RepositoryFactory.eINSTANCE.createRepository();
		this.repository.setEntityName(applicationModel.getName());
		this.buildGeneralComponents(applicationModel);
		List<Triple<OperationSignature,OperationRequiredRole,Double>> referencesToNodeChildren = new ArrayList<>();
		referencesToNodeChildren = this.traverseNodes(applicationModel.getTopNode(), this.appComponent);
		seffGen.createDelegatingCompositeSeff(appComponent, appSignature, referencesToNodeChildren);
		this.repository.getComponents__Repository().add(appResourcesComponent);
		this.repository.getInterfaces__Repository().add(appResourcesInterface);
		this.repository.getComponents__Repository().add(appComponent);
		this.repository.getInterfaces__Repository().add(appInterface);
		return repository;
	}
	
	private void initializeSubGenerators(DataModel dataModel) {
		this.seffGen = new SeffGenerator(dataModel);
		if (dataModel instanceof FileDataModel) {
			this.signatureGenerator = new FileBasedSignatureGenerator();
		} else {
			this.signatureGenerator = new RecordBasedSignatureGenerator();
		}
	}
	
	/*
	 *  Depth-first search for (sub-)edges of the parent node/graph
	 */
	private List<Triple<OperationSignature,OperationRequiredRole,Double>> traverseNodes(ExecutionNode parentNode, BasicComponent parentComponent) {
		log.info("Traversing node " + parentNode.getName());
		Set<ExecutionNode> visitedNodes = new HashSet<>();
		List<Triple<OperationSignature,OperationRequiredRole,Double>> referencesToNodeChildren = new ArrayList<>();
		if (parentNode.getEdgesOfChildren().size()==0) {
			searchSubnodes(parentNode.getChildren().get(0), 1.0, referencesToNodeChildren, parentComponent);
		} else {
			for (DirectedEdge edge : parentNode.getEdgesOfChildren()) {
				if (!visitedNodes.contains(edge.getTail())) {
					searchSubnodes(edge.getTail(), 1.0, referencesToNodeChildren, parentComponent);
					visitedNodes.add(edge.getTail());
				}
				if (!visitedNodes.contains(edge.getHead())) {
					searchSubnodes(edge.getHead(), edge.getDataTransmissionFactor(), referencesToNodeChildren, parentComponent);
					visitedNodes.add(edge.getHead());
				}
			}
		}
		return referencesToNodeChildren;
	}
	
	/*
	 *  Iterating composites
	 */
	private void searchSubnodes(ExecutionNode node, double transmissionFactor, List<Triple<OperationSignature,OperationRequiredRole,Double>> referencesToNodeChildren, BasicComponent parentComponent) {
		if (isCompositeNode(node)){
			Entry<BasicComponent, OperationSignature> entry = handleCompositeNode(node, transmissionFactor, referencesToNodeChildren, parentComponent);
			List<Triple<OperationSignature,OperationRequiredRole,Double>> tmpReferencesToChilren = traverseNodes(node, entry.getKey());
			seffGen.createDelegatingCompositeSeff(entry.getKey(), entry.getValue(), tmpReferencesToChilren);
			
		} else if (isLeafNode(node)) {
			log.info("Handling leaf node " + node.getName());
			handleLeafNode(node, transmissionFactor, referencesToNodeChildren, parentComponent);
			
		} else {
			new RuntimeException("Something went wrong here - node children: " + node.getChildren().size() + "; node edges: " + node.getEdgesOfChildren().size());
		}
	}
	
	private boolean isCompositeNode(ExecutionNode node) {
		return node.getChildren().size() >= 1 && node.getEdgesOfChildren().size() >= 0;
	}
	
	private boolean isLeafNode(ExecutionNode node) {
		return node.getChildren().size() == 0 && node.getEdgesOfChildren().size() == 0;
	}
	
	/*
	 *  Creates and assemble general top-level application components
	 */
	private void buildGeneralComponents(ApplicationExecutionArchitecture applicationModel) {
		this.appComponent = entityProvider.createComponent(applicationModel.getFramework()+"Application");
		this.appInterface = entityProvider.createInterface("Application");
		this.appSignature = signatureGenerator.createDelegationOperationSignature();
		this.appInterface.getSignatures__OperationInterface().add(appSignature);
		roleProvider.addOperationProvidedRole(this.appComponent, this.appInterface);
		this.appResourcesComponent = entityProvider.createComponent(applicationModel.getFramework()+"Resources");
		int totalCores = applicationModel.getApplicationConfiguration().getTaskSlotsPerExecutor() * applicationModel.getApplicationConfiguration().getExecutors();
		this.addPassiveResource(this.appResourcesComponent, totalCores);
		this.appResourcesInterface = entityProvider.createInfrastructureInterface("Resources");
		this.freeSignature = signatureGenerator.createFreeOperationSignature();
		this.appResourcesInterface.getInfrastructureSignatures__InfrastructureInterface().add(this.freeSignature);
		this.allocSignature = signatureGenerator.createAllocOperationSignature();
		this.appResourcesInterface.getInfrastructureSignatures__InfrastructureInterface().add(this.allocSignature);
		roleProvider.addInfrastructureProvidedRole(appResourcesComponent, appResourcesInterface);
		seffGen.createAllocCoreSeff(appResourcesComponent, allocSignature);
		seffGen.addFreeCoreSeff(appResourcesComponent, freeSignature);
	}
	
	/*
	 *  Creates and assembles components for nodes that represent a graph / are composites (e.g., a Apache Spark job)
	 */
	private SimpleEntry<BasicComponent, OperationSignature> handleCompositeNode(ExecutionNode node, double transmissionFactor, List<Triple<OperationSignature,OperationRequiredRole,Double>> referencesToNodeChildren, BasicComponent parentComponent) {
		BasicComponent nodeComponent = entityProvider.createComponent(node.getName());
		OperationInterface nodeInterface = entityProvider.createInterface("I" + node.getName());
		OperationSignature nodeSignature = signatureGenerator.createDelegationOperationSignature();
		nodeInterface.getSignatures__OperationInterface().add(nodeSignature);
		roleProvider.addOperationProvidedRole(nodeComponent, nodeInterface);
		OperationRequiredRole requiredRole = roleProvider.addOperationRequiredRole(parentComponent, nodeInterface);
		this.repository.getComponents__Repository().add(nodeComponent);
		this.repository.getInterfaces__Repository().add(nodeInterface);
		referencesToNodeChildren.add(Triple.of(nodeSignature, requiredRole,transmissionFactor));
		return new AbstractMap.SimpleEntry<BasicComponent, OperationSignature>(nodeComponent, nodeSignature);
	}
	
	/*
	 *  Creates and assembles components for leaf nodes, which execute tasks and contain a resource profile (e.g., a Apache Spark stage)
	 */
	private void handleLeafNode(ExecutionNode node, double transmissionFactor, List<Triple<OperationSignature,OperationRequiredRole,Double>> referencesToNodeChildren, BasicComponent parentComponent) {
		BasicComponent nodeComponent = entityProvider.createComponent(node.getName());
		OperationInterface nodeInterface = entityProvider.createInterface("I" + node.getName());
		OperationSignature nodeSignature = signatureGenerator.createDelegationOperationSignature();
		nodeInterface.getSignatures__OperationInterface().add(nodeSignature);
		
		OperationSignature nodeExecutionSignature = signatureGenerator.createExecutionOperationSignature();
		nodeInterface.getSignatures__OperationInterface().add(nodeExecutionSignature);
		roleProvider.addOperationProvidedRole(nodeComponent, nodeInterface);
		
		InfrastructureRequiredRole appResourceRole = roleProvider.addInfrastructureRequiredRole(nodeComponent, appResourcesInterface);
		OperationRequiredRole parentRequiredRole = roleProvider.addOperationRequiredRole(parentComponent, nodeInterface);
		OperationRequiredRole itselfRequiredRole = roleProvider.addOperationRequiredRole(nodeComponent, nodeInterface);
		
		BasicComponent taskComponent = entityProvider.createComponent("taskFor" + node.getName());
		OperationInterface taskInterface = entityProvider.createInterface("ItaskFor" + node.getName());
		OperationSignature taskOperationSignature = signatureGenerator.createTaskOperationSignature();
		taskInterface.getSignatures__OperationInterface().add(taskOperationSignature);
		seffGen.createTaskSeff(taskComponent, taskOperationSignature, node.getResourceProfile());
		roleProvider.addOperationProvidedRole(taskComponent, taskInterface);
		OperationRequiredRole taskRole = roleProvider.addOperationRequiredRole(nodeComponent, taskInterface);

		seffGen.createDelegatingExecutionSeff(nodeComponent, nodeSignature, nodeExecutionSignature, itselfRequiredRole, node);
		seffGen.createExecutingSeff(nodeComponent, nodeExecutionSignature, appResourceRole, allocSignature, freeSignature, taskRole, taskOperationSignature, node);
		this.repository.getComponents__Repository().add(taskComponent);
		this.repository.getInterfaces__Repository().add(taskInterface);
		this.repository.getComponents__Repository().add(nodeComponent);
		this.repository.getInterfaces__Repository().add(nodeInterface);
		referencesToNodeChildren.add(Triple.of(nodeSignature, parentRequiredRole, transmissionFactor));
	}
	
	private void addPassiveResource(BasicComponent component, int cores) {
		PassiveResource coreResources = RepositoryFactory.eINSTANCE.createPassiveResource();
		coreResources.setEntityName("cores");
		coreResources.setBasicComponent_PassiveResource(component);
		PCMRandomVariable coreVariable = CoreFactory.eINSTANCE.createPCMRandomVariable();
		coreVariable.setSpecification(String.valueOf(cores));
		coreResources.setCapacity_PassiveResource(coreVariable);
		component.getPassiveResource_BasicComponent().add(coreResources);
	}

}
