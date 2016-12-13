/*******************************************************************************
 * Copyright (c) 2005, 2015 Cognos Incorporated, IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Cognos Incorporated - initial API and implementation
 *     IBM Corporation - bug fixes and enhancements
 *     Raymond Augé <raymond.auge@liferay.com> - Bug 436698
 *******************************************************************************/
package org.eclipse.equinox.http.servlet.internal.context;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import javax.servlet.ServletContext;
import org.eclipse.equinox.http.servlet.internal.util.Const;

/**
 * The ProxyContext provides something similar to a ServletContext for all servlets and resources under a particular ProxyServlet.
 * In particular it holds and represent the concept of "context path" through the Proxy Servlets servlet path.
 * The Http Service also requires a ServletContext namespaced by each individual HttpContext. The ProxyContext provides support for the
 * attribute map of a ServletContext again namespaced by HttpContext as specified in the Http Service specification. The ContextAttributes
 * are reference counted so that when the HttpContext is no longer referenced the associated context attributes can be
 * garbage collected and the context temp dir deleteted.
 */
public class ProxyContext {
	private static final String JAVAX_SERVLET_CONTEXT_TEMPDIR = "javax.servlet.context.tempdir"; //$NON-NLS-1$

	private final ConcurrentMap<ContextController, ContextAttributes> attributesMap =
		new ConcurrentHashMap<ContextController, ContextAttributes>();
	File proxyContextTempDir;
	private ServletContext servletContext;

	public ProxyContext(ServletContext servletContext) {
		this.servletContext = servletContext;
		File tempDir = (File) servletContext.getAttribute(JAVAX_SERVLET_CONTEXT_TEMPDIR);
		if (tempDir != null) {
			proxyContextTempDir = new File(tempDir, "proxytemp"); //$NON-NLS-1$
			deleteDirectory(proxyContextTempDir);
			proxyContextTempDir.mkdirs();
		}
	}

	public void destroy() {
		if (proxyContextTempDir != null)
			deleteDirectory(proxyContextTempDir);
	}

	public String getServletPath() {
		return Const.BLANK;
	}

	public void createContextAttributes(
		ContextController controller) {

		attributesMap.compute(
			controller,
			new BiFunction<ContextController, ContextAttributes, ContextAttributes>() {

				@Override
				public ContextAttributes apply(
					ContextController contextController,
					ContextAttributes contextAttributes) {

					if (contextAttributes == null) {
						contextAttributes = new ContextAttributes(
							contextController);
					}

					contextAttributes.addReference();

					return contextAttributes;
				}

			});
	}

	public void destroyContextAttributes(
		ContextController controller) {

		attributesMap.compute(
			controller,
			new BiFunction<ContextController, ContextAttributes, ContextAttributes>() {

				@Override
				public ContextAttributes apply(
					ContextController contextController,
					ContextAttributes contextAttributes) {

					if (contextAttributes == null) {
						throw new IllegalStateException("too many calls");
					}

					if (contextAttributes.removeReference() == 0) {
						contextAttributes.destroy();

						return null;
					}

					return contextAttributes;
				}

			});
	}

	public Dictionary<String, Object> getContextAttributes(
		ContextController controller) {

		return attributesMap.get(controller);
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * deleteDirectory is a convenience method to recursively delete a directory
	 * @param directory - the directory to delete.
	 * @return was the delete succesful
	 */
	protected static boolean deleteDirectory(File directory) {
		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return directory.delete();
	}

	public class ContextAttributes extends Hashtable<String, Object> {
		private static final long serialVersionUID = 1916670423277243587L;
		private int referenceCount;

		public ContextAttributes(ContextController controller) {
			if (proxyContextTempDir != null) {
				File contextTempDir = new File(
					proxyContextTempDir,
					"hc_" + controller.hashCode()); //$NON-NLS-1$

				contextTempDir.mkdirs();

				put(JAVAX_SERVLET_CONTEXT_TEMPDIR, contextTempDir);
			}
		}

		public void destroy() {
			File contextTempDir = (File) get(JAVAX_SERVLET_CONTEXT_TEMPDIR);
			if (contextTempDir != null)
				deleteDirectory(contextTempDir);
		}

		public void addReference() {
			referenceCount++;
		}

		public void removeReference() {
			referenceCount--;
		}

		public int referenceCount() {
			return referenceCount;
		}
	}

}
