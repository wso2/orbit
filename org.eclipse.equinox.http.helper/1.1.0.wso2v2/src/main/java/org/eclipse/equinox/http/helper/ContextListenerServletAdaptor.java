/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.equinox.http.helper;

import java.io.IOException;
import javax.servlet.*;

public class ContextListenerServletAdaptor implements Servlet {
	private ServletConfig config;
	private ServletContextListener listener;
	private Servlet delegate;

	public ContextListenerServletAdaptor(ServletContextListener listener, Servlet delegate) {
		this.listener = listener;
		this.delegate = delegate;
	}

	public void init(ServletConfig config) throws ServletException {
		this.config = config;
		listener.contextInitialized(new ServletContextEvent(config.getServletContext()));
		delegate.init(config);
	}

	public void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
		delegate.service(req, resp);
	}

	public void destroy() {
		delegate.destroy();
		listener.contextDestroyed(new ServletContextEvent(config.getServletContext()));
		config = null;
	}

	public ServletConfig getServletConfig() {
		return config;
	}

	public String getServletInfo() {
		return ""; //$NON-NLS-1$
	}
}
