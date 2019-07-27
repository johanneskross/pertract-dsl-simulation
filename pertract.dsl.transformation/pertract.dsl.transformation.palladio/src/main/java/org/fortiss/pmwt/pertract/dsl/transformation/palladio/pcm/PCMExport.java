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
package org.fortiss.pmwt.pertract.dsl.transformation.palladio.pcm;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uka.ipd.sdq.pcm.allocation.Allocation;
import de.uka.ipd.sdq.pcm.repository.Repository;
import de.uka.ipd.sdq.pcm.resourceenvironment.ResourceEnvironment;
import de.uka.ipd.sdq.pcm.system.System;
import de.uka.ipd.sdq.pcm.usagemodel.UsageModel;

public class PCMExport {
	
	private final Logger log = LoggerFactory.getLogger(PCMExport.class);
	
	public byte[] zipModels(Repository repository, System system, ResourceEnvironment resourceEnvironment,
			Allocation allocation, UsageModel usage) {
		
		Map<String,byte[]> files = new HashMap<>();
		files.put("default.repository", this.storeRepositoryModelInMemory(repository));
		files.put("default.system", this.storeSystemModelInMemory(system));
		files.put("default.resourceenvironment", this.storeResourceEnvironmentInMemory(resourceEnvironment));
		files.put("default.allocation", this.storeAllocationModelInMemory(allocation));
		files.put("default.usagemodel", this.storeUsageModelInMemory(usage));
		return this.createZipFile(files);
	}
	
