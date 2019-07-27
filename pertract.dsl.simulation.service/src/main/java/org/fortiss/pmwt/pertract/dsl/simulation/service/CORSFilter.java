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
package org.fortiss.pmwt.pertract.dsl.simulation.service;


import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CORSFilter implements ContainerResponseFilter {
    public void filter(ContainerRequestContext paramContainerRequestContext, ContainerResponseContext paramContainerResponseContext) throws IOException {
        paramContainerResponseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        paramContainerResponseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        paramContainerResponseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        paramContainerResponseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        paramContainerResponseContext.getHeaders().add("Access-Control-Max-Age", "1209600");
    }
}
