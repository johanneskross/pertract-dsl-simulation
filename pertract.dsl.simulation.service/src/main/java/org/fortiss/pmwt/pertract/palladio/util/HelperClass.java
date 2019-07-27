/*******************************************************************************
 * Copyright (C) 2018 fortiss GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     voegele - initial implementation
 ******************************************************************************/
package org.fortiss.pmwt.pertract.palladio.util;

public class HelperClass {

	public final static String ENTRYLEVELSYSTEMCALL = "EntryLevelSystemCall";
	public final static String USAGESCENARIO = "UsageScenario";
	public final static double PROCESSINGRATENORMALIZER = 1000;

	public static String getComponentName(final String sensorName) {
		String responseTimeType = sensorName.replaceFirst("Response Time of ",
				"");
		if (responseTimeType.startsWith("Call_")) {
			responseTimeType = ENTRYLEVELSYSTEMCALL;
		} else if (responseTimeType.startsWith("Call ")) {
			responseTimeType = responseTimeType.replaceFirst("Call ", "");
			int firstPoint = responseTimeType.indexOf(".");
			responseTimeType = responseTimeType.substring(0, firstPoint);
		} else {
			responseTimeType = USAGESCENARIO;
		}
		return responseTimeType;
	}

	public static String getMethodName(final String sensorName) {
		String responseTimeType = sensorName.replaceFirst("Response Time of ",
				"");
		if (responseTimeType.startsWith("Call_")) {
			responseTimeType = responseTimeType.replaceFirst("Call_", "");
			int firstEmptySpace = responseTimeType.indexOf(" ");
			responseTimeType = responseTimeType.substring(0,
					firstEmptySpace - 1);

			boolean isDigit = true;
			while (isDigit) {
				String lastChar = responseTimeType.substring(
						responseTimeType.length() - 1,
						responseTimeType.length());
				if (lastChar.matches("\\d*")) {
					responseTimeType = responseTimeType.substring(0,
							responseTimeType.length() - 1);
				} else {
					isDigit = false;
				}
			}

		} else if (responseTimeType.startsWith("Call ")) {
			responseTimeType = responseTimeType.replaceFirst("Call ", "");
			int firstPoint = responseTimeType.indexOf(".");
			int firstEmptySpace = responseTimeType.indexOf(" ", 1);
			responseTimeType = responseTimeType.substring(firstPoint + 1,
					firstEmptySpace - 1);
		} else {
			int firstPoint = responseTimeType.indexOf(".");
			responseTimeType = responseTimeType.substring(0, firstPoint);
		}
		return responseTimeType;
	}

	public static String getResourceType(final String sensorName) {
		int firstInt = sensorName.indexOf("[") + 1;
		int secondInt = sensorName.indexOf("]");
		if (firstInt > 0 && secondInt > 0) {
			return sensorName.substring(firstInt, secondInt);
		}
		return "";
	}

	public static String getResourceContainerName(final String sensorName) {
		String resourceContainerName = sensorName.replaceFirst(
				"Demanded time at ", "");
		int firstInt = resourceContainerName.indexOf(" ");
		return resourceContainerName.substring(0, firstInt);
	}
	
}
