/*******************************************************************************
 * Copyright (c) 2007 Cognos Incorporated, IBM Corporation and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.equinox.http.helper;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResourceServlet implements Servlet {

	private static final String LAST_MODIFIED = "Last-Modified"; //$NON-NLS-1$
	private static final String IF_MODIFIED_SINCE = "If-Modified-Since"; //$NON-NLS-1$
	private static final String IF_NONE_MATCH = "If-None-Match"; //$NON-NLS-1$
	private static final String ETAG = "ETag"; //$NON-NLS-1$

	private String internalName;
	private ServletConfig config;

	public ResourceServlet() {
		this.internalName = ""; //$NON-NLS-1$
	}
	
	public ResourceServlet(String internalName) {
		this.internalName = internalName;
		if (internalName == null || internalName.equals("/")) { //$NON-NLS-1$
			this.internalName = ""; //$NON-NLS-1$
		}
	}

	public void init(ServletConfig config) throws ServletException {
		this.config = config;
		String baseName = config.getInitParameter("base-name"); //$NON-NLS-1$
		if (baseName != null) {
			if (baseName.equals("/")) //$NON-NLS-1$
				internalName = ""; //$NON-NLS-1$
			else
				internalName = baseName;
		}		
	}

	public void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
		HttpServletRequest httpRequest = (HttpServletRequest) req;
		HttpServletResponse httpResponse = (HttpServletResponse) resp;
	
		String method = httpRequest.getMethod();
		if (method.equals("GET") || method.equals("POST") || method.equals("HEAD")) { //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			String resourcePath = internalName + httpRequest.getPathInfo();
			if (!writeResource(httpRequest, httpResponse, resourcePath)) {
				httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} else
			httpResponse.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	public void destroy() {
		config = null;
	}

	public ServletConfig getServletConfig() {
		return config;
	}

	public String getServletInfo() {
		return ""; //$NON-NLS-1$
	}
	
	private boolean writeResource(final HttpServletRequest req, final HttpServletResponse resp, final String resourcePath) throws IOException {
		ServletContext servletContext = config.getServletContext();
		URL url = servletContext.getResource(resourcePath);
		if (url == null)
			return false;

		URLConnection connection = url.openConnection();
		long lastModified = connection.getLastModified();
		int contentLength = connection.getContentLength();

		String etag = null;
		if (lastModified != -1 && contentLength != -1)
			etag = "W/\"" + contentLength + "-" + lastModified + "\""; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

		// Check for cache revalidation.
		// We should prefer ETag validation as the guarantees are stronger and all HTTP 1.1 clients should be using it
		String ifNoneMatch = req.getHeader(IF_NONE_MATCH);
		if (ifNoneMatch != null && etag != null && ifNoneMatch.indexOf(etag) != -1) {
			resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return true;
		} else {
			long ifModifiedSince = req.getDateHeader(IF_MODIFIED_SINCE);
			// for purposes of comparison we add 999 to ifModifiedSince since the fidelity
			// of the IMS header generally doesn't include milli-seconds
			if (ifModifiedSince > -1 && lastModified > 0 && lastModified <= (ifModifiedSince + 999)) {
				resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				return true;
			}
		}

		// return the full contents regularly
		if (contentLength != -1)
			resp.setContentLength(contentLength);

		String contentType = servletContext.getMimeType(resourcePath);
		if (contentType == null)
			contentType = servletContext.getMimeType(resourcePath);

		if (contentType != null)
			resp.setContentType(contentType);

		if (lastModified > 0)
			resp.setDateHeader(LAST_MODIFIED, lastModified);

		if (etag != null)
			resp.setHeader(ETAG, etag);

		InputStream is = null;
		try {
			is = connection.getInputStream();
			OutputStream os = resp.getOutputStream();
			byte[] buffer = new byte[8192];
			int bytesRead = is.read(buffer);
			int writtenContentLength = 0;
			while (bytesRead != -1) {
				os.write(buffer, 0, bytesRead);
				writtenContentLength += bytesRead;
				bytesRead = is.read(buffer);
			}
			if (contentLength == -1 || contentLength != writtenContentLength)
				resp.setContentLength(writtenContentLength);
		} finally {
			if (is != null)
				is.close();
		}
		return true;
	}
}
