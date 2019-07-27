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
package org.fortiss.pmwt.pertract.dsl.transformation.palladio.simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PCMSimulator {

	private final Logger log = LoggerFactory.getLogger(PCMSimulator.class);
	private String simulationResultsFolderName = "simresults";

	public PCMSimulator() {
		// empty constructur
	}

	public File simulate(String workspace, int simulationtime) {
		try {
			File simulationFolder = createOrGetSimulationFolder(workspace);
			startSimulation(simulationFolder, workspace, simulationtime);
			File resultsDirectory = getSimulationResultsDirectory(simulationFolder);
			return resultsDirectory;
		} catch (Exception e) {
			log.error("Could not simulate models ", e);
		}
		return null;
	}
	
	private File createOrGetSimulationFolder(String workspace) throws URISyntaxException, IOException {
		File clsFolder = new File(workspace, "cls");
		if (!clsFolder.exists()) {
			ClassLoader classLoader = getClass().getClassLoader();
			InputStream clsZip = classLoader.getResourceAsStream("cls.zip");
			unzip(clsZip, workspace);
			log.info("cls folder unzipped at " + workspace);
		} else {
			log.info("cls folder already exists at " + workspace);
		}
		return clsFolder;
	}
	
	private void startSimulation(File simulationFolder, String modelFolderName, int simulationtime) throws IOException, InterruptedException {
		String command;
		if (System.getProperty("os.name").startsWith("Windows")) {
			command = getWindowsSimulationCommand(simulationFolder, modelFolderName, simulationtime);
		} else {
			command = getUnixSimulationCommand(simulationFolder, modelFolderName, simulationtime);
		}
		String line;
		Process p = Runtime.getRuntime().exec(command);
		p.waitFor();
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		while ((line = in.readLine()) != null) {
			log.info(line);
		}
		in.close();
	}
	
	private String getWindowsSimulationCommand(File simulationFolder, String modelFolderName, int simulationtime) {
		String command = "cmd /C start /MIN "+ simulationFolder.getAbsolutePath().replace("/", "\\") + "\\simulate.bat" + " " 
				+ modelFolderName.replace("/", "\\") + "default.allocation" + " "
				+ modelFolderName.replace("/", "\\") + "default.usagemodel" + " "
				+ modelFolderName.replace("/", "\\") + " "
				+ simulationtime + " "
				+ 1000000000 + " "
				+ simulationResultsFolderName;
		log.info(command);
		return command;
	}
	
	private String getUnixSimulationCommand(File simulationFolder, String modelFolderName, int simulationtime) {
		String command = "sh "+ simulationFolder.getAbsolutePath() + "/simulate.sh" + " " 
				+ simulationFolder.getAbsolutePath() + " "
				+ modelFolderName + "default.allocation" + " "
				+ modelFolderName + "default.usagemodel" + " "
				+ simulationResultsFolderName + " "
				+ simulationtime + " "
				+ 1000000000 + " ";
		log.info(command);
		return command;
	}
	
	private File getSimulationResultsDirectory(File clsFolder) throws InterruptedException {
		File resultsDirectory = null;
		Path experimentFolder = Paths.get(clsFolder.getAbsolutePath() + "/" + simulationResultsFolderName);
		boolean found = false;
		while (!found) {
			for (File file : experimentFolder.toFile().listFiles()) {
				if (file.isDirectory()) {
					if (file.getName().startsWith("Run ")) {
						resultsDirectory = file;
						found = true;
						break;
					}
				}
			}
			Thread.sleep(1000);
			log.info("Simulation results are not ready yet ;-( " + experimentFolder);
		}
		return resultsDirectory;
	}
	

	/**
	 * 
	 * Thanks to
	 * https://sandstorm.de/de/blog/post/extract-zip-archives-from-embedded-resources-in-java-jar-files.html
	 * 
	 */
	public static void unzip(InputStream source, String target) throws IOException {
		final ZipInputStream zipStream = new ZipInputStream(source);
		ZipEntry nextEntry;
		while ((nextEntry = zipStream.getNextEntry()) != null) {
			final String name = nextEntry.getName();
			if (!name.endsWith("/")) {
				final File nextFile = new File(target, name);
				final File parent = nextFile.getParentFile();
				if (parent != null) {
					parent.mkdirs();
				}
				try (OutputStream targetStream = new FileOutputStream(nextFile)) {
					copy(zipStream, targetStream);
				}
			}
		}
	}

	/**
	 * 
	 * Thanks to
	 * https://sandstorm.de/de/blog/post/extract-zip-archives-from-embedded-resources-in-java-jar-files.html
	 * 
	 */
	private static void copy(final InputStream source, final OutputStream target) throws IOException {
		final int bufferSize = 4 * 1024;
		final byte[] buffer = new byte[bufferSize];

		int nextCount;
		while ((nextCount = source.read(buffer)) >= 0) {
			target.write(buffer, 0, nextCount);
		}
	}

}