	public void saveModels(Repository repository, System system, ResourceEnvironment resourceEnvironment,
			Allocation allocation, UsageModel usage, String outputFolder) {
		byte[] repositoryByteArray = storeRepositoryModelInMemory(repository);
		byte[] systemByteArray = storeSystemModelInMemory(system);
		byte[] resourceByteArray = storeResourceEnvironmentInMemory(resourceEnvironment);
		byte[] allocationByteArray = storeAllocationModelInMemory(allocation);
		byte[] usageByteArray = storeUsageModelInMemory(usage);
		
		try {
			FileOutputStream fos;
			fos = new FileOutputStream(outputFolder + "default.repository");
			fos.write(repositoryByteArray);
			fos.close();
			fos = new FileOutputStream(outputFolder + "default.system");
			fos.write(systemByteArray);
			fos.close();
			fos = new FileOutputStream(outputFolder + "default.resourceenvironment");
			fos.write(resourceByteArray);
			fos.close();
			fos = new FileOutputStream(outputFolder + "default.allocation");
			fos.write(allocationByteArray);
			fos.close();
			fos = new FileOutputStream(outputFolder + "default.usagemodel");
			fos.write(usageByteArray);
			fos.close();
			log.info("Saved PCM models into " + outputFolder);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void saveRepositoryModelOnDisk(Repository repository, String outputFolder) {
		byte[] repositoryByteArray = storeRepositoryModelInMemory(repository);
		
		try {
			FileOutputStream fos;
			fos = new FileOutputStream(outputFolder + "default.repository");
			fos.write(repositoryByteArray);
			fos.close();
			log.info("Saved PCM repository model into " + outputFolder);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public byte[] storeRepositoryModelInMemory(Repository repository) {
		try {
			de.uka.ipd.sdq.pcm.repository.RepositoryPackage.eINSTANCE.eClass();
			de.uka.ipd.sdq.pcm.seff.SeffPackage.eINSTANCE.eClass();
			org.eclipse.emf.ecore.resource.Resource.Factory.Registry reg = org.eclipse.emf.ecore.resource.Resource.Factory.Registry.INSTANCE;
			Map<String, Object> m = reg.getExtensionToFactoryMap();
			m.put("repository", new XMIResourceFactoryImpl());
			ResourceSet resourceSet = new ResourceSetImpl();
			org.eclipse.emf.ecore.resource.Resource resource = resourceSet.createResource(URI.createURI("default.repository"));
			resource.getContents().add(repository);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			resource.save(outputStream, null);
			return outputStream.toByteArray();
		} catch (IOException e) {
			log.info("Exception", e);
			return new byte[]{};
		}
	}
	
	public byte[] storeSystemModelInMemory(System system) {
		try {
			de.uka.ipd.sdq.pcm.system.SystemPackage.eINSTANCE.eClass();
			de.uka.ipd.sdq.pcm.core.composition.CompositionPackage.eINSTANCE.eClass();
			org.eclipse.emf.ecore.resource.Resource.Factory.Registry reg = org.eclipse.emf.ecore.resource.Resource.Factory.Registry.INSTANCE;
			Map<String, Object> m = reg.getExtensionToFactoryMap();
			m.put("system", new XMIResourceFactoryImpl());
			ResourceSet resourceSet = new ResourceSetImpl();
			org.eclipse.emf.ecore.resource.Resource resource = resourceSet.createResource(URI.createURI("default.system"));
			resource.getContents().add(system);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			resource.save(outputStream, null);
			return outputStream.toByteArray();
		} catch (IOException e) {
			log.info("Exception", e);
			return new byte[]{};
		}
	}
	
	public byte[] storeResourceEnvironmentInMemory(ResourceEnvironment resourceEnvironment) {
		try {
			de.uka.ipd.sdq.pcm.resourceenvironment.ResourceenvironmentPackage.eINSTANCE.eClass();
			de.uka.ipd.sdq.pcm.resourcetype.ResourcetypePackage.eINSTANCE.eClass();
			org.eclipse.emf.ecore.resource.Resource.Factory.Registry reg = org.eclipse.emf.ecore.resource.Resource.Factory.Registry.INSTANCE;
			Map<String, Object> m = reg.getExtensionToFactoryMap();
			m.put("resourceenvironment", new XMIResourceFactoryImpl());
			ResourceSet resourceSet = new ResourceSetImpl();
			org.eclipse.emf.ecore.resource.Resource resource = resourceSet.createResource(URI.createURI("default.resourceenvironment"));
			resource.getContents().add(resourceEnvironment);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			resource.save(outputStream, null);
			return outputStream.toByteArray();
		} catch (IOException e) {
			log.info("Exception", e);
			return new byte[]{};
		}
	}
	
	public byte[] storeAllocationModelInMemory(Allocation allocation) {
		try {
			de.uka.ipd.sdq.pcm.allocation.AllocationPackage.eINSTANCE.eClass();
			org.eclipse.emf.ecore.resource.Resource.Factory.Registry reg = org.eclipse.emf.ecore.resource.Resource.Factory.Registry.INSTANCE;
			Map<String, Object> m = reg.getExtensionToFactoryMap();
			m.put("allocation", new XMIResourceFactoryImpl());
			ResourceSet resourceSet = new ResourceSetImpl();
			org.eclipse.emf.ecore.resource.Resource resource = resourceSet.createResource(URI.createURI("default.allocation"));
			resource.getContents().add(allocation);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			resource.save(outputStream, null);
			return outputStream.toByteArray();
		} catch (IOException e) {
			log.info("Exception", e);
			return new byte[]{};
		}
	}
	
	public byte[] storeUsageModelInMemory(UsageModel usage) {
		try {
			de.uka.ipd.sdq.pcm.usagemodel.UsagemodelPackage.eINSTANCE.eClass();
			org.eclipse.emf.ecore.resource.Resource.Factory.Registry reg = org.eclipse.emf.ecore.resource.Resource.Factory.Registry.INSTANCE;
			Map<String, Object> m = reg.getExtensionToFactoryMap();
			m.put("usagemodel", new XMIResourceFactoryImpl());
			ResourceSet resourceSet = new ResourceSetImpl();
			org.eclipse.emf.ecore.resource.Resource resource = resourceSet.createResource(URI.createURI("default.usagemodel"));
			resource.getContents().add(usage);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			resource.save(outputStream, null);
			return outputStream.toByteArray();
		} catch (IOException e) {
			log.info("Exception", e);
			return new byte[]{};
		}
	}

	public byte[] createZipFile(String filename, byte[] file) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(bos);
			ZipEntry entry = new ZipEntry(filename);
			entry.setSize(file.length);
			zos.putNextEntry(entry);
			zos.write(file);
			zos.closeEntry();
			zos.close();
			return bos.toByteArray();
		} catch (IOException e) {
			log.info("Exception", e);
			return new byte[]{};
		}
	}
	
	private byte[] createZipFile(Map<String,byte[]> files) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(bos);
			
			for (Map.Entry<String, byte[]> entry : files.entrySet()) {
				String filename = entry.getKey();
				byte[] file = entry.getValue();
				
				ZipEntry zipEntry = new ZipEntry(filename);
				zipEntry.setSize(file.length);
				zos.putNextEntry(zipEntry);
				zos.write(file);
			}
			zos.closeEntry();
			zos.close();
			return bos.toByteArray();
		} catch (IOException e) {
			log.info("Exception", e);
			return new byte[]{};
		}
	}

}
