/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation, Cognos Incorporated and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.equinox.http.helper;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;

public class BundleEntryHttpContext implements HttpContext {
	private Bundle bundle;
	private String bundlePath;

	public BundleEntryHttpContext(Bundle bundle) {
		this.bundle = bundle;
	}

	public BundleEntryHttpContext(Bundle b, String bundlePath) {
		this(b);
		if (bundlePath != null) {
			if (bundlePath.endsWith("/")) //$NON-NLS-1$
				bundlePath = bundlePath.substring(0, bundlePath.length() - 1);

			if (bundlePath.length() == 0)
				bundlePath = null;
		}
		this.bundlePath = bundlePath;
	}

	public String getMimeType(String arg0) {
		return null;
	}

	public boolean handleSecurity(HttpServletRequest arg0, HttpServletResponse arg1) throws IOException {
		return true;
	}

	public URL getResource(String resourceName) {
		if (bundlePath != null)
			resourceName = bundlePath + resourceName;
		
		int lastSlash = resourceName.lastIndexOf('/');
		if (lastSlash == -1)
			return null;
		
		String path = resourceName.substring(0, lastSlash);
		if (path.length() == 0)
			path = "/"; //$NON-NLS-1$
		String file = sanitizeEntryName(resourceName.substring(lastSlash + 1));
		Enumeration entryPaths = bundle.findEntries(path, file, false);
		
		if (entryPaths != null && entryPaths.hasMoreElements())
			return (URL) entryPaths.nextElement();
		
		return null;
	}

	private String sanitizeEntryName(String name) {
		StringBuffer buffer = null;
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			switch (c) {
			case '*':
			case '\\':
				// we need to escape '*' and '\'
				if (buffer == null) {
					buffer = new StringBuffer(name.length() + 16);
					buffer.append(name.substring(0, i));
				}
				buffer.append('\\').append(c);
				break;
			default:
				if (buffer != null)
					buffer.append(c);
				break;
			}
		}
		return (buffer == null) ? name : buffer.toString();
	}

	public Set getResourcePaths(String path) {
		if (bundlePath != null)
			path = bundlePath + path;
		
		Enumeration entryPaths = bundle.findEntries(path, null, false);
		if (entryPaths == null)
			return null;

		Set result = new HashSet();
		while (entryPaths.hasMoreElements()) {
			URL entryURL = (URL) entryPaths.nextElement();
			String entryPath = entryURL.getFile();

			if (bundlePath == null)	
				result.add(entryPath);
			else
				result.add(entryPath.substring(bundlePath.length()));
		}
		return result;
	}
}